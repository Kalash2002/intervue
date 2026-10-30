import static kit.Check.assertEquals;

import kit.Test;

public class Phase1Tests {

    private static void expect(int want, int rows, int cols) {
        assertEquals(want, SortingFloor.routes(rows, cols), "rows = " + rows + ", cols = " + cols);
    }

    @Test(order = 1)
    void sample() {
        expect(35, 4, 5);
        expect(56, 6, 4);
        expect(1, 1, 1);
    }

    @Test(order = 2)
    void oneRowOrOneColumnLeavesOneRoute() {
        expect(1, 1, 100);
        expect(1, 100, 1);
        expect(1, 12, 1);
    }

    @Test(order = 3)
    void twoRowsOrTwoColumns() {
        // the one move across can come before any of the 49 moves along, or after all of them
        expect(50, 2, 50);
        expect(50, 50, 2);
    }

    @Test(order = 4)
    void swappingRowsAndColumnsKeepsTheCount() {
        expect(125_970, 9, 13);
        expect(125_970, 13, 9);
    }

    @Test(order = 5)
    void everyFloorUpToTenByTen() {
        for (int rows = 1; rows <= 10; rows++) {
            for (int cols = 1; cols <= 10; cols++) {
                expect(everyMoveSequence(rows, cols), rows, cols);
            }
        }
    }

    /** Counts the sequences of rows + cols - 2 moves, each down or right, with exactly rows - 1 downs. */
    private static int everyMoveSequence(int rows, int cols) {
        int moves = rows + cols - 2;
        int count = 0;
        for (int downs = 0; downs < 1 << moves; downs++) {
            if (Integer.bitCount(downs) == rows - 1) {
                count++;
            }
        }
        return count;
    }

    @Test(order = 6)
    void narrowFloorsAtTheLargestLength() {
        expect(4_421_275, 100, 5);
        expect(91_962_520, 6, 100);
    }

    @Test(order = 7, timeoutMs = 200)
    void aSquareishFloorWithNearlyTwoBillionRoutes() {
        // 15 moves down and 19 to the right: 34 choose 15 orders
        expect(1_855_967_520, 16, 20);
    }

    @Test(order = 8, timeoutMs = 200)
    void aTallFloorWithTheMostRoutesAllowed() {
        // 68 moves down and 7 to the right: 75 choose 7 orders
        expect(1_984_829_850, 69, 8);
    }
}
