import static kit.Check.assertEquals;
import static kit.Check.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import kit.Test;

public class Phase1Tests {

    /** Compares piece by piece, then the number of pieces. */
    private static void expect(String s, Integer... want) {
        String input = s.length() <= 60 ? "\"" + s + "\"" : s.length() + " letters";
        List<Integer> got = Pieces.sizes(s);
        assertTrue(got != null, "return a list of sizes, not null, for s = " + input);
        for (int i = 0; i < Math.min(want.length, got.size()); i++) {
            assertEquals(want[i], got.get(i), "length of piece " + i + " for s = " + input);
        }
        assertEquals(want.length, got.size(), "number of pieces for s = " + input);
    }

    @Test(order = 1)
    void sample() {
        expect("hgfhgxyzzyklmlk", 5, 1, 4, 5);
        expect("nvwnwv", 6);
        expect("wvu", 1, 1, 1);
    }

    @Test(order = 2)
    void aLetterWithNoCopiesLeftDoesNotEndThePiece() {
        expect("qtrtq", 5); // the r never appears again, but the q and t do
        expect("mxxmoo", 4, 2); // the x's end at 2, the m only at 3
    }

    @Test(order = 3)
    void aNewLetterDoesNotStartAPiece() {
        expect("gjgw", 3, 1); // the j is new, but the g comes back after it
        expect("jkjlkl", 6);
    }

    @Test(order = 4)
    void piecesAtBothEnds() {
        expect("ffgiigjj", 2, 4, 2);
    }

    @Test(order = 5)
    void theFirstLetterComesBackLast() {
        expect("rkwjqr", 6);
    }

    @Test(order = 6)
    void oneLetterOrOneRun() {
        expect("s", 1);
        expect("eeee", 4);
    }

    @Test(order = 7)
    void everyLetterOnce() {
        Integer[] ones = new Integer[26];
        Arrays.fill(ones, 1);
        expect("zyxwvutsrqponmlkjihgfedcba", ones);
    }

    @Test(order = 8)
    void randomShortStrings() {
        Random random = new Random(1537);
        for (int round = 0; round < 300; round++) {
            StringBuilder s = new StringBuilder();
            int length = 1 + random.nextInt(12);
            for (int i = 0; i < length; i++) {
                s.append((char) ('p' + random.nextInt(5)));
            }
            expect(s.toString(), mostPieces(s.toString()));
        }
    }

    /** Tries every set of cuts and keeps the valid one with the most pieces. */
    private static Integer[] mostPieces(String s) {
        List<Integer> best = null;
        for (int cuts = 0; cuts < 1 << (s.length() - 1); cuts++) {
            List<String> pieces = new ArrayList<>();
            int start = 0;
            for (int i = 0; i < s.length(); i++) {
                if (i == s.length() - 1 || (cuts >> i & 1) == 1) {
                    pieces.add(s.substring(start, i + 1));
                    start = i + 1;
                }
            }
            boolean valid = true;
            for (int a = 0; a < pieces.size() && valid; a++) {
                for (int b = a + 1; b < pieces.size() && valid; b++) {
                    for (char c : pieces.get(a).toCharArray()) {
                        valid &= pieces.get(b).indexOf(c) < 0;
                    }
                }
            }
            if (valid && (best == null || pieces.size() > best.size())) {
                best = new ArrayList<>();
                for (String piece : pieces) {
                    best.add(piece.length());
                }
            }
        }
        return best.toArray(new Integer[0]);
    }

    /** Appends count letters, cycling through the letters from first to last. */
    private static void cycle(StringBuilder s, char first, char last, int count) {
        for (int i = 0; i < count; i++) {
            s.append((char) (first + i % (last - first + 1)));
        }
    }

    @Test(order = 9, timeoutMs = 200)
    void twoLongPieces() {
        // a to f and u to z appear twice near the start and never again, but the g between them
        // belongs to g to m, which cycle on to position 59,999. Then n to t cycle to the end.
        StringBuilder s = new StringBuilder("abcdefuvwxyzgabcdefuvwxyz");
        cycle(s, 'g', 'm', 59_975);
        cycle(s, 'n', 't', 40_000);
        expect(s.toString(), 60_000, 40_000);
    }

    @Test(order = 10, timeoutMs = 200)
    void thirteenPairsOfLetters() {
        // Thirteen pieces, each two letters taking turns: a and b, then c and d, up to y and z.
        StringBuilder s = new StringBuilder();
        Integer[] want = new Integer[13];
        for (int piece = 0; piece < 13; piece++) {
            want[piece] = piece < 12 ? 7_692 : 7_696;
            char first = (char) ('a' + 2 * piece);
            cycle(s, first, (char) (first + 1), want[piece]);
        }
        expect(s.toString(), want);
    }
}
