import static kit.Check.assertEquals;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import kit.Test;

public class Phase1Tests {

    private static void addCopies(SquareFinder finder, int x, int y, int copies) {
        for (int i = 0; i < copies; i++) {
            finder.add(x, y);
        }
    }

    private static void expectCount(long want, SquareFinder finder, int x, int y) {
        assertEquals(want, finder.count(x, y), "count(" + x + ", " + y + ")");
    }

    @Test(order = 1)
    void sample() {
        SquareFinder finder = new SquareFinder();
        finder.add(6, 7);
        finder.add(10, 7);
        finder.add(10, 3);
        finder.add(2, 7);
        finder.add(2, 3);
        expectCount(2, finder, 6, 3); // one square to the right, one to the left
        expectCount(0, finder, 6, 7);

        finder = new SquareFinder();
        finder.add(0, 5);
        addCopies(finder, 3, 5, 2);
        addCopies(finder, 0, 8, 2);
        expectCount(4, finder, 3, 8);

        finder = new SquareFinder();
        expectCount(0, finder, 7, 7);
        finder.add(5, 0);
        finder.add(10, 5);
        finder.add(5, 10);
        expectCount(0, finder, 0, 5); // a square, but tilted
    }

    @Test(order = 2)
    void squaresInAllFourDirections() {
        SquareFinder finder = new SquareFinder();
        // the eight points around (500, 500) at distance 7: one square of side 7 in each quarter
        int[][] around = {{507, 500}, {500, 507}, {507, 507}, {493, 500}, {493, 507}, {500, 493}, {507, 493}, {493, 493}};
        for (int[] p : around) {
            finder.add(p[0], p[1]);
        }
        expectCount(4, finder, 500, 500);
        finder.add(520, 500);
        finder.add(520, 520);
        finder.add(500, 520);
        expectCount(5, finder, 500, 500); // plus one of side 20, up and to the right
        expectCount(1, finder, 507, 507); // side 14, down and to the left
        expectCount(1, finder, 493, 493); // side 14, up and to the right
    }

    @Test(order = 3)
    void copiesMultiply() {
        SquareFinder finder = new SquareFinder();
        addCopies(finder, 40, 60, 3);
        addCopies(finder, 40, 90, 2);
        finder.add(70, 90);
        expectCount(6, finder, 70, 60); // 3 x 2 x 1
        addCopies(finder, 70, 60, 5); // copies of the query point itself change nothing for it
        expectCount(6, finder, 70, 60);
        expectCount(10, finder, 40, 60); // 2 x 5 x 1
        expectCount(15, finder, 40, 90); // 3 x 5 x 1
    }

    @Test(order = 4)
    void aSquareNeedsASideAndCountStoresNothing() {
        SquareFinder finder = new SquareFinder();
        addCopies(finder, 9, 9, 4);
        expectCount(0, finder, 9, 9); // four copies of the query point make no square
        finder.add(9, 14);
        finder.add(14, 9);
        expectCount(0, finder, 9, 9); // (14, 14) is missing
        finder.add(14, 14);
        expectCount(1, finder, 9, 9);
        expectCount(4, finder, 14, 14); // each copy of (9, 9) is a different choice
        expectCount(0, finder, 20, 20);
        finder.add(20, 25);
        finder.add(25, 20);
        expectCount(0, finder, 25, 25); // (20, 20) was only counted, never added
        finder.add(25, 25);
        expectCount(1, finder, 20, 20);
    }

    @Test(order = 5)
    void rectanglesAndTiltedSquaresDoNotCount() {
        SquareFinder finder = new SquareFinder();
        finder.add(100, 100);
        finder.add(130, 100);
        finder.add(100, 120);
        expectCount(0, finder, 130, 120); // a rectangle 30 wide and 20 high
        finder.add(300, 200);
        finder.add(310, 210);
        finder.add(300, 220);
        expectCount(0, finder, 290, 210); // a square standing on its corner
        finder.add(290, 210);
        expectCount(0, finder, 300, 200);
        expectCount(0, finder, 310, 210);
        finder.add(130, 130);
        expectCount(1, finder, 100, 130); // with (130, 130) the rectangle's points make a square of side 30
    }

    @Test(order = 6)
    void theEdgesOfTheGrid() {
        SquareFinder finder = new SquareFinder();
        finder.add(0, 0);
        finder.add(0, 1000);
        finder.add(1000, 0);
        expectCount(1, finder, 1000, 1000);
        expectCount(0, finder, 0, 0);
        finder.add(1000, 1000);
        expectCount(1, finder, 0, 0);
        expectCount(1, finder, 0, 1000);
        expectCount(1, finder, 1000, 0);
        expectCount(1, finder, 1000, 1000);
        // the squares these would finish reach past the edge of the grid on one side
        expectCount(0, finder, 0, 500);
        expectCount(0, finder, 1000, 500);
        expectCount(0, finder, 500, 1000);
    }

