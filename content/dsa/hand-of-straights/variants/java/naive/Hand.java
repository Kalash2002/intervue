import java.util.Arrays;

/** Decides whether a hand of numbered cards can be laid out as straights. */
public class Hand {

    /** Returns true if every card can be put in a straight of size consecutive numbers. */
    static boolean canSplit(int[] cards, int size) {
        int[] sorted = cards.clone();
        Arrays.sort(sorted);
        boolean[] used = new boolean[sorted.length];
        for (int first = 0; first < sorted.length; first++) {
            if (used[first]) {
                continue;
            }
            used[first] = true; // the smallest card left must start a straight
            int at = first;
            for (int want = sorted[first] + 1; want < sorted[first] + size; want++) {
                while (at < sorted.length && (used[at] || sorted[at] < want)) {
                    at++; // skip cards already taken and smaller numbers
                }
                if (at == sorted.length || sorted[at] != want) {
                    return false; // no card left with the next number
                }
                used[at] = true;
            }
        }
        return true;
    }
}
