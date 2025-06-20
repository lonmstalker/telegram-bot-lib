package io.lonmstalker.tgkit.security.captcha.provider;

import static java.util.Collections.*;

import io.lonmstalker.tgkit.core.BotRequest;
import io.lonmstalker.tgkit.core.dsl.Button;
import io.lonmstalker.tgkit.core.exception.BotApiException;
import io.lonmstalker.tgkit.security.captcha.CaptchaProvider;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import javax.imageio.ImageIO;
import lombok.SneakyThrows;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;

public final class SliderCaptchaProvider implements CaptchaProvider {
  private static final SecureRandom RND = new SecureRandom();
  private final Map<Long, Integer> answers = synchronizedMap(new WeakHashMap<>());

  private final String backgroundPath;

  public SliderCaptchaProvider(@NonNull String backgroundPath) {
    this.backgroundPath = backgroundPath;
  }

  @Override
  public @NonNull SendPhoto question(@NonNull BotRequest<?> req) {
    try {
      int offset = RND.nextInt(140) + 30; // 30..170 px
      int answer = offset;
      answers.put(req.user().chatId(), answer);

      BufferedImage bg =
          ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(backgroundPath)));
      BufferedImage piece = cutPiece(bg, offset);

      /* Merge: прозрачный пазл    */
      Graphics2D g = bg.createGraphics();
      g.setComposite(AlphaComposite.SrcOver.derive(0.5f));
      g.drawImage(piece, offset, 50, null);
      g.dispose();

      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ImageIO.write(bg, "png", baos);
      InputFile photo = new InputFile(new ByteArrayInputStream(baos.toByteArray()), "puzzle.png");

      return req.photo(photo)
          .keyboard(kb -> kb.row(Button.cb("◀", "L"), Button.cb("▶", "R")))
          .disableNotif()
          .build();
    } catch (IOException e) {
      throw new BotApiException(e);
    }
  }

  @Override
  public boolean verify(@NonNull BotRequest<?> r, @NonNull String data) {
    /* data = "pos:<x>" от callback */
    int pos = Integer.parseInt(data.substring(4));
    Integer expect = answers.remove(r.user().chatId());
    return expect != null && Math.abs(expect - pos) <= 5;
  }

  /* — helpers — */
  @SneakyThrows
  private static BufferedImage cutPiece(BufferedImage src, int x) {
    int w = 60, h = 60, y = 50;
    BufferedImage dst = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = dst.createGraphics();
    g.setClip(new RoundRectangle2D.Double(0, 0, w, h, 12, 12));
    g.drawImage(src, -x, -y, null);
    g.dispose();
    return dst;
  }
}
