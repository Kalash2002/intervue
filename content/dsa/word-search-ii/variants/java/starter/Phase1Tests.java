import static kit.Check.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import kit.Test;

public class Phase1Tests {

    private static char[][] grid(String... rows) {
        char[][] board = new char[rows.length][];
        for (int i = 0; i < rows.length; i++) {
            board[i] = rows[i].toCharArray();
        }
        return board;
    }

    /** Checks the words found (in any order, each once) and that the grid holds its letters afterwards. */
    private static void expect(char[][] board, String[] words, String... want) {
        String before = Arrays.deepToString(board);
        List<String> got = new ArrayList<>(WordHunt.findAll(board, words));
        got.sort(null);
        String[] sorted = want.clone();
        Arrays.sort(sorted);
        assertEquals(Arrays.toString(sorted), got.toString(), "the words found, sorted");
        assertEquals(before, Arrays.deepToString(board), "the grid must hold its original letters again");
    }

    @Test(order = 1)
    void sample() {
        expect(grid("plum", "ears", "ntok"), new String[] {"plum", "rot", "pear", "sort", "pan"}, "plum", "pear", "rot");
    }

    @Test(order = 2)
    void wordsThatArePrefixesOfEachOther() {
        expect(grid("plum", "ears", "ntok"), new String[] {"ear", "earl", "era"}, "ear");
        expect(grid("plum", "ears", "ntok"), new String[] {"rot", "ro", "r"}, "r", "ro", "rot");
    }

    @Test(order = 3)
    void aWordTracedTwiceIsReportedOnce() {
        expect(grid("kk"), new String[] {"kk", "kkk"}, "kk");
        expect(grid("hah", "aha"), new String[] {"ha", "aha"}, "aha", "ha");
    }

    @Test(order = 4)
    void aCellIsUsedOncePerTrace() {
        expect(grid("plum", "ears", "ntok"), new String[] {"pep", "pea"}, "pea");
    }

    @Test(order = 5)
    void diagonalStepsDoNotCount() {
        expect(grid("plum", "ears", "ntok"), new String[] {"pa", "mr", "ms"}, "ms");
    }

    @Test(order = 6)
    void marksAreClearedAfterADeadEnd() {
        expect(grid("mom", "omm"), new String[] {"mmo", "mom", "omo", "momo"}, "mmo", "mom", "momo", "omo");
    }

    @Test(order = 7)
    void singleCellAndNothingFound() {
        expect(grid("z"), new String[] {"z", "zz", "y"}, "z");
        expect(grid("ab", "cd"), new String[] {"ad", "bc", "abdca"});
    }

    @Test(order = 8)
    void randomSmallGridAgainstOneSearchPerWord() {
        Random random = new Random(79);
        char[][] board = new char[6][6];
        for (char[] row : board) {
            for (int c = 0; c < 6; c++) {
                row[c] = (char) ('a' + random.nextInt(3));
            }
        }
        Set<String> words = new LinkedHashSet<>();
        while (words.size() < 300) {
            char[] word = new char[1 + random.nextInt(6)];
            for (int k = 0; k < word.length; k++) {
                word[k] = (char) ('a' + random.nextInt(3));
            }
            words.add(new String(word));
        }
        List<String> want = new ArrayList<>();
        for (String word : words) {
            if (traceable(board, word)) {
                want.add(word);
            }
        }
        expect(board, words.toArray(new String[0]), want.toArray(new String[0]));
    }

    @Test(order = 9, timeoutMs = 1000)
    void manyWordsSharingALongPrefix() {
        String[] rows = new String[12];
        Arrays.fill(rows, 0, 10, "a".repeat(12));
        rows[10] = "nopqrstuvwxy";
        rows[11] = "bcdefghijklm"; // no cell of this row touches an a
        List<String> words = new ArrayList<>();
        for (char x = 'b'; x <= 'm'; x++) {
            for (char y = 'a'; y <= 'z'; y++) {
                words.add("aaaaaaaa" + x + y);
                for (char z = 'a'; z <= 'z'; z++) {
                    words.add("aaaaaaa" + x + y + z);
                }
            }
        }
        String[] found = {"aaaaaaaaan", "aaaaaaaaaa", "nb", "ymlk", "an", "ayx"};
        words.addAll(List.of(found));
        words.addAll(List.of("ab", "zz"));
        expect(grid(rows), words.toArray(new String[0]), found);
    }

    // One plain search for one word, used to compute the expected answer of the random test.
    private static boolean traceable(char[][] board, String word) {
        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[0].length; c++) {
                if (step(board, word, r, c, 0, new boolean[board.length][board[0].length])) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean step(char[][] board, String word, int r, int c, int k, boolean[][] used) {
        if (r < 0 || c < 0 || r >= board.length || c >= board[0].length || used[r][c] || board[r][c] != word.charAt(k)) {
            return false;
        }
        if (k == word.length() - 1) {
            return true;
        }
        used[r][c] = true;
        boolean ok = step(board, word, r + 1, c, k + 1, used) || step(board, word, r - 1, c, k + 1, used)
                || step(board, word, r, c + 1, k + 1, used) || step(board, word, r, c - 1, k + 1, used);
        used[r][c] = false;
        return ok;
    }
}
