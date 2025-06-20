package io.lonmstalker.tgkit.webhook;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.lonmstalker.tgkit.core.config.BotGlobalConfig;
import io.lonmstalker.tgkit.core.bot.WebHookReceiver;
import io.lonmstalker.observability.MetricsCollector;
import io.lonmstalker.tgkit.observability.Tags;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import java.util.concurrent.atomic.AtomicInteger;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Минималистичный HTTP-сервер для Telegram WebHook.
 *
 * <p>Поддерживает Jetty и Netty. Сервер проверяет секретный токен и добавляет заголовок HSTS во все
 * ответы.
 *
 * <p>Пример инициализации и регистрации бота:
 *
 * <pre>{@code
 * BotGlobalConfig.INSTANCE.webhook()
 *     .port(8080)
 *     .secret("SECRET")
 *     .engine(WebhookServer.Engine.JETTY);
 * BotCoreInitializer.init();
 * Bot bot = BotFactory.INSTANCE.from("TOKEN", config, adapter, new SetWebhook());
 * }</pre>
 */
public final class WebhookServer implements AutoCloseable {
  private static final Logger log = LoggerFactory.getLogger(WebhookServer.class);
  private static final String SECRET_HEADER = "X-Telegram-Bot-Api-Secret-Token";
  private static final String HSTS_VALUE = "max-age=31536000; includeSubDomains";

  /** Варианты реализации HTTP-сервера. */
  public enum Engine {
    JETTY,
    NETTY
  }

  private final ServerEngine engine;
  private final ObjectMapper mapper = BotGlobalConfig.INSTANCE.http().getMapper();
  private final String secret;
  private final MetricsCollector metrics = BotGlobalConfig.INSTANCE.observability().getCollector();
  private final Counter dropped = metrics.counter("updates_dropped_total", Tags.of());
  private final AtomicInteger queueGauge = new AtomicInteger();

  public WebhookServer(int port, @NonNull String secretToken, @NonNull Engine engine) {
    this.secret = secretToken;
    this.engine = engine == Engine.JETTY ? new JettyEngine(port) : new NettyEngine(port);
    Gauge.builder("updates_queue_size", queueGauge, AtomicInteger::get).register(metrics.registry());
  }

  /** Запуск сервера. */
  public void start() throws Exception {
    engine.start();
    log.info("Webhook server started on port {}", port());
  }

  /**
   * Регистрирует нового получателя обновлений.
   *
   * @param receiver бот-получатель
   */
  public void register(@NonNull WebHookReceiver receiver) {
    engine.register(receiver);
  }

  /**
   * @return фактический порт сервера.
   */
  public int port() {
    return engine.port();
  }

  @Override
  public void close() throws Exception {
    engine.close();
  }

