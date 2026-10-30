import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Lists every way to place n queens on an n by n board with no two attacking each other. */
public class NQueens {

    /** Returns each placement once, as n rows of '.' and 'Q' from top to bottom. */
    static List<List<String>> solve(int n) {
        List<List<String>> result = new ArrayList<>();
        fill(n, 0, new int[n], result);
        return result;
    }

    // Tries every column in every row and checks the whole board only once all n queens stand.
    private static void fill(int n, int row, int[] colOf, List<List<String>> result) {
        if (row == n) {
            if (safe(colOf)) {
                result.add(draw(colOf));
            }
            return;
        }
        for (int c = 0; c < n; c++) {
            colOf[row] = c;
            fill(n, row + 1, colOf, result);
        }
    }

    private static boolean safe(int[] colOf) {
        for (int i = 0; i < colOf.length; i++) {
            for (int j = i + 1; j < colOf.length; j++) {
                if (colOf[i] == colOf[j] || Math.abs(colOf[i] - colOf[j]) == j - i) {
                    return false; // same column, or as many columns apart as rows: same diagonal
                }
            }
        }
        return true;
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
