import static kit.Check.assertEquals;

import java.util.Arrays;
import java.util.Random;
import kit.Test;

public class Phase1Tests {

    private static final int[] TARGET = {700, 400, 900};

    private static void expect(boolean want, int[] target, int[][] presets) {
        String input = presets.length <= 20 ? Arrays.deepToString(presets) : presets.length + " presets";
        int[][] copy = new int[presets.length][];
        for (int i = 0; i < presets.length; i++) {
            copy[i] = presets[i].clone();
        }
        assertEquals(want, Lights.canMix(copy, target.clone()), "presets = " + input + ", target = " + Arrays.toString(target));
    }

    /** The levels three at a time, as a list of presets. */
    private static int[][] presets(int... levels) {
        int[][] presets = new int[levels.length / 3][];
        for (int i = 0; i < presets.length; i++) {
            presets[i] = Arrays.copyOfRange(levels, 3 * i, 3 * i + 3);
        }
        return presets;
    }

    private static int[] target(int red, int green, int blue) {
        return new int[] {red, green, blue};
    }

    @Test(order = 1)
    void sample() {
        expect(true, target(60, 30, 90), presets(40, 10, 90, 60, 20, 30, 50, 80, 10, 20, 30, 70));
        expect(false, target(35, 40, 25), presets(15, 40, 25, 35, 10, 50, 30, 20, 45));
        expect(false, target(3, 8, 9), presets(3, 8, 8));
    }

    @Test(order = 2)
    void overlayingEveryPresetOvershoots() {
        expect(true, target(60, 40, 80), presets(60, 40, 80, 90, 30, 20)); // leave the 90 out
        expect(true, target(80, 90, 60), presets(30, 90, 60, 80, 40, 60, 90, 90, 90));
    }

    @Test(order = 3)
    void aLevelOnAPresetThatOvershootsDoesNotCount() {
        // green 70 is only on the second preset, which brings blue 90
        expect(false, target(70, 70, 70), presets(70, 30, 20, 30, 70, 90, 10, 10, 70));
        // green 60 is only on the second preset, which brings blue 70
        expect(false, target(50, 60, 40), presets(50, 10, 10, 30, 60, 70, 30, 70, 40));
    }

    @Test(order = 4)
    void aLevelEqualToTheTargetFits() {
        expect(true, target(40, 90, 60), presets(40, 90, 20, 40, 30, 60));
    }

    @Test(order = 5)
    void severalPresetsAreNeeded() {
        expect(true, target(120, 140, 160), presets(120, 10, 10, 10, 140, 10, 10, 10, 160));
        expect(true, target(200, 300, 250), presets(10, 300, 250, 200, 300, 10));
    }

    @Test(order = 6)
    void levelsBelowTheTargetCannotRise() {
        expect(false, target(30, 60, 30), presets(10, 30, 10, 30, 10, 30));
    }

    @Test(order = 7)
    void onePreset() {
        expect(true, target(1000, 1000, 1000), presets(1000, 1000, 1000));
        expect(true, target(1, 1000, 1), presets(1, 1000, 1));
        expect(false, target(1000, 2, 1000), presets(1000, 1, 1000));
    }

    @Test(order = 8)
    void randomSmallDesks() {
        Random random = new Random(1427);
        for (int round = 0; round < 300; round++) {
            int[][] presets = new int[1 + random.nextInt(8)][3];
            for (int[] preset : presets) {
                for (int c = 0; c < 3; c++) {
                    preset[c] = 20 + random.nextInt(5);
                }
            }
            int[] target = new int[3];
            for (int c = 0; c < 3; c++) {
                target[c] = 20 + random.nextInt(5);
            }
            if (round % 2 == 0) { // the overlay of a random non-empty set, so the answer is true
                int set = 1 + random.nextInt((1 << presets.length) - 1);
                target = overlay(presets, set);
            }
            expect(everySet(presets, target), target, presets);
        }
    }

    /** Tries every non-empty set of presets. */
    private static boolean everySet(int[][] presets, int[] target) {
        for (int set = 1; set < 1 << presets.length; set++) {
            if (Arrays.equals(overlay(presets, set), target)) {
                return true;
            }
        }
        return false;
    }

    private static int[] overlay(int[][] presets, int set) {
        int[] mix = new int[3];
        for (int p = 0; p < presets.length; p++) {
            if ((set >> p & 1) == 1) {
                for (int c = 0; c < 3; c++) {
                    mix[c] = Math.max(mix[c], presets[p][c]);
                }
            }
        }
        return mix;
    }

    /** 100,000 presets around TARGET: half fit under it but reach none of its levels, half overshoot. */
    private static int[][] desk(long seed) {
        Random random = new Random(seed);
        int[][] presets = new int[100_000][];
        for (int i = 0; i < presets.length; i++) {
            int[] preset = {10 + random.nextInt(690), 10 + random.nextInt(390), 10 + random.nextInt(890)};
            if (random.nextBoolean()) { // one target level, but above the target in another channel
                int has = random.nextInt(3);
                int over = (has + 1 + random.nextInt(2)) % 3;
                preset[has] = TARGET[has];
                preset[over] = TARGET[over] + 1 + random.nextInt(1000 - TARGET[over]);
            }
            presets[i] = preset;
        }
        return presets;
    }

    @Test(order = 9, timeoutMs = 400)
    void threeFittingPresetsInTheMiddle() {
        // Only presets 40,000, 50,000 and 60,000 fit under the target with one of its levels each.
        int[][] presets = desk(27);
        presets[40_000] = new int[] {700, 123, 456};
        presets[50_000] = new int[] {321, 400, 654};
        presets[60_000] = new int[] {234, 345, 900};
        expect(true, TARGET, presets);
    }

    @Test(order = 10, timeoutMs = 400)
    void blueOnlyOnPresetsThatOvershoot() {
        // Presets 40,000 and 50,000 fit and give red 700 and green 400, but every preset with blue
        // 900 goes above the target elsewhere; preset 60,000 misses by one on green.
        int[][] presets = desk(127);
        presets[40_000] = new int[] {700, 123, 456};
        presets[50_000] = new int[] {321, 400, 654};
        presets[60_000] = new int[] {234, 401, 900};
        expect(false, TARGET, presets);
    }
}
