import static kit.Check.assertEquals;
import static kit.Check.assertTrue;

import java.util.ArrayList;
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
        assertEquals(normal(wanted), normal(CombinationSum.find(candidates, target)));
    }

    @Test(order = 1)
    void sample() {
        expect(new int[] {3, 5, 4}, 11, new int[][] {{3, 4, 4}, {3, 3, 5}});
    }

    @Test(order = 2)
    void valuesUsedDifferentNumbersOfTimes() {
        expect(new int[] {6, 2, 9}, 12, new int[][] {{6, 6}, {2, 2, 2, 6}, {2, 2, 2, 2, 2, 2}});
    }

    @Test(order = 3)
    void everyCandidateTooLarge() {
        expect(new int[] {8, 12}, 5, new int[][] {});
    }

    @Test(order = 4)
    void oneValueUsedSeveralTimes() {
        expect(new int[] {7}, 21, new int[][] {{7, 7, 7}});
    }

    @Test(order = 5)
    void targetIsItselfACandidate() {
        expect(new int[] {5, 10}, 10, new int[][] {{5, 5}, {10}});
    }

    @Test(order = 6)
    void orderingsAreOneCombination() {
        expect(new int[] {2, 3}, 10, new int[][] {{2, 2, 2, 2, 2}, {2, 2, 3, 3}});
    }

    @Test(order = 7)
    void largerValueFirst() {
        expect(new int[] {9, 2}, 4, new int[][] {{2, 2}});
        expect(new int[] {13, 4, 11}, 12, new int[][] {{4, 4, 4}});
    }

    @Test(order = 8)
    void limits() {
        expect(new int[] {40, 2}, 40, new int[][] {{40}, {2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2}});
        expect(new int[] {2, 3}, 1, new int[][] {});
    }

    @Test(order = 9, timeoutMs = 2000)
    void thirtyCandidates() {
        int[] candidates = new int[30];
        for (int i = 0; i < 30; i++) {
            candidates[i] = i + 2; // 2..31
        }
        Random random = new Random(68);
        for (int i = candidates.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int swap = candidates[i];
            candidates[i] = candidates[j];
            candidates[j] = swap;
        }
        int target = 40;
        // ways[s] counts the combinations that reach s: the standard coin-counting table
        long[] ways = new long[target + 1];
        ways[0] = 1;
        for (int c : candidates) {
            for (int s = c; s <= target; s++) {
                ways[s] += ways[s - c];
            }
        }
        Set<Integer> allowed = new HashSet<>();
        for (int c : candidates) {
            allowed.add(c);
        }
        List<List<Integer>> got = CombinationSum.find(candidates, target);
        Set<String> seen = new HashSet<>();
        for (List<Integer> c : got) {
            int sum = 0;
            for (int v : c) {
                assertTrue(allowed.contains(v), "combination " + c + " uses a value not in candidates");
                sum += v;
            }
            assertEquals(target, sum, "combination " + c + " does not add up to the target");
            List<Integer> sorted = new ArrayList<>(c);
            sorted.sort(null);
            assertTrue(seen.add(sorted.toString()), "combination " + sorted + " is listed twice");
        }
        // every listed combination is valid and distinct, so the right count means none is missing
        assertEquals(ways[target], (long) got.size(), "number of combinations");
    }
}
