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
package io.lonmstalker.tgkit.security;

import java.lang.reflect.Field;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

public final class TestUtils {

  @SuppressWarnings("unchecked")
  public static <T> T extract(Object target, String field) {
    try {
      Field f = target.getClass().getDeclaredField(field);
      f.setAccessible(true);
      return (T) f.get(target);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static void setField(Object target, String field, Object value) {
    try {
      Field f = target.getClass().getDeclaredField(field);
      f.setAccessible(true);
      f.set(target, value);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static void setEnv(String key, String value) {
    try {
      var env = System.getenv();
      Field m = env.getClass().getDeclaredField("m");
      m.setAccessible(true);
      @SuppressWarnings("unchecked")
      var map = (java.util.Map<String, String>) m.get(env);
      if (value == null) map.remove(key); else map.put(key, value);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static Update message(long chat, long user) {
    Update u = new Update();
    Message m = new Message();
    Chat c = new Chat();
    c.setId(chat);
    User u1 = new User();
    u1.setId(user);
    m.setChat(c);
    m.setFrom(u1);
    u.setMessage(m);
    return u;
  }
}
