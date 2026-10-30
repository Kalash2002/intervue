import java.util.HashSet;
import java.util.Set;

/** Flags order codes that repeat too close together. */
public class Alerts {

    /** Returns true if two equal codes sit at positions i < j with j - i <= gap. */
    static boolean repeatsNearby(int[] codes, int gap) {
        Set<Integer> window = new HashSet<>(); // codes at the previous `gap` positions
        for (int j = 0; j < codes.length; j++) {
            if (j > gap) {
                window.remove(codes[j - gap - 1]); // this position is now gap + 1 places back
            }
            if (!window.add(codes[j])) {
                return true;
            }
        }
        return false;
    }
}
