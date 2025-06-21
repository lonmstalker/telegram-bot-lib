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
package io.github.tgkit.validator.impl;

import static io.github.tgkit.validator.impl.PollValidators.*;
import static org.junit.jupiter.api.Assertions.*;

import io.github.tgkit.api.exception.ValidationException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.polls.Poll;
import org.telegram.telegrambots.meta.api.objects.polls.PollOption;

class PollValidatorsTest {

  private PollOption opt(String text) {
    PollOption o = new PollOption();
    o.setText(text);
    return o;
  }

  private Poll poll(List<PollOption> opts) {
    Poll p = new Poll();
    p.setOptions(opts);
    return p;
  }

  @Test
  void optionsCount_acceptsValid() {
    assertDoesNotThrow(() -> optionsCount().validate(poll(List.of(opt("a"), opt("b")))));
  }

  @Test
  void optionsCount_rejectsTooFew() {
    ValidationException ex =
        assertThrows(
            ValidationException.class, () -> optionsCount().validate(poll(List.of(opt("a")))));
    assertEquals("error.poll.count", ex.getErrorKey().key());
  }

  @Test
  void optionTextLength_acceptsValid() {
    PollOption o = opt("x".repeat(100));
    assertDoesNotThrow(() -> optionTextLength().validate(poll(List.of(o, o))));
  }

  @Test
  void optionTextLength_rejectsTooLong() {
    PollOption o = opt("x".repeat(101));
    ValidationException ex =
        assertThrows(
            ValidationException.class, () -> optionTextLength().validate(poll(List.of(o, o))));
    assertEquals("error.poll.optionLength", ex.getErrorKey().key());
  }
}
