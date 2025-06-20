package io.lonmstalker.tgkit.security.rbac;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import io.lonmstalker.tgkit.core.BotRequest;
import io.lonmstalker.tgkit.core.init.BotCoreInitializer;
import io.lonmstalker.tgkit.core.interceptor.BotInterceptor;
import io.lonmstalker.tgkit.core.loader.BotCommandFactory;
import io.lonmstalker.tgkit.security.init.BotSecurityInitializer;
import java.lang.reflect.Method;
import java.util.ServiceLoader;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.objects.Update;

public class RoleSecurityTest {

  /* ======= dummy handler ======= */
  @RequiresRole("ADMIN")
  void secure(Update u) {}

  Method handler;

  static {
    BotCoreInitializer.init();
    BotSecurityInitializer.init();
  }

  @BeforeEach
  void init() throws Exception {
    handler = getClass().getDeclaredMethod("secure", Update.class);
  }

  /* -------------------------------------------------------------- */

  @Test
  void serviceLoaderDetectsFactory() {
    boolean found =
        ServiceLoader.load(BotCommandFactory.class).stream()
            .map(ServiceLoader.Provider::get)
            .anyMatch(f -> f instanceof RoleBotCommandFactory);

    assertThat(found).isTrue();
  }

  @Test
  void interceptorAllowsWhenRoleMatches() {
    RoleBotCommandFactory f = new RoleBotCommandFactory();
    RequiresRole ann = handler.getAnnotationsByType(RequiresRole.class)[0];
    BotInterceptor rbac = f.build(handler, ann).orElseThrow();

    var request = Mockito.mock(BotRequest.class, Mockito.RETURNS_DEEP_STUBS);
    when(request.user()).thenReturn(new SimpleUser(Set.of("ADMIN")));

    // no exception
    rbac.preHandle(Mockito.mock(), request);
  }

  @Test
  void interceptorDeniesWhenNoRole() {
    RoleBotCommandFactory f = new RoleBotCommandFactory();
    RequiresRole ann = handler.getAnnotationsByType(RequiresRole.class)[0];
    BotInterceptor rbac = f.build(handler, ann).orElseThrow();

    var request = Mockito.mock(BotRequest.class, Mockito.RETURNS_DEEP_STUBS);
    when(request.user()).thenReturn(new SimpleUser(Set.of("USER")));

    assertThatThrownBy(() -> rbac.preHandle(Mockito.mock(), request))
        .isInstanceOf(ForbiddenException.class);
  }
}
