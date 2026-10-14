package si.intervue.engine.health;

import java.util.List;

/** One Doctor run; {@code ready} is false when any blocking check failed. */
public record DoctorReport(boolean ready, List<CheckResult> results) {}
