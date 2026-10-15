import java.util.ArrayList;
import java.util.List;

/** Keeps a sorted list of non-overlapping bookings. */
public class Bookings {

    /**
     * Returns booked with added inserted, merging every range that overlaps or touches it.
     * booked is sorted by start with no overlaps; it must not be modified.
     */
    static int[][] insert(int[][] booked, int[] added) {
        List<int[]> result = new ArrayList<>(booked.length + 1);
        int i = 0;
        while (i < booked.length && booked[i][1] < added[0]) { // ends before the new range starts
            result.add(booked[i++]);
        }
        int start = added[0];
        int end = added[1];
        while (i < booked.length && booked[i][0] <= end) { // reaches the new range: absorb it
            start = Math.min(start, booked[i][0]);
            end = Math.max(end, booked[i][1]);
            i++;
        }
        result.add(new int[] {start, end});
        while (i < booked.length) { // starts after the merged range ends
            result.add(booked[i++]);
        }
        return result.toArray(new int[0][]);
    }
}
