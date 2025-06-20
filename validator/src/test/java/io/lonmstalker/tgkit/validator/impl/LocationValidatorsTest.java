package io.lonmstalker.tgkit.validator.impl;

import static io.lonmstalker.tgkit.validator.impl.LocationValidators.*;
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