  private static class HstsFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
      if (response instanceof HttpServletResponse resp) {
        resp.setHeader("Strict-Transport-Security", HSTS_VALUE);
      }
      chain.doFilter(request, response);
    }
  }

  private interface ServerEngine extends AutoCloseable {
    void start() throws Exception;

    void register(@NonNull WebHookReceiver receiver);

    int port();
  }

  private final class JettyEngine implements ServerEngine {
    private final Server server;
    private final Map<String, WebHookReceiver> receivers = new ConcurrentHashMap<>();

    JettyEngine(int port) {
      this.server = new Server(new InetSocketAddress("localhost", port));
      ServletContextHandler context = new ServletContextHandler();
      context.addServlet(new ServletHolder(new UpdateServlet()), "/*");
      context.addFilter(
          new FilterHolder(new HstsFilter()), "/*", EnumSet.of(DispatcherType.REQUEST));
      server.setHandler(context);
    }

    @Override
    public void start() throws Exception {
      server.start();
    }

    @Override
    public void register(@NonNull WebHookReceiver receiver) {
      receivers.put("/" + receiver.getBotPath(), receiver);
    }

    @Override
    public int port() {
      return ((ServerConnector) server.getConnectors()[0]).getLocalPort();
    }

    @Override
    public void close() throws Exception {
      server.stop();
    }

    private class UpdateServlet extends HttpServlet {
      @Override
      protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        queueGauge.incrementAndGet();
        String header = req.getHeader(SECRET_HEADER);
        if (!secret.equals(header)) {
          resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
          dropped.increment();
          queueGauge.decrementAndGet();
          return;
        }
        WebHookReceiver receiver = receivers.get(req.getRequestURI());
        if (receiver == null) {
          resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
          dropped.increment();
          queueGauge.decrementAndGet();
          return;
        }
        Update update;
        try {
          update = mapper.readValue(req.getInputStream(), Update.class);
        } catch (IOException e) {
          resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
          dropped.increment();
          queueGauge.decrementAndGet();
          return;
        }
        try {
          BotApiMethod<?> method = receiver.onWebhookUpdateReceived(update);
          if (method != null) {
            receiver.execute(method);
          }
        } catch (Exception e) {
          log.error("Webhook handling failed", e);
          resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
          dropped.increment();
          queueGauge.decrementAndGet();
          return;
        }
        resp.setStatus(HttpServletResponse.SC_OK);
        queueGauge.decrementAndGet();
      }
    }
  }

  private final class NettyEngine implements ServerEngine {
    private final EventLoopGroup boss = new NioEventLoopGroup(1);
    private final EventLoopGroup worker = new NioEventLoopGroup();
    private final int port;
    private Channel serverChannel;
    private final Map<String, WebHookReceiver> receivers = new ConcurrentHashMap<>();

    NettyEngine(int port) {
      this.port = port;
    }

    @Override
    public void start() throws InterruptedException {
      ServerBootstrap b =
          new ServerBootstrap()
              .group(boss, worker)
              .channel(NioServerSocketChannel.class)
              .childHandler(
                  new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                      ch.pipeline().addLast(new HttpServerCodec());
                      ch.pipeline().addLast(new NettyHandler());
                    }
                  })
              .childOption(ChannelOption.SO_KEEPALIVE, true);
      ChannelFuture f = b.bind(new InetSocketAddress("localhost", port)).sync();
      serverChannel = f.channel();
    }

    @Override
    public void register(@NonNull WebHookReceiver receiver) {
      receivers.put("/" + receiver.getBotPath(), receiver);
    }

    @Override
    public int port() {
      return ((InetSocketAddress) serverChannel.localAddress()).getPort();
    }

    @Override
    public void close() {
      if (serverChannel != null) {
        serverChannel.close();
      }
      boss.shutdownGracefully();
      worker.shutdownGracefully();
    }

    private final class NettyHandler extends SimpleChannelInboundHandler<HttpObject> {
      private HttpRequest request;
      private final ByteBuf body = Unpooled.buffer();

      @Override
      protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        if (msg instanceof HttpRequest httpRequest) {
          this.request = httpRequest;
        }
        if (msg instanceof HttpContent content) {
          body.writeBytes(content.content());
          if (content instanceof LastHttpContent) {
            handle(ctx);
          }
        }
      }

      private void handle(ChannelHandlerContext ctx) throws IOException {
        queueGauge.incrementAndGet();
        HttpHeaders headers = request.headers();
        if (!HttpMethod.POST.equals(request.method())
            || !secret.equals(headers.get(SECRET_HEADER))) {
          send(ctx, HttpResponseStatus.UNAUTHORIZED);
          dropped.increment();
          queueGauge.decrementAndGet();
          return;
        }
        URI uri = URI.create(request.uri());
        WebHookReceiver receiver = receivers.get(uri.getPath());
        if (receiver == null) {
          send(ctx, HttpResponseStatus.NOT_FOUND);
          dropped.increment();
          queueGauge.decrementAndGet();
          return;
        }
        Update update;
        try {
          update = mapper.readValue(new ByteBufInputStream(body, true), Update.class);
        } catch (IOException e) {
          send(ctx, HttpResponseStatus.BAD_REQUEST);
          dropped.increment();
          queueGauge.decrementAndGet();
          return;
        }
        try {
          BotApiMethod<?> method = receiver.onWebhookUpdateReceived(update);
          if (method != null) {
            receiver.execute(method);
          }
        } catch (Exception e) {
          log.error("Webhook handling failed", e);
          send(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR);
          dropped.increment();
          queueGauge.decrementAndGet();
          return;
        }
        send(ctx, HttpResponseStatus.OK);
        queueGauge.decrementAndGet();
      }

      private void send(ChannelHandlerContext ctx, HttpResponseStatus status) {
        DefaultFullHttpResponse resp =
            new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, Unpooled.EMPTY_BUFFER);
        resp.headers().set(HttpHeaderNames.CONTENT_LENGTH, 0);
        resp.headers().set("Strict-Transport-Security", HSTS_VALUE);
        ctx.writeAndFlush(resp).addListener(f -> ctx.close());
      }
    }
  }
}
