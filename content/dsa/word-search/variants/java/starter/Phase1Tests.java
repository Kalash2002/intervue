import static kit.Check.assertEquals;

import java.util.Arrays;
import java.util.Random;
import kit.Test;

public class Phase1Tests {

    private static char[][] grid(String... rows) {
        char[][] board = new char[rows.length][];
        for (int i = 0; i < rows.length; i++) {
            board[i] = rows[i].toCharArray();
        }
        return board;
    }

    /** Checks the answer and that the grid holds its original letters afterwards. */
    private static void expect(char[][] board, String word, boolean want) {
        String before = Arrays.deepToString(board);
        assertEquals(want, WordSearch.exists(board, word), "word " + word);
        assertEquals(before, Arrays.deepToString(board), "the grid must hold its original letters again");
    }

    private static char[][] randomGrid(Random random) {
        char[][] board = new char[8][8];
        for (char[] row : board) {
            for (int c = 0; c < 8; c++) {
                row[c] = (char) ('a' + random.nextInt(3)); // only a, b and c: many partial matches
            }
        }
        return board;
    }

    @Test(order = 1)
    void sample() {
        expect(grid("cats", "orem", "ding"), "core", true);
    }

    @Test(order = 2)
    void diagonalStepDoesNotCount() {
        expect(grid("cats", "orem", "ding"), "cart", false);
    }

    @Test(order = 3)
    void cellUsedTwice() {
        expect(grid("q"), "qq", false);
        expect(grid("cats", "orem", "ding"), "tet", false);
    }

    @Test(order = 4)
    void singleCell() {
        expect(grid("q"), "q", true);
        expect(grid("q"), "z", false);
    }

    @Test(order = 5)
    void marksAreClearedAfterADeadEnd() {
        // the dead ends from the first m cells must not block the only trace, which starts bottom right
        expect(grid("mom", "omm"), "mmo", true);
    }

    @Test(order = 6)
    void wordLongerThanTheGrid() {
        expect(grid("ab", "cd"), "abdca", false);
    }

    @Test(order = 7)
    void traceThroughEveryCell() {
        expect(grid("cats", "orem", "ding"), "catsmeroding", true);
    }

    @Test(order = 8)
    void longestWordAlongTwoRows() {
        char[][] board = randomGrid(new Random(73));
        // across row 3 left to right, then back along row 4: sixteen cells
        String word = new String(board[3]) + new StringBuilder(new String(board[4])).reverse();
        expect(board, word, true);
    }

    @Test(order = 9, timeoutMs = 100)
    void fullGridWordNotThere() {
        Random random = new Random(72);
        char[][] board = randomGrid(random);
        StringBuilder word = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            word.append((char) ('a' + random.nextInt(3)));
        }
        expect(board, word.toString(), false);
    }
}
