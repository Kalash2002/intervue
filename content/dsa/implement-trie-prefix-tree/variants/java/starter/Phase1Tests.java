import static kit.Check.assertEquals;

import java.util.Random;
import java.util.TreeSet;
import kit.Test;

public class Phase1Tests {

    @Test(order = 1)
    void sample() {
        Trie trie = new Trie();
        trie.insert("tram");
        trie.insert("trap");
        trie.insert("tab");
        assertEquals(false, trie.search("tra"));
        assertEquals(true, trie.startsWith("tra"));
        assertEquals(true, trie.search("trap"));
    }

    @Test(order = 2)
    void emptyTrie() {
        Trie trie = new Trie();
        assertEquals(false, trie.search("a"));
        assertEquals(false, trie.startsWith("a"));
    }

    @Test(order = 3)
    void sharedPrefix() {
        Trie trie = new Trie();
        trie.insert("tram");
        trie.insert("trap");
        assertEquals(true, trie.startsWith("tr"));
        assertEquals(false, trie.startsWith("tre"));
        assertEquals(true, trie.search("trap"));
        assertEquals(false, trie.search("trax"));
    }

    @Test(order = 4)
    void aWordCanBeThePrefixOfAnother() {
        Trie trie = new Trie();
        trie.insert("car");
        trie.insert("cart");
        assertEquals(true, trie.search("car"));
        assertEquals(true, trie.search("cart"));
        assertEquals(true, trie.startsWith("car"));
        assertEquals(false, trie.search("carts"));
        assertEquals(true, trie.startsWith("cart"));
    }

    @Test(order = 5)
    void insertingTheLongerWordFirst() {
        Trie trie = new Trie();
        trie.insert("cart");
        assertEquals(false, trie.search("car"));
        trie.insert("car");
        assertEquals(true, trie.search("car"));
    }

    @Test(order = 6)
    void aWordIsItsOwnPrefix() {
        Trie trie = new Trie();
        trie.insert("kiwi");
        assertEquals(true, trie.startsWith("kiwi"));
        assertEquals(false, trie.startsWith("kiwis"));
    }

    @Test(order = 7)
    void insertingTwiceChangesNothing() {
        Trie trie = new Trie();
        trie.insert("fig");
        trie.insert("fig");
        assertEquals(true, trie.search("fig"));
        assertEquals(false, trie.search("fi"));
    }

    @Test(order = 8)
    void singleLetterWordsAndBothEnds() {
        Trie trie = new Trie();
        trie.insert("a");
        trie.insert("z");
        trie.insert("zebra");
        assertEquals(true, trie.search("a"));
        assertEquals(true, trie.search("z"));
        assertEquals(false, trie.search("b"));
        assertEquals(true, trie.startsWith("zeb"));
        assertEquals(false, trie.startsWith("ab"));
    }

    @Test(order = 9, timeoutMs = 2000)
    void manyWordsManyQueries() {
        Random random = new Random(9);
        TreeSet<String> words = new TreeSet<>();
        Trie trie = new Trie();
        for (int i = 0; i < 50_000; i++) {
            String word = randomText(random, 8);
            words.add(word);
            trie.insert(word);
        }
        Object[] stored = words.toArray();
        for (int q = 0; q < 200_000; q++) {
            int kind = q % 4;
            if (kind == 0) { // a stored word
                String word = (String) stored[random.nextInt(stored.length)];
                assertEquals(true, trie.search(word));
            } else if (kind == 1) { // a prefix of a stored word
                String word = (String) stored[random.nextInt(stored.length)];
                assertEquals(true, trie.startsWith(word.substring(0, 1 + random.nextInt(8))));
            } else if (kind == 2) { // a random word, almost surely not stored
                String word = randomText(random, 8);
                assertEquals(words.contains(word), trie.search(word));
            } else { // a short random prefix, sometimes present and sometimes not
                String prefix = randomText(random, 3 + random.nextInt(3));
                String next = words.ceiling(prefix);
                assertEquals(next != null && next.startsWith(prefix), trie.startsWith(prefix));
            }
        }
    }

    private static String randomText(Random random, int length) {
        char[] letters = new char[length];
        for (int i = 0; i < length; i++) {
            letters[i] = (char) ('a' + random.nextInt(26));
        }
        return new String(letters);
    }
}
