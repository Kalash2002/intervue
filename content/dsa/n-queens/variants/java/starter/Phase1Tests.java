import static kit.Check.assertEquals;
import static kit.Check.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import kit.Test;

public class Phase1Tests {

    // the number of placements for n = 0..9, worked out independently of any solution
    private static final long[] COUNT = {1, 1, 0, 0, 2, 10, 4, 40, 92, 352};

    /** Sorts the list of placements but not their rows: row order is part of the answer. */
    private static String normal(List<List<String>> boards) {
        List<String> parts = new ArrayList<>();
        for (List<String> b : boards) {
            parts.add(b.toString());
        }
        parts.sort(null);
        return parts.toString();
    }

    private static void expect(int n, String[][] want) {
        List<List<String>> wanted = new ArrayList<>();
        for (String[] w : want) {
            wanted.add(List.of(w));
        }
        assertEquals(normal(wanted), normal(NQueens.solve(n)));
    }

    /** That many different valid placements are all of them. */
    private static void expectAll(int n) {
        List<List<String>> got = NQueens.solve(n);
        Set<String> seen = new HashSet<>();
        for (List<String> board : got) {
            assertEquals(n, board.size(), "board " + board + " has the wrong number of rows");
            Set<Integer> cols = new HashSet<>();
            Set<Integer> downRight = new HashSet<>();
            Set<Integer> downLeft = new HashSet<>();
            for (int r = 0; r < n; r++) {
                String row = board.get(r);
                int c = row.indexOf('Q');
                assertTrue(
                        row.length() == n && c >= 0 && row.lastIndexOf('Q') == c && row.replace("Q", "").replace(".", "").isEmpty(),
                        "row " + r + " of " + board + " must be n squares of '.' with one 'Q'");
                assertTrue(cols.add(c), "two queens share a column in " + board);
                assertTrue(downRight.add(r - c) && downLeft.add(r + c), "two queens share a diagonal in " + board);
            }
            assertTrue(seen.add(board.toString()), "placement " + board + " is listed twice");
        }
        assertEquals(COUNT[n], (long) got.size(), "number of placements");
    }

    @Test(order = 1)
    void sample() {
        expect(
                6,
                new String[][] {
                    {".Q....", "...Q..", ".....Q", "Q.....", "..Q...", "....Q."},
                    {"..Q...", ".....Q", ".Q....", "....Q.", "Q.....", "...Q.."},
                    {"...Q..", "Q.....", "....Q.", ".Q....", ".....Q", "..Q..."},
                    {"....Q.", "..Q...", "Q.....", ".....Q", "...Q..", ".Q...."}
                });
    }

    @Test(order = 2)
    void boardsWithNoPlacement() {
        expect(2, new String[][] {});
        expect(3, new String[][] {});
    }

    @Test(order = 3)
    void oneSquare() {
        expect(1, new String[][] {{"Q"}});
    }

    @Test(order = 4)
    void bothDiagonalsCount() {
        expectAll(4);
        expectAll(5);
    }

    @Test(order = 5)
    void sevenAndEight() {
        expectAll(7);
        expectAll(8);
    }

    @Test(order = 6, timeoutMs = 300)
    void nine() {
        expectAll(9);
    }
}
