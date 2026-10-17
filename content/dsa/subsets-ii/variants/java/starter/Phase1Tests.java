import static kit.Check.assertEquals;
import static kit.Check.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import kit.Test;

public class Phase1Tests {

    /** Sorts each subset, then the list, so any order is accepted; repeats stay visible. */
    private static String normal(List<List<Integer>> subsets) {
        List<String> parts = new ArrayList<>();
        for (List<Integer> s : subsets) {
            List<Integer> sorted = new ArrayList<>(s);
            sorted.sort(null);
            parts.add(sorted.toString());
        }
        parts.sort(null);
        return parts.toString();
    }

    private static void expect(int[] nums, int[][] want) {
        List<List<Integer>> wanted = new ArrayList<>();
        for (int[] w : want) {
            List<Integer> s = new ArrayList<>();
            for (int v : w) {
                s.add(v);
            }
            wanted.add(s);
        }
        assertEquals(normal(wanted), normal(SubsetsWithRepeats.all(nums)));
    }

    private static Map<Integer, Integer> counts(Iterable<Integer> values) {
        Map<Integer, Integer> counts = new HashMap<>();
        for (int v : values) {
            counts.merge(v, 1, Integer::sum);
        }
        return counts;
    }

    /**
     * A distinct subset takes 0..c copies of each value present c times, so there are the product of
     * (c + 1) of them; that many different subsets, none taking more copies than nums has, are all.
     */
    private static void expectAll(int[] nums) {
        List<Integer> values = new ArrayList<>();
        for (int v : nums) {
            values.add(v);
        }
        Map<Integer, Integer> available = counts(values);
        long want = 1;
        for (int c : available.values()) {
            want *= c + 1;
        }
        List<List<Integer>> got = SubsetsWithRepeats.all(nums);
        assertEquals(want, (long) got.size(), "number of distinct subsets");
        Set<String> seen = new HashSet<>();
        for (List<Integer> s : got) {
            for (Map.Entry<Integer, Integer> e : counts(s).entrySet()) {
                assertTrue(
                        e.getValue() <= available.getOrDefault(e.getKey(), 0),
                        "subset " + s + " takes more copies of " + e.getKey() + " than nums has");
            }
            List<Integer> sorted = new ArrayList<>(s);
            sorted.sort(null);
            assertTrue(seen.add(sorted.toString()), "subset " + sorted + " is listed twice");
        }
    }

    @Test(order = 1)
    void sample() {
        expect(new int[] {3, 1, 3}, new int[][] {{}, {1}, {3}, {1, 3}, {3, 3}, {1, 3, 3}});
    }

    @Test(order = 2)
    void twoRepeatedValues() {
        expect(
                new int[] {2, 7, 2, 7},
                new int[][] {{}, {2}, {7}, {2, 2}, {2, 7}, {7, 7}, {2, 2, 7}, {2, 7, 7}, {2, 2, 7, 7}});
    }

    @Test(order = 3)
    void onlyOneValueRepeated() {
        expect(new int[] {-8, -8}, new int[][] {{}, {-8}, {-8, -8}});
    }

    @Test(order = 4)
    void allValuesEqual() {
        expect(new int[] {5, 5, 5, 5}, new int[][] {{}, {5}, {5, 5}, {5, 5, 5}, {5, 5, 5, 5}});
    }

    @Test(order = 5)
    void noRepeats() {
        expect(new int[] {-4, 9}, new int[][] {{}, {-4}, {9}, {-4, 9}});
    }

    @Test(order = 6)
    void limits() {
        expect(new int[] {10, -10, 10}, new int[][] {{}, {10}, {-10}, {10, 10}, {-10, 10}, {-10, 10, 10}});
    }

    @Test(order = 7)
    void repeatsNotSideBySide() {
        expectAll(new int[] {4, -1, 4, 6, -1, 4, 0, 6});
    }

    @Test(order = 8, timeoutMs = 400)
    void twentyFourValuesThreeKinds() {
        int[] nums = new int[24];
        for (int i = 0; i < 24; i++) {
            nums[i] = (i % 3) * 7 - 7; // -7, 0 and 7, eight of each: 9 * 9 * 9 = 729 subsets
        }
        Random random = new Random(70);
        for (int i = nums.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int swap = nums[i];
            nums[i] = nums[j];
            nums[j] = swap;
        }
        expectAll(nums);
    }
}
