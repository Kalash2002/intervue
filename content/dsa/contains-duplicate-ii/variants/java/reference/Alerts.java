import java.util.HashMap;
import java.util.Map;

/** Flags order codes that repeat too close together. */
public class Alerts {

    /** Returns true if two equal codes sit at positions i < j with j - i <= gap. */
    static boolean repeatsNearby(int[] codes, int gap) {
        Map<Integer, Integer> lastSeen = new HashMap<>();
        for (int j = 0; j < codes.length; j++) {
            Integer last = lastSeen.get(codes[j]);
            if (last != null && j - last <= gap) {
                return true;
            }
            lastSeen.put(codes[j], j);
        }
        return false;
    }
}
