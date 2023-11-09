package io.flamingock.core.util;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * Extending `TimeUtil` to avoid hard coding instances(Instant.now, LocalDateTime.now(), etc.)
 */
public class TimeService {

  /**
   * @param millis milliseconds to add to the Date
   * @return current date plus milliseconds passed as parameter
   */
  public LocalDateTime currentDatePlusMillis(long millis) {
    return LocalDateTime.now().plus(millis, ChronoUnit.MILLIS);
  }

  /**
   * @return current Date
   */
  @Deprecated
  public Date currentTimeOld() {
    return new Date(System.currentTimeMillis());
  }


  public LocalDateTime currentDateTime() {
    return LocalDateTime.now();
  }

  public long currentMillis() {
    return Instant.now().toEpochMilli();
  }




  private Instant nowInstant() {
    return Instant.now(Clock.systemDefaultZone());
  }

  public Instant nowPlusMillis(long millis) {
    return nowInstant().plusMillis(millis);
  }

  public boolean isPast(Instant moment) {
    return nowInstant().isAfter(moment);
  }

  public boolean isPast(LocalDateTime dateTime) {
    return currentDateTime().isAfter(dateTime);
  }
}
