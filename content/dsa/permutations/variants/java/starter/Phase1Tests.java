import static kit.Check.assertEquals;
import static kit.Check.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import kit.Test;

public class Phase1Tests {

    /** Sorts the list of orderings but not inside them: the order within an ordering is the answer. */
    private static String normal(List<List<Integer>> orderings) {
        List<String> parts = new ArrayList<>();
        for (List<Integer> p : orderings) {
            parts.add(p.toString());
        }
        parts.sort(null);
        return parts.toString();
    }

    private static void expect(int[] nums, int[][] want) {
        List<List<Integer>> wanted = new ArrayList<>();
        for (int[] w : want) {
            List<Integer> p = new ArrayList<>();
            for (int v : w) {
                p.add(v);
            }
            wanted.add(p);
        }
        assertEquals(normal(wanted), normal(Permutations.all(nums)));
    }

    /** n! different orderings, each holding exactly the values of nums, are exactly all of them. */
    private static void expectAll(int[] nums) {
        List<List<Integer>> got = Permutations.all(nums);
        long factorial = 1;
        for (int i = 2; i <= nums.length; i++) {
            factorial *= i;
        }
        assertEquals(factorial, (long) got.size(), "number of orderings");
        int[] values = nums.clone();
        Arrays.sort(values);
        String wantValues = Arrays.toString(values);
        Set<String> seen = new HashSet<>();
        for (List<Integer> p : got) {
            int[] sorted = new int[p.size()];
            for (int i = 0; i < sorted.length; i++) {
                sorted[i] = p.get(i);
            }
            Arrays.sort(sorted);
            assertEquals(wantValues, Arrays.toString(sorted), "ordering " + p + " does not hold each value once");
            assertTrue(seen.add(p.toString()), "ordering " + p + " is listed twice");
        }
    }

    @Test(order = 1)
    void sample() {
        expect(new int[] {5, 0, 2}, new int[][] {{5, 0, 2}, {5, 2, 0}, {0, 5, 2}, {0, 2, 5}, {2, 5, 0}, {2, 0, 5}});
    }

    @Test(order = 2)
    void twoValues() {
        expect(new int[] {-3, 8}, new int[][] {{-3, 8}, {8, -3}});
    }

    @Test(order = 3)
    void singleValue() {
        expect(new int[] {42}, new int[][] {{42}});
    }

    @Test(order = 4)
    void valuesAtTheLimits() {
        expect(new int[] {50, -50}, new int[][] {{50, -50}, {-50, 50}});
    }

    @Test(order = 5)
    void fourValues() {
        expectAll(new int[] {7, -1, 30, 4});
    }

    @Test(order = 6)
    void sixValues() {
        expectAll(new int[] {-9, 13, 0, 21, -40, 6});
    }

    @Test(order = 7, timeoutMs = 2000)
    void eightValues() {
        expectAll(new int[] {17, -25, 3, 44, -8, 0, 31, -50});
    }
}
