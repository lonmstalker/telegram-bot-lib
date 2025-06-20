package io.lonmstalker.tgkit.core.wizard;

import io.lonmstalker.tgkit.core.BotCommand;
import io.lonmstalker.tgkit.core.BotRequest;
import io.lonmstalker.tgkit.core.annotation.BotHandler;
import io.lonmstalker.tgkit.core.bot.BotCommandRegistry;
import io.lonmstalker.tgkit.core.loader.BotCommandFactory;
import io.lonmstalker.tgkit.core.state.StateStore;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Реализация {@link WizardEngine}, повторно использующая BotCommand-механизм для навигационных
 * shortcut’ов (&laquo;back&raquo;, &laquo;skip&raquo;, &laquo;cancel&raquo; и др.).
 *
 * <p>Алгоритм:
 *
 * <ol>
 *   <li>При регистрации Wizard создаём CommandAdapter-ы<br>
 *       для всех сгенерированных handler-методов навигации.
 *   <li>Эти виртуальные методы прогоняются через все {@link BotCommandFactory} точно так же, как
 *       обычные команды приложения.
 *   <li>В {@link #route(BotRequest)} сначала ищем BotCommand (shortcut), затем — выполняем текущий
 *       step по sessionId.
 * </ol>
 */
@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class WizardEngineImpl implements WizardEngine {

  private final @NonNull BotCommandRegistry commandRegistry;
  private final @NonNull StateStore stateStore;
  private final @NonNull WizardStepRunner stepRunner;
  private final List<BotCommandFactory<?>> factories =
      ServiceLoader.load(BotCommandFactory.class).stream()
          .map(ServiceLoader.Provider::get)
          .toList();

  /** wizardId → Wizard<?> */
  private final Map<String, Wizard<?>> wizardMap = new HashMap<>();

  @Override
  public void register(@NonNull Wizard<?> wizard) {
    wizardMap.put(wizard.getId(), wizard);
    registerShortcutsAsCommands(wizard);
    log.info("Wizard [{}] registered, steps={}", wizard.getId(), wizard.getSteps().size());
  }

  /** Каждый «короткий» переход оформляем как команду /callback. */
  private void registerShortcutsAsCommands(Wizard<?> wizard) {
    Class<?> dynClass = DynamicShortcutHolder.generate(wizard.meta().id());
    Object instance = DynamicShortcutHolder.newInstance(dynClass);

    // накладываем factories вручную (динамический класс не сканируется Reflections)
    for (Method m : dynClass.getDeclaredMethods()) {
      if (!m.isAnnotationPresent(BotHandler.class)) continue;
      InternalCommandAdapter.Builder b = DynamicShortcutHolder.toBuilder(m, instance);
      applyFactories(b, m);
      commandRegistry.add(b.build());
    }
  }

  @SuppressWarnings("unchecked")
  private void applyFactories(InternalCommandAdapter.Builder b, Method m) {
    for (BotCommandFactory<?> f : factories) {
      Annotation a = m.getAnnotation(f.annotationType());
      if (a != null) ((BotCommandFactory<Annotation>) f).apply(b, m, a);
    }
  }

  @Override
  public @NonNull String start(@NonNull String wizardId, @NonNull BotRequest<?> req) {
    Wizard<?> w = wizardMap.get(wizardId);
    if (w == null) throw new IllegalArgumentException("Wizard not found: " + wizardId);
    String sid = UUID.randomUUID().toString();
    stateStore.newSession(req.chatId(), sid, wizardId, Instant.now());
    stepRunner.askStep(wizardId, sid, w.steps().get(0), req);
    return sid;
  }

  @Override
  public void route(@NonNull BotRequest<?> req) {
    // 1. shortcut?
    Optional<BotCommand<?>> cmd = commandRegistry.findCommand(req);
    if (cmd.isPresent()) {
      cmd.get().handle(req);
      return;
    }

    // 2. обычный step
    String sid = extractSessionId(req.update());
    SessionState st =
        stateStore
            .find(sid)
            .orElseThrow(() -> new IllegalStateException("Session not found: " + sid));
    Wizard<?> w = wizardMap.get(st.wizardId());
    stepRunner.handleAnswer(w, st, req);
  }

  @Override
  public void resume(@NonNull BotRequest<?> req) {
    stateStore
        .findActiveByUser(req.chatId())
        .ifPresent(
            st -> {
              Wizard<?> w = wizardMap.get(st.wizardId());
              stepRunner.askStep(w.meta().id(), st.sessionId(), w.steps().get(st.stepId()), req);
            });
  }

  private static @NonNull String extractSessionId(@NonNull Update u) {
    if (u.hasCallbackQuery()) {
      CallbackQuery cb = u.getCallbackQuery();
      String[] p = cb.getData().split(":");
      if (p.length > 2 && "W".equals(p[0])) return p[1];
    }
    throw new IllegalArgumentException("No sessionId in update");
  }
}
