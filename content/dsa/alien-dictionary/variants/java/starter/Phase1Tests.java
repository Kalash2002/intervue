import static kit.Check.assertEquals;
import static kit.Check.fail;

import java.util.Arrays;
import java.util.Random;
import kit.Test;

public class Phase1Tests {

    // Any valid order passes: each letter of the words once, no other letter, and every neighbouring
    // pair of words sorted under it.
    private static void expectValidOrder(String... words) {
        String order = Lexicon.letterOrder(words);
        boolean[] used = new boolean[26];
        int letters = 0;
        for (String word : words) {
            for (char ch : word.toCharArray()) {
                if (!used[ch - 'a']) {
                    used[ch - 'a'] = true;
                    letters++;
                }
            }
        }
        int[] position = new int[26];
        Arrays.fill(position, -1);
        for (int i = 0; i < order.length(); i++) {
            char ch = order.charAt(i);
            if (ch < 'a' || ch > 'z' || !used[ch - 'a']) {
                fail("'" + ch + "' at index " + i + " of \"" + order + "\" is not a letter of the words");
            }
            if (position[ch - 'a'] != -1) {
                fail("'" + ch + "' appears twice in \"" + order + "\"");
            }
            position[ch - 'a'] = i;
        }
        assertEquals(letters, order.length(), "letters in \"" + order + "\"; every letter of the words must appear");
        for (int w = 0; w + 1 < words.length; w++) {
            String first = words[w];
            String second = words[w + 1];
            int k = 0;
            while (k < first.length() && k < second.length() && first.charAt(k) == second.charAt(k)) {
                k++;
            }
            if (k < first.length() && k < second.length()
                    && position[first.charAt(k) - 'a'] > position[second.charAt(k) - 'a']) {
                fail("under \"" + order + "\", \"" + first + "\" does not come before \"" + second + "\"");
            }
        }
    }

    private static void expectNoOrder(String... words) {
        String order = Lexicon.letterOrder(words);
        assertEquals("\"\"", "\"" + order + "\"", "no order exists, so the answer must be the empty string");
    }

    @Test(order = 1)
    void sample() {
        expectValidOrder("mop", "mpq", "om", "oq", "q");
        expectValidOrder("zoo");
        expectNoOrder("kn", "nk", "k");
        expectNoOrder("lamp", "lam");
    }

    @Test(order = 2)
    void aPrefixMayComeFirstButNotLast() {
        expectValidOrder("lam", "lamp");
        expectNoOrder("tid", "tide", "ti");
    }

    @Test(order = 3)
    void onlyTheFirstDifferenceCounts() {
        // b before x; reading the second letters as well would also demand x before b
        expectValidOrder("bx", "xb");
        expectValidOrder("hug", "hen", "eh");
    }

    @Test(order = 4)
    void lettersWithoutRulesStillAppear() {
        expectValidOrder("cab", "cad"); // only b before d is known; c and a must be listed too
        expectValidOrder("y");
        expectValidOrder("rs", "rst", "rstuv");
    }

    @Test(order = 5)
    void repeatedWordsAndRepeatedRules() {
        expectValidOrder("ok", "ok", "ko");
        // a before b is given twice; counting it twice would leave b waiting forever
        expectValidOrder("ka", "kb", "ma", "mb");
    }

    @Test(order = 6)
    void aLoopThroughThreeLetters() {
        // b before c, then a before b, then c before a
        expectNoOrder("ab", "ac", "bc", "ba");
        expectNoOrder("w", "x", "y", "w");
    }

    @Test(order = 7)
    void randomListsWithAndWithoutAContradiction() {
        Random random = new Random(97);
        for (int round = 0; round < 200; round++) {
            char[] alphabet = new char[1 + random.nextInt(8)]; // a hidden order of some letters
            boolean[] taken = new boolean[26];
            for (int i = 0; i < alphabet.length; i++) {
                int letter;
                do {
                    letter = random.nextInt(26);
                } while (taken[letter]);
                taken[letter] = true;
                alphabet[i] = (char) ('a' + letter);
            }
            String[] words = new String[1 + random.nextInt(12)];
            for (int i = 0; i < words.length; i++) {
                StringBuilder word = new StringBuilder();
                for (int j = 1 + random.nextInt(5); j > 0; j--) {
                    word.append(alphabet[random.nextInt(alphabet.length)]);
                }
                words[i] = word.toString();
            }
            String hidden = new String(alphabet);
            Arrays.sort(words, (p, q) -> compare(p, q, hidden));
            expectValidOrder(words);
            String last = words[words.length - 1];
            String[] broken = Arrays.copyOf(words, words.length + 2);
            broken[words.length] = last + alphabet[0];
            broken[words.length + 1] = last; // a word after its own extension
            expectNoOrder(broken);
        }
    }

    /** Dictionary order under the given alphabet. */
    private static int compare(String p, String q, String alphabet) {
        for (int k = 0; k < p.length() && k < q.length(); k++) {
            if (p.charAt(k) != q.charAt(k)) {
                return alphabet.indexOf(p.charAt(k)) - alphabet.indexOf(q.charAt(k));
            }
        }
        return p.length() - q.length();
    }

    /** The numbers 0 to count - 1 written in base 26 with four digits, in a shuffled alphabet. */
    private static String[] numbered(int count, long seed) {
        char[] digit = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        Random random = new Random(seed);
        for (int i = 25; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char swap = digit[i];
            digit[i] = digit[j];
            digit[j] = swap;
        }
        String[] words = new String[count];
        for (int i = 0; i < count; i++) {
            words[i] = new String(new char[] {
                digit[i / 17_576], digit[i / 676 % 26], digit[i / 26 % 26], digit[i % 26]
            });
        }
        return words;
    }

    @Test(order = 8, timeoutMs = 1000)
    void aHundredThousandSortedWords() {
        expectValidOrder(numbered(100_000, 8));
    }

    @Test(order = 9, timeoutMs = 1000)
    void aHundredThousandWordsWithTheFirstRepeatedAtTheEnd() {
        String[] words = Arrays.copyOf(numbered(99_999, 9), 100_000);
        words[99_999] = words[0]; // its first letter would have to come after the last word's
        expectNoOrder(words);
    }
}