    @Test(order = 7)
    void largeCountsNeedALong() {
        SquareFinder finder = new SquareFinder();
        addCopies(finder, 200, 300, 2000);
        addCopies(finder, 200, 700, 2000);
        addCopies(finder, 600, 700, 2000);
        expectCount(8_000_000_000L, finder, 600, 300); // 2000^3 does not fit in an int
        addCopies(finder, 600, 300, 1500);
        expectCount(6_000_000_000L, finder, 200, 700); // 2000 x 2000 x 1500
    }

    @Test(order = 8)
    void twoFindersAreIndependent() {
        SquareFinder first = new SquareFinder();
        SquareFinder second = new SquareFinder();
        first.add(1, 1);
        first.add(1, 4);
        first.add(4, 1);
        second.add(4, 4);
        expectCount(1, first, 4, 4);
        expectCount(0, second, 4, 4);
        expectCount(0, second, 1, 1);
    }

    /** Counts squares by trying every side length up to 8 and every direction: an answer that never looks at columns. */
    private static long bySides(Map<Integer, Integer> copies, int x, int y) {
        long ways = 0;
        for (int d = -8; d <= 8; d++) { // the random points lie in a 9 x 9 window, so no side is longer
            if (d == 0) {
                continue;
            }
            for (int e : new int[] {d, -d}) {
                ways += (long) copiesAt(copies, x + d, y) * copiesAt(copies, x, y + e) * copiesAt(copies, x + d, y + e);
            }
        }
        return ways;
    }

    private static int copiesAt(Map<Integer, Integer> copies, int x, int y) {
        return x < 0 || x > 1000 || y < 0 || y > 1000 ? 0 : copies.getOrDefault(1001 * x + y, 0);
    }

    @Test(order = 9)
    void randomFinders() {
        Random random = new Random(1431);
        int[] corners = {0, 496, 992}; // small windows at both edges of the grid and in the middle
        for (int round = 0; round < 300; round++) {
            int baseX = corners[random.nextInt(3)];
            int baseY = corners[random.nextInt(3)];
            SquareFinder finder = new SquareFinder();
            Map<Integer, Integer> copies = new HashMap<>();
            int calls = 1 + random.nextInt(60);
            for (int call = 0; call < calls; call++) {
                int x = baseX + random.nextInt(9);
                int y = baseY + random.nextInt(9);
                if (random.nextInt(3) > 0) {
                    finder.add(x, y);
                    copies.merge(1001 * x + y, 1, Integer::sum);
                } else {
                    expectCount(bySides(copies, x, y), finder, x, y);
                }
            }
        }
    }

    /** The timed points: dense in the lower-left quarter, about ten to a row and a column in the upper-right one. */
    private static boolean stored(int x, int y) {
        if (x < 0 || x > 1000 || y < 0 || y > 1000) {
            return false;
        }
        if (x < 500 && y < 500) {
            return (x + 2 * y) % 3 == 0;
        }
        return x >= 500 && y >= 500 && (x - y) % 50 == 0;
    }

    @Test(order = 10, timeoutMs = 1000)
    void manyPointsAndManyCounts() {
        SquareFinder finder = new SquareFinder();
        for (int x = 0; x <= 1000; x++) {
            for (int y = 0; y <= 1000; y++) {
                if (stored(x, y)) {
                    finder.add(x, y); // 88,355 points, each once
                }
            }
        }
        Random random = new Random(1433);
        for (int query = 0; query < 50_000; query++) {
            // queries in the sparse quarter, half of them on its diagonal lines so that squares exist
            int x = 500 + random.nextInt(501);
            int y = random.nextBoolean() ? 500 + random.nextInt(501) : 500 + Math.floorMod(x - 500, 50) + 50 * random.nextInt(10);
            long want = 0;
            for (int other = 500 + Math.floorMod(x - 500, 50); other <= 1000; other += 50) { // column x's points
                int side = other - y;
                if (side != 0) {
                    want += (stored(x + side, y) && stored(x + side, other) ? 1 : 0) + (stored(x - side, y) && stored(x - side, other) ? 1 : 0);
                }
            }
            long got = finder.count(x, y);
            if (got != want) { // the message is built only for a wrong count
                assertEquals(want, got, "count(" + x + ", " + y + ") after the 88,355 timed points, query " + query);
            }
        }
    }
}
