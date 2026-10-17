import static kit.Check.assertEquals;
import static kit.Check.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import kit.Test;

public class Phase1Tests {

    /** Sorts each combination, then the list, so any order is accepted; repeats stay visible. */
    private static String normal(List<List<Integer>> combinations) {
        List<String> parts = new ArrayList<>();
        for (List<Integer> c : combinations) {
            List<Integer> sorted = new ArrayList<>(c);
            sorted.sort(null);
            parts.add(sorted.toString());
        }
        parts.sort(null);
        return parts.toString();
    }

    private static void expect(int[] candidates, int target, int[][] want) {
        List<List<Integer>> wanted = new ArrayList<>();
        for (int[] w : want) {
            List<Integer> c = new ArrayList<>();
            for (int v : w) {
                c.add(v);
            }
            wanted.add(c);
        }
        assertEquals(normal(wanted), normal(CombinationSumOnce.find(candidates, target)));
    }

    /**
     * Counts the distinct combinations with a table (each value taken 0..copies times), then checks
     * that every returned one is valid and listed once, so the right count means none is missing.
     */
    private static void expectAll(int[] candidates, int target) {
        int[] copies = new int[41]; // values are 1..40
        for (int v : candidates) {
            copies[v]++;
        }
        long[] ways = new long[target + 1];
        ways[0] = 1;
        for (int v = 1; v <= 40; v++) {
            for (int s = target; s >= 0; s--) { // downward, so ways[s - k * v] is still the old row
                for (int k = 1; k <= copies[v] && k * v <= s; k++) {
                    ways[s] += ways[s - k * v];
                }
            }
        }
        List<List<Integer>> got = CombinationSumOnce.find(candidates, target);
        Set<String> seen = new HashSet<>();
        for (List<Integer> c : got) {
            int[] used = new int[41];
            int sum = 0;
            for (int v : c) {
                assertTrue(v >= 1 && v <= 40 && ++used[v] <= copies[v], "combination " + c + " uses a value more often than candidates has it");
                sum += v;
            }
            assertEquals(target, sum, "combination " + c + " does not add up to the target");
            List<Integer> sorted = new ArrayList<>(c);
            sorted.sort(null);
            assertTrue(seen.add(sorted.toString()), "combination " + sorted + " is listed twice");
        }
        assertEquals(ways[target], (long) got.size(), "number of distinct combinations");
    }

    private static void shuffle(int[] values, long seed) {
        Random random = new Random(seed);
        for (int i = values.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int swap = values[i];
            values[i] = values[j];
            values[j] = swap;
        }
    }

    @Test(order = 1)
    void sample() {
        expect(new int[] {4, 2, 6, 2, 3}, 8, new int[][] {{2, 6}, {2, 2, 4}});
    }

    @Test(order = 2)
    void allValuesEqual() {
        expect(new int[] {5, 5, 5}, 10, new int[][] {{5, 5}});
    }

    @Test(order = 3)
    void noCombination() {
        expect(new int[] {10, 20}, 15, new int[][] {});
    }

    @Test(order = 4)
    void eachPositionAtMostOnce() {
        expect(new int[] {3}, 6, new int[][] {});
        expect(new int[] {3, 3}, 6, new int[][] {{3, 3}});
    }

    @Test(order = 5)
    void repeatsNotSideBySide() {
        expect(new int[] {1, 7, 1, 6, 1}, 8, new int[][] {{1, 7}, {1, 1, 6}});
    }

    @Test(order = 6)
    void limits() {
        expect(new int[] {40, 1, 32}, 32, new int[][] {{32}});
        expect(new int[] {16, 16, 16}, 32, new int[][] {{16, 16}});
        expect(new int[] {2, 1}, 1, new int[][] {{1}});
    }

    @Test(order = 7)
    void mixedSmall() {
        expectAll(new int[] {3, 1, 4, 1, 2, 3, 2, 5, 1, 4}, 9);
    }

    @Test(order = 8, timeoutMs = 1000)
    void eightyValuesEightKinds() {
        int[] candidates = new int[80];
        for (int i = 0; i < 80; i++) {
            candidates[i] = i % 8 + 1; // ten each of 1..8
        }
        shuffle(candidates, 71);
        expectAll(candidates, 32);
    }

    @Test(order = 9, timeoutMs = 1000)
    void eightyOnes() {
        int[] candidates = new int[80];
        Arrays.fill(candidates, 1);
        expectAll(candidates, 32); // one combination: thirty-two 1s
    }
}
