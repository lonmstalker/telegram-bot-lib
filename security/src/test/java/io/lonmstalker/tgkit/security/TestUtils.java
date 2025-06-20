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
