/** Checks decks against a riffle shuffle of two piles. */
public class Riffle {

    /** Returns whether deck can come out of riffling left and right together, each pile keeping its order. */
    static boolean canShuffle(String left, String right, String deck) {
        if (left.length() + right.length() != deck.length()) {
            return false; // every card of both piles is used exactly once
        }
        // can[j]: the first i cards of left and the first j of right can make the first i + j of deck
        boolean[] can = new boolean[right.length() + 1];
        for (int i = 0; i <= left.length(); i++) {
            for (int j = 0; j <= right.length(); j++) {
                if (i == 0 && j == 0) {
                    can[0] = true; // no cards from either pile make the empty start of the deck
                    continue;
                }
                char card = deck.charAt(i + j - 1);
                boolean lastFromLeft = i > 0 && can[j] && left.charAt(i - 1) == card; // can[j] still holds row i - 1
                boolean lastFromRight = j > 0 && can[j - 1] && right.charAt(j - 1) == card; // can[j - 1] holds row i
                can[j] = lastFromLeft || lastFromRight;
            }
        }
        return can[right.length()];
    }
}
