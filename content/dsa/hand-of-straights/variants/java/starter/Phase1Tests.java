import static kit.Check.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import kit.Test;

public class Phase1Tests {

    private static void expect(boolean want, int size, int... cards) {
        String input = cards.length <= 50 ? Arrays.toString(cards) : cards.length + " cards";
        assertEquals(want, Hand.canSplit(cards.clone(), size), "cards = " + input + ", size = " + size);
    }

    @Test(order = 1)
    void sample() {
        expect(true, 3, 32, 36, 31, 33, 30, 32, 35, 31, 33, 34, 31, 32);
        expect(false, 3, 10, 12, 11, 13, 11, 14);
        expect(false, 3, 4, 4, 4);
    }

    @Test(order = 2)
    void aStraightStartedAboveTheSmallestCardFails() {
        // 23, 24, 25 from the first card leaves 22 without a 23; the split is 22-24 and 25-27
        expect(true, 3, 23, 27, 22, 25, 24, 26);
        // 13, 14 from the first card leaves 15 without a 16; the split is 12-13 and 14-15
        expect(true, 2, 13, 15, 12, 14);
    }

    @Test(order = 3)
    void blocksOfTheSortedHandAreNotStraights() {
        expect(true, 2, 31, 30, 31, 30); // sorted 30, 30, 31, 31: two straights 30, 31
        expect(true, 3, 41, 42, 40, 41, 42, 40);
    }

    @Test(order = 4)
    void aGapBreaksAStraight() {
        expect(false, 3, 3, 5, 4, 7, 6, 9); // 6, 7 and then 9, not 8
        expect(false, 2, 100, 102);
    }

    @Test(order = 5)
    void cardsLeftOver() {
        expect(false, 2, 20, 21, 21, 22, 22); // five cards cannot make pairs
    }

    @Test(order = 6)
    void repeatsNeedStraightsOfTheirOwn() {
        expect(false, 4, 50, 50, 51, 51);
        expect(true, 3, 7, 8, 7, 8, 9, 9);
        expect(false, 2, 7, 7, 8, 9); // the second 7 has no 8 left
        expect(false, 2, 5, 5, 6, 7, 7, 8); // two straights start at 5, but there is one 6
    }

    @Test(order = 7)
    void sizeOneOrTheWholeHand() {
        expect(true, 1, 1_000_000_000, 0, 1_000_000_000);
        expect(true, 1, 6);
        expect(true, 3, 12, 10, 11);
        expect(false, 3, 12, 10, 14);
        expect(false, 2, 0, 1_000_000_000);
    }

    @Test(order = 8)
    void randomSmallHands() {
        Random random = new Random(1523);
        for (int round = 0; round < 300; round++) {
            int[] cards;
            int size;
            if (round % 2 == 0) { // straights dealt on purpose, then shuffled, sometimes one card changed
                size = 1 + random.nextInt(4);
                int count = 1 + random.nextInt(12 / size);
                cards = new int[count * size];
                for (int s = 0; s < count; s++) {
                    int low = 40 + random.nextInt(6);
                    for (int k = 0; k < size; k++) {
                        cards[s * size + k] = low + k;
                    }
                }
                if (random.nextInt(3) == 0) {
                    cards[random.nextInt(cards.length)] = 40 + random.nextInt(9);
                }
                shuffle(cards, random);
            } else {
                cards = new int[1 + random.nextInt(12)];
                for (int i = 0; i < cards.length; i++) {
                    cards[i] = 40 + random.nextInt(6);
                }
                size = 1 + random.nextInt(Math.min(4, cards.length));
            }
            List<Integer> hand = new ArrayList<>();
            for (int card : cards) {
                hand.add(card);
            }
            expect(everySplit(hand, size), size, cards);
        }
    }

    /** Puts the first card left into every straight that could hold it, then splits the rest. */
    private static boolean everySplit(List<Integer> left, int size) {
        if (left.isEmpty()) {
            return true;
        }
        int card = left.get(0);
        for (int place = 0; place < size; place++) {
            List<Integer> rest = new ArrayList<>(left);
            boolean whole = true;
            for (int number = card - place; number < card - place + size && whole; number++) {
                whole = rest.remove(Integer.valueOf(number));
            }
            if (whole && everySplit(rest, size)) {
                return true;
            }
        }
        return false;
    }

    private static void shuffle(int[] cards, Random random) {
        for (int i = cards.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int card = cards[i];
            cards[i] = cards[j];
            cards[j] = card;
        }
    }

    @Test(order = 9, timeoutMs = 200)
    void manyCopiesOfThreeNumbers() {
        // 33,333 copies each of 999,999,998, 999,999,999 and 1,000,000,000, shuffled: 33,333
        // straights of the three.
        int[] cards = new int[99_999];
        for (int i = 0; i < cards.length; i++) {
            cards[i] = 999_999_998 + i % 3;
        }
        shuffle(cards, new Random(26));
        expect(true, 3, cards);
    }

    @Test(order = 10, timeoutMs = 200)
    void twoMoreSevensThanEights() {
        // 50,001 sevens and 49,999 eights, shuffled. Pairs of 7, 8 use up every 8, and the two
        // sevens left over have no 8; taking the largest card first fails on them just the same.
        int[] cards = new int[100_000];
        for (int i = 0; i < cards.length; i++) {
            cards[i] = i < 50_001 ? 7 : 8;
        }
        shuffle(cards, new Random(126));
        expect(false, 2, cards);
    }
}
