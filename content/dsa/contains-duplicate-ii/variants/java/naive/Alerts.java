/** Flags order codes that repeat too close together. */
public class Alerts {

    /** Returns true if two equal codes sit at positions i < j with j - i <= gap. */
    static boolean repeatsNearby(int[] codes, int gap) {
        for (int i = 0; i < codes.length; i++) {
            int last = Math.min(codes.length - 1, i + gap);
            for (int j = i + 1; j <= last; j++) {
                if (codes[i] == codes[j]) {
                    return true;
                }
            }
        }
        return false;
    }
}
