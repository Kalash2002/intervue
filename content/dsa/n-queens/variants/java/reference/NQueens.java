import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Lists every way to place n queens on an n by n board with no two attacking each other. */
public class NQueens {

    /** Returns each placement once, as n rows of '.' and 'Q' from top to bottom. */
    static List<List<String>> solve(int n) {
        List<List<String>> result = new ArrayList<>();
        // a diagonal going down-right keeps row - col fixed, one going down-left keeps row + col fixed
        place(n, 0, new int[n], new boolean[n], new boolean[2 * n - 1], new boolean[2 * n - 1], result);
        return result;
    }

    // Places a queen in row and every row below it; colOf[r] is the column of the queen in row r.
    private static void place(int n, int row, int[] colOf, boolean[] colTaken, boolean[] downRight, boolean[] downLeft, List<List<String>> result) {
        if (row == n) {
            result.add(draw(colOf));
            return;
        }
        for (int c = 0; c < n; c++) {
            int dr = row - c + n - 1; // shifted so the index is never negative
            int dl = row + c;
            if (colTaken[c] || downRight[dr] || downLeft[dl]) {
                continue; // an earlier queen attacks this square
            }
            colTaken[c] = downRight[dr] = downLeft[dl] = true;
            colOf[row] = c;
            place(n, row + 1, colOf, colTaken, downRight, downLeft, result);
            colTaken[c] = downRight[dr] = downLeft[dl] = false; // free the lines for the next column
        }
    }

    private static List<String> draw(int[] colOf) {
        List<String> board = new ArrayList<>();
        for (int c : colOf) {
            char[] row = new char[colOf.length];
            Arrays.fill(row, '.');
            row[c] = 'Q';
            board.add(new String(row));
        }
        return board;
    }
}
