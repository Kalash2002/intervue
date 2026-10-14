package si.intervue.engine.session;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;

/** A clock tests move by hand. */
final class TestClock extends Clock {
  private long millis = 1_800_000_000_000L;

  void advance(long ms) {
    millis += ms;
  }

  @Override
  public long millis() {
    return millis;
  }

  @Override
  public Instant instant() {
    return Instant.ofEpochMilli(millis);
  }

  @Override
  public ZoneId getZone() {
    return ZoneOffset.UTC;
  }

  @Override
  public Clock withZone(ZoneId zone) {
    return this;
  }
}
