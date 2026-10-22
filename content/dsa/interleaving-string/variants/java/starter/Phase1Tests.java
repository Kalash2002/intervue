import static kit.Check.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import kit.Test;

public class Phase1Tests {

    private static void expect(boolean want, String left, String right, String deck) {
        String input = "left = \"" + abbreviate(left) + "\", right = \"" + abbreviate(right) + "\", deck = \"" + abbreviate(deck) + "\"";
        assertEquals(want, Riffle.canShuffle(left, right, deck), input);
    }

    private static String abbreviate(String s) {
        return s.length() <= 40 ? s : s.substring(0, 20) + "... (" + s.length() + " cards)";
    }

    @Test(order = 1)
    void sample() {
        expect(true, "lime", "leek", "limleeke");
        expect(false, "pear", "plum", "plumepar");
        expect(false, "fig", "pea", "fpeig");
    }

    @Test(order = 2)
    void takingTheCardFromOnePileWhenBothMatchCanBeWrong() {
        // both piles start with l; preferring either pile when both show the card fails
        expect(true, "leek", "lime", "limleeke");
        // kiwi's i and fig's i: k, then f, i from fig, then w, i from kiwi, g from fig, i from kiwi
        expect(true, "kiwi", "fig", "kfiwigi");
        expect(true, "fig", "kiwi", "kfiwigi");
    }

    @Test(order = 3)
    void eachPileOnItsOwnIsNotEnough() {
        // both piles can be found in the deck, but not in cards that do not overlap
        expect(false, "plum", "pear", "plumepar");
        expect(false, "pear", "date", "padeater");
        expect(false, "date", "pear", "padeater");
    }

    @Test(order = 4)
    void theRightCardsInTheWrongOrder() {
        expect(false, "kale", "", "lake");
    }

    @Test(order = 5)
    void everyCardMustBeUsedExactlyOnce() {
        expect(false, "fig", "pea", "figpeas"); // one card too many, after both piles are used up
        expect(false, "fig", "pea", "figpe"); // the deck runs out before the a of pea
    }

    @Test(order = 6)
    void anEmptyPile() {
        expect(true, "", "kale", "kale");
        expect(true, "okra", "", "okra");
    }

    @Test(order = 7)
    void randomSmallDecksAgainstEverySplit() {
        Random random = new Random(1119);
        for (int round = 0; round < 300; round++) {
            String left = cards(random, random.nextInt(7));
            String right = cards(random, (left.isEmpty() ? 1 : 0) + random.nextInt(7)); // never both empty
            String deck = round % 2 == 0 ? riffle(random, left, right) : shuffle(random, left + right);
            expect(everySplit(left, right, deck), left, right, deck);
        }
    }

    private static String cards(Random random, int length) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < length; i++) {
            s.append(random.nextBoolean() ? 'a' : 'b');
        }
        return s.toString();
    }

    private static String riffle(Random random, String left, String right) {
        StringBuilder deck = new StringBuilder();
        int i = 0;
        int j = 0;
        while (i < left.length() || j < right.length()) {
            boolean fromLeft = j == right.length() || (i < left.length() && random.nextBoolean());
            deck.append(fromLeft ? left.charAt(i++) : right.charAt(j++));
        }
        return deck.toString();
    }

    private static String shuffle(Random random, String cards) {
        List<Character> list = new ArrayList<>();
        for (char c : cards.toCharArray()) {
            list.add(c);
        }
        Collections.shuffle(list, random);
        StringBuilder s = new StringBuilder();
        for (char c : list) {
            s.append(c);
        }
        return s.toString();
    }

    /** Tries every choice of the deck positions that come from left, in order, and checks both piles. */
    private static boolean everySplit(String left, String right, String deck) {
        if (left.length() + right.length() != deck.length()) {
            return false;
        }
        for (int mask = 0; mask < 1 << deck.length(); mask++) {
            if (Integer.bitCount(mask) != left.length()) {
                continue;
            }
            StringBuilder fromLeft = new StringBuilder();
            StringBuilder fromRight = new StringBuilder();
            for (int k = 0; k < deck.length(); k++) {
                ((mask >> k & 1) == 1 ? fromLeft : fromRight).append(deck.charAt(k));
            }
            if (fromLeft.toString().equals(left) && fromRight.toString().equals(right)) {
                return true;
            }
        }
        return false;
    }

    @Test(order = 8)
    void fullPilesWithNoCardInCommon() {
        // left uses a to m and right n to z, so the deck says which pile each card came from
        Random random = new Random(808);
        String left = letters(random, 'a', 100);
        String right = letters(random, 'n', 100);
        StringBuilder deck = new StringBuilder(riffle(random, left, right));
        expect(true, left, right, deck.toString());
        // swap the first two cards of the left pile that differ, where they lie in the deck
        List<Integer> fromLeft = new ArrayList<>();
        for (int k = 0; k < deck.length(); k++) {
            if (deck.charAt(k) < 'n') {
                fromLeft.add(k);
            }
        }
        int t = 0;
        while (left.charAt(t) == left.charAt(t + 1)) {
            t++;
        }
        deck.setCharAt(fromLeft.get(t), left.charAt(t + 1));
        deck.setCharAt(fromLeft.get(t + 1), left.charAt(t));
        expect(false, left, right, deck.toString());
    }

    private static String letters(Random random, char from, int length) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < length; i++) {
            s.append((char) (from + random.nextInt(13)));
        }
        return s.toString();
    }

    @Test(order = 9, timeoutMs = 300)
    void aCardOutOfPlaceDeepInTheDeck() {
        // the b of left sits after its 50th card but comes last in the deck, after all 199 a's
        expect(false, "a".repeat(50) + "b" + "a".repeat(49), "a".repeat(100), "a".repeat(199) + "b");
    }

    @Test(order = 10, timeoutMs = 300)
    void theSamePilesTheOtherWayRound() {
        expect(false, "a".repeat(100), "a".repeat(50) + "b" + "a".repeat(49), "a".repeat(199) + "b");
    }

    @Test(order = 11, timeoutMs = 300)
    void theLastCardCannotComeFromEitherPile() {
        // both piles end with b, but the deck ends with an a
        expect(false, "ab".repeat(50), "ab".repeat(50), "ab".repeat(99) + "ba");
    }
}
