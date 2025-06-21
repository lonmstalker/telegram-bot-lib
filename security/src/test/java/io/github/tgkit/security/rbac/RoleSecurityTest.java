/*
 * Copyright 2025 TgKit Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.tgkit.security.rbac;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import io.github.tgkit.internal.BotRequest;
import io.github.tgkit.internal.interceptor.BotInterceptor;
import io.github.tgkit.internal.loader.BotCommandFactory;
import io.github.tgkit.security.init.BotSecurityInitializer;
import io.github.tgkit.testkit.TestBotBootstrap;
import java.lang.reflect.Method;
import java.util.ServiceLoader;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.objects.Update;

public class RoleSecurityTest {

  static {
    TestBotBootstrap.initOnce();
    BotSecurityInitializer.init();
  }

  Method handler;

  /* ======= dummy handler ======= */
  @RequiresRole("ADMIN")
  void secure(Update u) {}

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
