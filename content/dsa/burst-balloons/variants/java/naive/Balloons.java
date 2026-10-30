import java.util.*;

/** Picks the order in which to burst a row of numbered balloons. */
public class Balloons {

    /** Returns the most coins any order of bursting every balloon in values pays. */
    static int mostCoins(int[] values) {
        List<Integer> row = new ArrayList<>();
        for (int value : values) {
            row.add(value);
        }
        return mostFrom(row);
    }

    /** The most coins from bursting every balloon left in row, trying each one as the next. */
    private static int mostFrom(List<Integer> row) {
        int most = 0;
        for (int k = 0; k < row.size(); k++) {
            int left = k > 0 ? row.get(k - 1) : 1; // a side with nothing left counts as 1
            int right = k + 1 < row.size() ? row.get(k + 1) : 1;
            int value = row.remove(k);
            most = Math.max(most, left * value * right + mostFrom(row));
            row.add(k, value); // put it back before trying the next balloon
        }
        return most;
    }
}
