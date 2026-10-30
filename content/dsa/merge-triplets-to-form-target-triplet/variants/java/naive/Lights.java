import java.util.Arrays;

/** Decides whether some lighting presets overlay to an exact colour. */
public class Lights {

    /** Returns true if overlaying some of the presets, each channel at its highest level, gives target. */
    static boolean canMix(int[][] presets, int[] target) {
        int n = presets.length;
        boolean[] in = new boolean[n]; // the set being tried, as a binary counter
        while (true) {
            int i = 0;
            while (i < n && in[i]) { // add one to the counter
                in[i++] = false;
            }
            if (i == n) {
                return false; // the counter wrapped round: every non-empty set has been tried
            }
            in[i] = true;
            int[] mix = new int[3];
            for (int p = 0; p < n; p++) {
                if (in[p]) {
                    for (int c = 0; c < 3; c++) {
                        mix[c] = Math.max(mix[c], presets[p][c]);
                    }
                }
            }
            if (Arrays.equals(mix, target)) {
                return true;
            }
        }
    }
}
