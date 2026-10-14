package si.intervue.engine.runners;

import java.lang.management.ManagementFactory;

/**
 * The heap a test run gets: a quarter of the machine's memory, between 512 MB and 4 GB, so a
 * runaway test swaps itself and not the laptop. Sessions and the content verify tool share it, so
 * verified timings match what candidates see.
 */
public final class HeapPolicy {

  static final int MIN_MB = 512;
  static final int MAX_MB = 4096;

  private HeapPolicy() {}

  public static int defaultMb() {
    var os =
        (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    return forTotalMemory(os.getTotalMemorySize());
  }

  static int forTotalMemory(long totalBytes) {
    long quarterMb = totalBytes / 4 / (1024 * 1024);
    return (int) Math.max(MIN_MB, Math.min(MAX_MB, quarterMb));
  }
}
