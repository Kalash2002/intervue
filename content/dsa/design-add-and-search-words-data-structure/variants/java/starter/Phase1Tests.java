import static kit.Check.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import kit.Test;

public class Phase1Tests {

    private static WordDictionary with(String... words) {
        WordDictionary dictionary = new WordDictionary();
        for (String word : words) {
            dictionary.addWord(word);
        }
        return dictionary;
    }

    private static void expect(WordDictionary dictionary, String pattern, boolean want) {
        assertEquals(want, dictionary.search(pattern), "search(\"" + pattern + "\")");
    }

    @Test(order = 1)
    void sample() {
        WordDictionary dictionary = with("lamp", "limp", "lump");
        expect(dictionary, "l.mp", true);
        expect(dictionary, "l..p", true);
        expect(dictionary, "l.m", false);
    }

    @Test(order = 2)
    void aDotIsExactlyOneLetter() {
        WordDictionary dictionary = with("ox");
        expect(dictionary, "o.", true);
        expect(dictionary, ".", false);
        expect(dictionary, "ox.", false);
        expect(dictionary, ".x", true);
    }

    @Test(order = 3)
    void emptyDictionary() {
        WordDictionary dictionary = with();
        expect(dictionary, "..", false);
        expect(dictionary, "q", false);
    }

    @Test(order = 4)
    void plainWordsNeedAWholeWordMatch() {
        WordDictionary dictionary = with("kettle");
        expect(dictionary, "kettle", true);
        expect(dictionary, "kett", false);
        expect(dictionary, "kettles", false);
    }

    @Test(order = 5)
    void aDotMustTryEveryBranch() {
        // the first letter that fits the dot, a, leads to a dead end; only c continues to "by"
        WordDictionary dictionary = with("abx", "cby");
        expect(dictionary, ".by", true);
        expect(dictionary, ".bz", false);
        expect(dictionary, "..y", true);
    }

    @Test(order = 6)
    void wordsThatArePrefixesOfEachOther() {
        WordDictionary dictionary = with("van", "vane");
        expect(dictionary, "va.", true);
        expect(dictionary, "va..", true);
        expect(dictionary, "v.n.", true);
        expect(dictionary, "va...", false);
        expect(dictionary, "v..s", false);
    }

    @Test(order = 7)
    void wordAddedAfterAFailedSearch() {
        WordDictionary dictionary = with("pine");
        expect(dictionary, "p.n", false);
        dictionary.addWord("pin");
        expect(dictionary, "p.n", true);
        dictionary.addWord("pin");
        expect(dictionary, "p.n", true);
        expect(dictionary, "pi", false);
    }

    @Test(order = 8)
    void longestWords() {
        String word = "abcdefghijklmnopqrst"; // twenty letters
        WordDictionary dictionary = with(word, "z");
        expect(dictionary, word, true);
        expect(dictionary, "abcdefghij.lmnopqrs.", true);
        expect(dictionary, "abcdefghij.lmnopqrs", false);
        expect(dictionary, ".", true);
    }

    @Test(order = 9, timeoutMs = 1500)
    void manyWordsManyPatterns() {
        Random random = new Random(78);
        WordDictionary dictionary = new WordDictionary();
        List<String> stored = new ArrayList<>();
        for (int i = 0; i < 50_000; i++) {
            char[] letters = new char[6 + random.nextInt(5)];
            for (int k = 0; k < letters.length; k++) {
                letters[k] = (char) ('a' + random.nextInt(13)); // a to m only
            }
            stored.add(new String(letters));
            dictionary.addWord(stored.get(i));
        }
        for (int q = 0; q < 50_000; q++) {
            char[] pattern = stored.get(random.nextInt(stored.size())).toCharArray();
            int a = random.nextInt(pattern.length);
            int b = random.nextInt(pattern.length);
            if (q % 3 == 0) { // a stored word with one letter hidden: it fits itself
                pattern[a] = '.';
                expect(dictionary, new String(pattern), true);
            } else if (q % 3 == 1) { // one letter hidden, another changed to n..z, which no word holds
                if (a == b) {
                    b = (a + 1) % pattern.length;
                }
                pattern[a] = '.';
                pattern[b] = (char) ('n' + random.nextInt(13));
                expect(dictionary, new String(pattern), false);
            } else { // eleven letters: longer than every stored word
                char[] longer = new char[11];
                for (int k = 0; k < 11; k++) {
                    longer[k] = k == 0 ? '.' : (char) ('a' + random.nextInt(13));
                }
                expect(dictionary, new String(longer), false);
            }
        }
    }
}
