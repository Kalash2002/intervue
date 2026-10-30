/** Positions in a sorted array. */
public class SortedSlots {

    static int insertAt(int[] sorted, int target) {
        int i = 0;
        while (i < sorted.length && sorted[i] < target) {
            i++;
        }
        return i;
    }
}
