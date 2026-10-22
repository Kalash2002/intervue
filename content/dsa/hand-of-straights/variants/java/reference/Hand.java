import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/** Decides whether a hand of numbered cards can be laid out as straights. */
public class Hand {

    /** Returns true if every card can be put in a straight of size consecutive numbers. */
    static boolean canSplit(int[] cards, int size) {
        Map<Integer, Integer> left = new HashMap<>(); // number -> copies not yet in a straight
        for (int card : cards) {
            left.merge(card, 1, Integer::sum);
        }
        int[] numbers = new int[left.size()];
        int d = 0;
        for (int number : left.keySet()) {
            numbers[d++] = number;
        }
        Arrays.sort(numbers); // only the distinct numbers need to be in order
        for (int number : numbers) {
            int starts = left.get(number); // every copy of the lowest number left starts its own straight
            for (int next = number + 1; next < number + size && starts > 0; next++) {
                int copies = left.getOrDefault(next, 0);
                if (copies < starts) {
                    return false; // those straights lack copies of next
                }
                left.put(next, copies - starts);
            }
        }
        return true;
    }
}
