package io.github.tgkit.flag.test;

import io.github.tgkit.internal.config.BotGlobalConfig;
import io.github.tgkit.internal.dsl.feature_flags.FeatureFlags;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

/** JUnit extension that installs {@link FlagOverrideRegistry}. */
public final class FlagOverrideExtension
    implements BeforeEachCallback, AfterEachCallback, ParameterResolver {
  private static final ExtensionContext.Namespace NS =
      ExtensionContext.Namespace.create(FlagOverrideExtension.class);
  private static final String KEY = "registry";

  @Override
  public void beforeEach(ExtensionContext context) {
    FeatureFlags original = BotGlobalConfig.INSTANCE.dsl().getFeatureFlags();
    FlagOverrideRegistry registry = new FlagOverrideRegistry(original);
    BotGlobalConfig.INSTANCE.dsl().featureFlags(registry);
    context.getStore(NS).put(KEY, registry);
  }

  @Override
  public void afterEach(ExtensionContext context) {
    FlagOverrideRegistry registry = context.getStore(NS).remove(KEY, FlagOverrideRegistry.class);
    if (registry != null) {
      BotGlobalConfig.INSTANCE.dsl().featureFlags(registry.original());
    }
  }

  @Override
  public boolean supportsParameter(ParameterContext pc, ExtensionContext ec) {
    Class<?> type = pc.getParameter().getType();
    return type == Flags.class || type == FlagOverrideRegistry.class;
  }

  @Override
  public Object resolveParameter(ParameterContext pc, ExtensionContext ec) {
    return ec.getStore(NS).get(KEY, FlagOverrideRegistry.class);
  }
}
