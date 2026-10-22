import java.util.Arrays;

/** Decides whether some lighting presets overlay to an exact colour. */
public class Lights {

    /** Returns true if overlaying some of the presets, each channel at its highest level, gives target. */
    static boolean canMix(int[][] presets, int[] target) {
        int[] mix = new int[3]; // the overlay of every preset that fits under the target
        for (int[] preset : presets) {
            if (preset[0] <= target[0] && preset[1] <= target[1] && preset[2] <= target[2]) {
                for (int c = 0; c < 3; c++) {
                    mix[c] = Math.max(mix[c], preset[c]);
                }
            }
        }
        return Arrays.equals(mix, target); // levels start at 1, so an empty mix never matches
    }
}
