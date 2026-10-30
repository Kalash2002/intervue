import static kit.Check.assertEquals;
import static kit.Check.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
        assertEquals(normal(wanted), normal(Subsets.all(nums)));
    }

    /** 2^n different subsets, each made of distinct values of nums, are exactly all the subsets. */
    private static void expectAll(int[] nums) {
        List<List<Integer>> got = Subsets.all(nums);
        assertEquals(1L << nums.length, (long) got.size(), "number of subsets");
        Set<Integer> allowed = new HashSet<>();
        for (int v : nums) {
            allowed.add(v);
        }
        Set<String> seen = new HashSet<>();
        for (List<Integer> s : got) {
            assertTrue(allowed.containsAll(s), "subset " + s + " holds a value not in nums");
            assertEquals(s.size(), new HashSet<>(s).size(), "subset " + s + " repeats a value");
            List<Integer> sorted = new ArrayList<>(s);
            sorted.sort(null);
            assertTrue(seen.add(sorted.toString()), "subset " + sorted + " is listed twice");
        }
    }

    @Test(order = 1)
    void sample() {
        expect(new int[] {4, -2, 7}, new int[][] {{}, {4}, {-2}, {7}, {4, -2}, {4, 7}, {-2, 7}, {4, -2, 7}});
    }

    @Test(order = 2)
    void singleValue() {
        expect(new int[] {9}, new int[][] {{}, {9}});
    }

    @Test(order = 3)
    void emptyArrayHasTheEmptySubset() {
        expect(new int[] {}, new int[][] {{}});
    }

    @Test(order = 4)
    void valuesAtTheLimits() {
        expect(new int[] {100, -100}, new int[][] {{}, {100}, {-100}, {-100, 100}});
    }

    @Test(order = 5)
    void fourValues() {
        expect(
                new int[] {10, -5, 3, 0},
                new int[][] {
                    {}, {10}, {-5}, {3}, {0},
                    {10, -5}, {10, 3}, {10, 0}, {-5, 3}, {-5, 0}, {3, 0},
                    {10, -5, 3}, {10, -5, 0}, {10, 3, 0}, {-5, 3, 0},
                    {10, -5, 3, 0}
                });
    }

    @Test(order = 6)
    void sixValues() {
        expectAll(new int[] {13, -1, 44, 2, -60, 8});
    }

    @Test(order = 7, timeoutMs = 2000)
    void twelveValues() {
        Random random = new Random(67);
        Set<Integer> picked = new HashSet<>();
        while (picked.size() < 12) {
            picked.add(random.nextInt(201) - 100); // distinct values in -100..100
        }
        int[] nums = new int[12];
        int i = 0;
        for (int v : picked) {
            nums[i++] = v;
        }
        expectAll(nums);
    }
}
