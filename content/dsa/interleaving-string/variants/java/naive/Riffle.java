/** Checks decks against a riffle shuffle of two piles. */
public class Riffle {

    /** Returns whether deck can come out of riffling left and right together, each pile keeping its order. */
    static boolean canShuffle(String left, String right, String deck) {
        if (left.length() + right.length() != deck.length()) {
            return false; // every card of both piles is used exactly once
        }
        return from(left, right, deck, 0, 0);
    }

    /** Whether the rest of deck is left from i on riffled with right from j on. */
    private static boolean from(String left, String right, String deck, int i, int j) {
        if (i == left.length() && j == right.length()) {
            return true; // both piles used up, and so is the deck, since the lengths add up
        }
        char card = deck.charAt(i + j);
        if (i < left.length() && left.charAt(i) == card && from(left, right, deck, i + 1, j)) {
            return true; // the card dropped from the left pile
        }
        // or from the right pile; when both piles show the card, both choices are tried
        return j < right.length() && right.charAt(j) == card && from(left, right, deck, i, j + 1);
    }
}
