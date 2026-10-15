import static kit.Check.assertEquals;

import java.util.Arrays;
import java.util.Random;
import kit.Test;

public class Phase1Tests {

    private static void check(int[][] expected, int[][] ranges) {
        assertEquals(Arrays.deepToString(expected), Arrays.deepToString(Schedule.mergeRanges(ranges)));
    }

    @Test(order = 1)
    void sample() {
        check(new int[][] {{2, 8}, {14, 17}}, new int[][] {{14, 17}, {2, 5}, {4, 8}});
    }

    @Test(order = 2)
    void touchingRangesMerge() {
        check(new int[][] {{1, 9}}, new int[][] {{6, 9}, {1, 6}});
    }

    @Test(order = 3)
    void emptyInput() {
        check(new int[][] {}, new int[][] {});
    }

    @Test(order = 4)
    void singleRange() {
        check(new int[][] {{3, 4}}, new int[][] {{3, 4}});
    }

    @Test(order = 5)
    void oneRangeContainsTheOthers() {
        check(new int[][] {{1, 10}}, new int[][] {{1, 10}, {3, 4}, {5, 6}});
        check(new int[][] {{1, 10}}, new int[][] {{3, 4}, {5, 6}, {1, 10}});
    }

    @Test(order = 6)
    void unsortedChainMergesIntoOne() {
        check(new int[][] {{1, 9}}, new int[][] {{7, 9}, {1, 3}, {3, 5}, {5, 7}});
    }

    @Test(order = 7)
    void separateRangesComeOutSorted() {
        check(new int[][] {{1, 2}, {10, 11}, {20, 21}}, new int[][] {{20, 21}, {1, 2}, {10, 11}});
    }

    @Test(order = 8)
    void identicalRanges() {
        check(new int[][] {{4, 5}}, new int[][] {{4, 5}, {4, 5}});
    }

    @Test(order = 9)
    void sameStartDifferentEnd() {
        check(new int[][] {{2, 9}, {12, 13}}, new int[][] {{2, 9}, {2, 4}, {12, 13}});
        check(new int[][] {{2, 9}, {12, 13}}, new int[][] {{12, 13}, {2, 4}, {2, 9}});
    }

    @Test(order = 10, timeoutMs = 1000)
    void manySeparateRangesInShuffledOrder() {
        int n = 100_000;
        int[][] ranges = new int[n][];
        for (int i = 0; i < n; i++) {
            ranges[i] = new int[] {3 * i, 3 * i + 1}; // a gap of one minute after each range
        }
        Random random = new Random(11);
        for (int i = n - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int[] swap = ranges[i];
            ranges[i] = ranges[j];
            ranges[j] = swap;
        }
        int[][] merged = Schedule.mergeRanges(ranges);
        assertEquals(n, merged.length);
        for (int i = 0; i < n; i++) {
            assertEquals(3 * i, merged[i][0]);
            assertEquals(3 * i + 1, merged[i][1]);
        }
    }
}
