import static kit.Check.assertEquals;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import kit.Test;

public class Phase1Tests {

    private static void expect(boolean want, String text, List<String> words) {
        String input = text.length() <= 40 ? "text = \"" + text + "\", words = " + words : text.length() + " letters";
        assertEquals(want, Segmenter.canSplit(text, new ArrayList<>(words)), input);
    }

    @Test(order = 1)
    void sample() {
        expect(true, "seatide", List.of("seat", "sea", "tide"));
        expect(true, "coconut", List.of("co", "nut"));
        expect(false, "plumbs", List.of("plum", "plumb", "sum"));
        expect(false, "ox", List.of("oxen"));
    }

    @Test(order = 2)
    void theLongestFirstPieceCanBeWrong() {
        expect(true, "pinkink", List.of("pink", "pin", "kink")); // pink leaves ink; pin, kink
    }

    @Test(order = 3)
    void theShortestFirstPieceCanBeWrong() {
        expect(true, "cartridge", List.of("car", "cart", "ridge")); // car leaves tridge
        expect(true, "notebook", List.of("no", "note", "book")); // no leaves tebook
    }

    @Test(order = 4)
    void wordsCanBeUsedAgain() {
        expect(true, "murmurmur", List.of("mur"));
        expect(true, "abcabcab", List.of("abc", "ab"));
    }

    @Test(order = 5)
    void aLeftoverTailMeansNo() {
        expect(false, "doors", List.of("door", "do", "or"));
        expect(false, "kayaks", List.of("kayak", "ka", "yak"));
    }

    @Test(order = 6)
    void wordsThatRunPastTheEnd() {
        expect(false, "plan", List.of("pl", "anet")); // anet would start at 2 and run 2 letters past the end
        expect(true, "tea", List.of("teapot", "t", "ea"));
    }

    @Test(order = 7)
    void aOneLetterMessage() {
        expect(true, "q", List.of("q"));
        expect(false, "q", List.of("qu", "u"));
    }

    @Test(order = 8)
    void randomShortMessages() {
        Random random = new Random(1108);
        for (int round = 0; round < 300; round++) {
            String text = randomWord(random, 1 + random.nextInt(12), "ab");
            Set<String> words = new LinkedHashSet<>();
            int count = 1 + random.nextInt(5);
            while (words.size() < count) {
                words.add(randomWord(random, 1 + random.nextInt(3), "ab"));
            }
            expect(everyCut(text, words), text, new ArrayList<>(words));
        }
    }

    private static String randomWord(Random random, int length, String letters) {
        StringBuilder word = new StringBuilder();
        for (int i = 0; i < length; i++) {
            word.append(letters.charAt(random.nextInt(letters.length())));
        }
        return word.toString();
    }

    /** Tries every set of cut points between letters. */
    private static boolean everyCut(String text, Set<String> words) {
        int gaps = text.length() - 1;
        for (int cuts = 0; cuts < 1 << gaps; cuts++) {
            boolean allWords = true;
            int start = 0;
            for (int end = 1; end <= text.length() && allWords; end++) {
                if (end == text.length() || (cuts >> (end - 1) & 1) == 1) {
                    allWords = words.contains(text.substring(start, end));
                    start = end;
                }
            }
            if (allWords) {
                return true;
            }
        }
        return false;
    }

    /** "a" repeated 1 to 20 times. */
    private static List<String> runsOfA() {
        List<String> words = new ArrayList<>();
        for (int length = 1; length <= 20; length++) {
            words.add("a".repeat(length));
        }
        return words;
    }

    @Test(order = 9)
    void aLongMessageThatSplits() {
        // sea tide cart ridge, 18 times: seat and car both lead into dead ends
        String text = "seatidecartridge".repeat(18);
        expect(true, text, List.of("sea", "seat", "tide", "car", "cart", "ridge"));
    }

    @Test(order = 10, timeoutMs = 500)
    void aLongMessageThatCannotBeSplit() {
        // The one b needs a piece that contains it, and the only such words have two b's.
        String text = "a".repeat(149) + "b" + "a".repeat(150);
        List<String> words = runsOfA();
        words.add("bb");
        words.add("bab");
        expect(false, text, words);
    }

    @Test(order = 11, timeoutMs = 500)
    void aThousandWordsThatNeverCoverTheB() {
        // 20 runs of a, and 980 words with a c in them; the message has no c.
        Random random = new Random(42_108);
        Set<String> words = new LinkedHashSet<>(runsOfA());
        while (words.size() < 1000) {
            StringBuilder word = new StringBuilder(randomWord(random, 2 + random.nextInt(19), "abc"));
            word.setCharAt(random.nextInt(word.length()), 'c');
            words.add(word.toString());
        }
        String text = "a".repeat(150) + "b" + "a".repeat(149);
        expect(false, text, new ArrayList<>(words));
    }
}
