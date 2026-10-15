import static kit.Check.assertEquals;

import java.util.Arrays;
import kit.Test;

public class Phase1Tests {

    private static void check(int[][] expected, int[][] booked, int[] added) {
        int[][] before = Arrays.stream(booked).map(int[]::clone).toArray(int[][]::new);
        String actual = Arrays.deepToString(Bookings.insert(booked, added));
        assertEquals(Arrays.deepToString(expected), actual);
        assertEquals(Arrays.deepToString(before), Arrays.deepToString(booked), "the input list must not change");
    }

    @Test(order = 1)
    void sample() {
        check(new int[][] {{9, 16}, {18, 20}}, new int[][] {{9, 12}, {14, 16}, {18, 20}}, new int[] {11, 15});
    }

    @Test(order = 2)
    void touchingRangesMerge() {
        check(new int[][] {{9, 13}, {14, 16}, {18, 20}}, new int[][] {{9, 12}, {14, 16}, {18, 20}}, new int[] {12, 13});
    }

    @Test(order = 3)
    void emptyList() {
        check(new int[][] {{5, 6}}, new int[][] {}, new int[] {5, 6});
    }

    @Test(order = 4)
    void beforeEverything() {
        check(new int[][] {{1, 2}, {5, 6}, {8, 9}}, new int[][] {{5, 6}, {8, 9}}, new int[] {1, 2});
    }

    @Test(order = 5)
    void afterEverything() {
        check(new int[][] {{5, 6}, {8, 9}, {10, 12}}, new int[][] {{5, 6}, {8, 9}}, new int[] {10, 12});
    }

    @Test(order = 6)
    void fitsInAGap() {
        check(new int[][] {{1, 2}, {3, 4}, {6, 7}}, new int[][] {{1, 2}, {6, 7}}, new int[] {3, 4});
    }

    @Test(order = 7)
    void insideAnExistingRange() {
        check(new int[][] {{2, 8}}, new int[][] {{2, 8}}, new int[] {4, 5});
    }

    @Test(order = 8)
    void coversEverything() {
        check(new int[][] {{1, 10}}, new int[][] {{2, 3}, {5, 6}, {8, 9}}, new int[] {1, 10});
    }

    @Test(order = 9)
    void touchesTheFirstRangeFromTheLeft() {
        check(new int[][] {{2, 8}}, new int[][] {{5, 8}}, new int[] {2, 5});
    }

    @Test(order = 10)
    void singlePointRange() {
        check(new int[][] {{1, 3}, {7, 7}, {9, 10}}, new int[][] {{1, 3}, {9, 10}}, new int[] {7, 7});
    }
}
