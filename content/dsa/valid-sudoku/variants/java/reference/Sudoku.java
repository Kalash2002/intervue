/** Checks a partly filled sudoku board for repeated digits. */
public class Sudoku {

    /** Returns true when no digit repeats in a row, a column or a 3 by 3 box. */
    static boolean isValid(String[] rows) {
        boolean[][] inRow = new boolean[9][10]; // inRow[r][d]: digit d already seen in row r
        boolean[][] inColumn = new boolean[9][10];
        boolean[][] inBox = new boolean[9][10];
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                char ch = rows[r].charAt(c);
                if (ch == '.') {
                    continue; // empty cells break no rule
                }
                int d = ch - '0';
                int box = r / 3 * 3 + c / 3; // boxes numbered 0..8, left to right, top to bottom
                if (inRow[r][d] || inColumn[c][d] || inBox[box][d]) {
                    return false; // a repeat in some group
                }
                inRow[r][d] = true;
                inColumn[c][d] = true;
                inBox[box][d] = true;
            }
        }
        return true;
    }
}
