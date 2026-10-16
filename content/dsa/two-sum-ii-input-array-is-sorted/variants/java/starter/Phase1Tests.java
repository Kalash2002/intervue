import static kit.Check.assertEquals;

import java.util.Arrays;
import kit.Test;

public class Phase1Tests {

    private static void expect(int[] numbers, int target, int i, int j) {
        int[] got = SortedPair.find(numbers, target);
        assertEquals(i + "," + j, got.length == 2 ? got[0] + "," + got[1] : Arrays.toString(got));
    }

    @Test(order = 1)
    void sample() {
        expect(new int[] {-4, 2, 3, 9, 12, 17}, 21, 3, 4);
    }

    @Test(order = 2)
    void twoElements() {
        expect(new int[] {3, 9}, 12, 0, 1);
    }

    @Test(order = 3)
    void equalValuesFormThePair() {
        expect(new int[] {1, 4, 4, 9}, 8, 1, 2);
    }

    @Test(order = 4)
    void negativeValues() {
        expect(new int[] {-8, -3, 0, 2, 6}, -3, 1, 2);
    }

    @Test(order = 5)
    void pairAtTheTwoEnds() {
        expect(new int[] {1, 3, 5, 7, 20}, 21, 0, 4);
    }

    @Test(order = 6)
    void pairAtTheEnd() {
        expect(new int[] {1, 3, 5, 7, 9}, 16, 3, 4);
    }

    @Test(order = 7)
    void pairOfZeros() {
        expect(new int[] {-5, 0, 0, 6}, 0, 1, 2);
    }

    @Test(order = 8)
    void aPositionDoesNotPairWithItself() {
        expect(new int[] {3, 5, 7}, 10, 0, 2);
    }

    @Test(order = 9, timeoutMs = 1000)
    void largePairAtTheEnd() {
        int n = 200_000;
        int[] numbers = new int[n];
        for (int i = 0; i < n; i++) {
            numbers[i] = i; // the largest possible sum, n - 2 + n - 1, belongs to one pair only
        }
        expect(numbers, 2 * n - 3, n - 2, n - 1);
    }
}
