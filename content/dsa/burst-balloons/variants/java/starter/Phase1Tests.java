import static kit.Check.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import kit.Test;

public class Phase1Tests {

    private static void expect(int want, int[] values) {
        String input = values.length <= 12 ? Arrays.toString(values) : "a row of " + values.length + " balloons starting " + Arrays.toString(Arrays.copyOf(values, 6));
        assertEquals(want, Balloons.mostCoins(values.clone()), "values = " + input);
    }

    @Test(order = 1)
    void sample() {
        expect(133, new int[] {2, 3, 7, 4, 2});
        expect(42, new int[] {5, 0, 7});
        expect(9, new int[] {9});
    }

    @Test(order = 2)
    void theRowReversedPaysTheSame() {
        expect(133, new int[] {2, 4, 7, 3, 2});
        expect(42, new int[] {7, 0, 5});
    }

    @Test(order = 3)
    void neitherTheSmallestFirstNorTheBiggestPayFirstIsBest() {
        // burst 3, 5, 6, 2, 4 in that order; smallest first gets 128, the biggest pay each time 222 at best (220 on other ties)
        expect(240, new int[] {2, 6, 5, 3, 4});
        expect(240, new int[] {4, 3, 5, 6, 2});
    }

    @Test(order = 4)
    void zerosPayNothingButCanBeBurstFirst() {
        // with the zeros gone, the 6 pays 8 × 6 and the 8 pays 8
        expect(56, new int[] {0, 8, 0, 6, 0});
        expect(0, new int[] {0, 0, 0, 0, 0});
        expect(0, new int[] {0});
    }

    @Test(order = 5)
    void theLargestNumbers() {
        // the middle 100 pays 100^3, then 100 × 100, then 100
        expect(1_010_100, new int[] {100, 100, 100});
    }

    @Test(order = 6)
    void randomShortRowsAgainstEveryOrder() {
        Random random = new Random(1205);
        for (int round = 0; round < 300; round++) {
            int[] values = new int[1 + random.nextInt(7)];
            for (int i = 0; i < values.length; i++) {
                values[i] = random.nextInt(5) == 0 ? 0 : 1 + random.nextInt(100);
            }
            List<Integer> row = new ArrayList<>();
            for (int value : values) {
                row.add(value);
            }
            expect(everyOrder(row), values);
        }
    }

    /** Tries every order of bursting the balloons left in row. */
    private static int everyOrder(List<Integer> row) {
        int most = 0;
        for (int k = 0; k < row.size(); k++) {
            int left = k > 0 ? row.get(k - 1) : 1;
            int right = k + 1 < row.size() ? row.get(k + 1) : 1;
            int value = row.remove(k);
            most = Math.max(most, left * value * right + everyOrder(row));
            row.add(k, value);
        }
        return most;
    }

    @Test(order = 7, timeoutMs = 500)
    void threeHundredHundreds() {
        // each of the 298 inner bursts can pay 100^3; the last two pay at most 100 × 100 and 100
        int[] values = new int[300];
        Arrays.fill(values, 100);
        expect(298_010_100, values);
    }

    @Test(order = 8, timeoutMs = 500)
    void hundredsBetweenZeros() {
        // bursting every zero first never lowers another burst, and leaves 150 hundreds in a row
        int[] values = new int[300];
        for (int i = 0; i < values.length; i += 2) {
            values[i] = 100;
        }
        expect(148_010_100, values);
    }

    @Test(order = 9, timeoutMs = 500)
    void oneHundredAmongOnes() {
        // no burst can pay more than 100, and bursting the neighbours of the 100 one by one gets 100 every time
        int[] values = new int[300];
        Arrays.fill(values, 1);
        values[137] = 100;
        expect(30_000, values);
    }
}
