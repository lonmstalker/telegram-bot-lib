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

import static io.github.tgkit.validator.impl.LocationValidators.*;
import static org.junit.jupiter.api.Assertions.*;

import io.lonmstalker.tgkit.core.exception.ValidationException;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.Location;

class LocationValidatorsTest {

  private Location loc(double lat, double lon) {
    Location l = new Location();
    l.setLatitude(lat);
    l.setLongitude(lon);
    return l;
  }

  @Test
  void inBounds_acceptsValid() {
    assertDoesNotThrow(() -> inBounds().validate(loc(0, 0)));
  }

  @Test
  void inBounds_rejectsInvalid() {
    ValidationException ex =
        assertThrows(ValidationException.class, () -> inBounds().validate(loc(100, 0)));
    assertEquals("error.location.bounds", ex.getErrorKey().key());
  }
}
