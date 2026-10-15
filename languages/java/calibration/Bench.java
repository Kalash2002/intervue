import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * The calibration workload (PLAN.md 4.2): sort 2 million longs, then 1 million HashMap inserts and
 * lookups. Prints its own milliseconds on the last line, so JVM start-up is not measured.
 */
public class Bench {
  public static void main(String[] args) {
    long start = System.nanoTime();
    Random random = new Random(42);
    long[] values = new long[2_000_000];
    for (int i = 0; i < values.length; i++) {
      values[i] = random.nextLong();
    }
    Arrays.sort(values);
    Map<Integer, Integer> map = new HashMap<>();
    for (int i = 0; i < 1_000_000; i++) {
      map.put(i * 31, i);
    }
    long sum = values[values.length / 2];
    for (int i = 0; i < 1_000_000; i++) {
      sum += map.get(i * 31);
    }
    long ms = (System.nanoTime() - start) / 1_000_000;
    // The checksum goes to stderr so the JIT cannot drop the work; stdout carries only the time.
    System.err.println("checksum " + sum);
    System.out.println(ms);
  }
}
