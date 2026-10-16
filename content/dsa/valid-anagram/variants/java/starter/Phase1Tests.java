import static kit.Check.assertEquals;

import java.util.Random;
import kit.Test;

public class Phase1Tests {

    @Test(order = 1)
    void sample() {
        assertEquals(true, Anagrams.areAnagrams("carton", "contra"));
    }

    @Test(order = 2)
    void emptyStrings() {
        assertEquals(true, Anagrams.areAnagrams("", ""));
    }

    @Test(order = 3)
    void differentLengths() {
        assertEquals(false, Anagrams.areAnagrams("ab", "abc"));
        assertEquals(false, Anagrams.areAnagrams("", "a"));
        assertEquals(false, Anagrams.areAnagrams("zz", "z"));
    }

    @Test(order = 4)
    void sameLettersDifferentCounts() {
        assertEquals(false, Anagrams.areAnagrams("aab", "abb"));
        assertEquals(false, Anagrams.areAnagrams("noon", "nnno"));
        assertEquals(false, Anagrams.areAnagrams("aabb", "abcc"));
    }

    @Test(order = 5)
    void rearrangedWords() {
        assertEquals(true, Anagrams.areAnagrams("drawer", "reward"));
        assertEquals(true, Anagrams.areAnagrams("tool", "loot"));
        assertEquals(true, Anagrams.areAnagrams("abcxyz", "zyxcba"));
    }

    @Test(order = 6)
    void singleLetters() {
        assertEquals(true, Anagrams.areAnagrams("a", "a"));
        assertEquals(false, Anagrams.areAnagrams("a", "b"));
    }

    @Test(order = 7)
    void identicalWords() {
        assertEquals(true, Anagrams.areAnagrams("banana", "banana"));
    }

    @Test(order = 8)
    void sameLengthDisjointLetters() {
        assertEquals(false, Anagrams.areAnagrams("abc", "xyz"));
    }

    @Test(order = 9, timeoutMs = 1000)
    void largeRuns() {
        String a = "a".repeat(100_000) + "b".repeat(100_000);
        assertEquals(true, Anagrams.areAnagrams(a, a));
    }

    @Test(order = 10, timeoutMs = 1000)
    void largeShuffled() {
        Random random = new Random(7);
        char[] a = new char[200_000];
        for (int i = 0; i < a.length; i++) {
            a[i] = (char) ('a' + random.nextInt(26));
        }
        char[] b = a.clone();
        for (int i = b.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char t = b[i];
            b[i] = b[j];
            b[j] = t;
        }
        assertEquals(true, Anagrams.areAnagrams(new String(a), new String(b)));
        b[0] = b[0] == 'a' ? 'b' : 'a'; // one letter changed
        assertEquals(false, Anagrams.areAnagrams(new String(a), new String(b)));
    }
}
