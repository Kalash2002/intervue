import static kit.Check.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import kit.Test;

public class Phase1Tests {

    @Test(order = 1)
    void sample() {
        assertEquals(4, Ladder.fewestChanges("cold", "warm", List.of("cord", "card", "ward", "warm", "worm")));
    }

    @Test(order = 2)
    void oneChange() {
        assertEquals(1, Ladder.fewestChanges("map", "mat", List.of("mat", "man", "mad")));
    }

    @Test(order = 3)
    void sameWordNeedsNoChange() {
        assertEquals(0, Ladder.fewestChanges("abc", "abc", List.of()));
    }

    @Test(order = 4)
    void endIsNotAllowed() {
        assertEquals(-1, Ladder.fewestChanges("dog", "dot", List.of("dig", "dug")));
        assertEquals(-1, Ladder.fewestChanges("dog", "dot", List.of()));
    }

    @Test(order = 5)
    void endIsAllowedButCannotBeReached() {
        assertEquals(-1, Ladder.fewestChanges("cold", "warm", List.of("cord", "card", "warm", "worm")));
    }

    @Test(order = 6)
    void beginMayBeInTheList() {
        assertEquals(2, Ladder.fewestChanges("hat", "cot", List.of("hat", "hot", "cat", "cot")));
    }

    @Test(order = 7)
    void shortcutBeatsTheLongRoute() {
        // aaa, aac, acc, ccc is 3 changes; the route through aab, abb, bbb, bbc, bcc is 6.
        List<String> allowed = List.of("aab", "abb", "bbb", "bbc", "bcc", "ccc", "acc", "aac");
        assertEquals(3, Ladder.fewestChanges("aaa", "ccc", allowed));
    }

    @Test(order = 8)
    void oneLetterWords() {
        assertEquals(1, Ladder.fewestChanges("a", "c", List.of("b", "c")));
        assertEquals(-1, Ladder.fewestChanges("a", "c", List.of("b")));
    }

    @Test(order = 9, timeoutMs = 2400)
    void largeListWithAnIsolatedEnd() {
        Random random = new Random(5);
        String letters = "abcdefg";
        List<String> allowed = new ArrayList<>();
        char[] word = new char[6];
        int total = 7 * 7 * 7 * 7 * 7 * 7;
        for (int code = 0; code < total; code++) {
            int rest = code;
            for (int i = 0; i < 6; i++) {
                word[i] = letters.charAt(rest % 7);
                rest /= 7;
            }
            if (random.nextDouble() < 0.42) {
                allowed.add(new String(word));
            }
        }
        allowed.add("zzzzzz"); // allowed, but no other word is one change away from it
        assertEquals(-1, Ladder.fewestChanges("aaaaaa", "zzzzzz", allowed));
    }
}
