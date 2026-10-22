import static kit.Check.assertEquals;

import java.util.Arrays;
import java.util.Random;
import kit.Test;

public class Phase1Tests {

    private static void expect(boolean want, int... spring) {
        String input = spring.length <= 50 ? Arrays.toString(spring) : spring.length + " platforms";
        assertEquals(want, Platforms.canReachEnd(spring.clone()), "spring = " + input);
    }

    @Test(order = 1)
    void sample() {
        expect(true, 3, 1, 4, 0, 0, 0, 2);
        expect(false, 1, 2, 0, 3, 0, 0, 0, 1);
        expect(false, 0, 6);
    }

    @Test(order = 2)
    void theLongestJumpCanBeTheWrongOne() {
        expect(true, 2, 3, 0, 0, 1); // the longest jump from 0 lands on platform 2, a dead end
        expect(true, 4, 0, 3, 0, 0, 0); // the longest jump lands on 4; the 3 on platform 2 gets there
    }

    @Test(order = 3)
    void aZeroCanBeJumpedOver() {
        expect(true, 3, 0, 0, 1, 0);
        expect(true, 1, 0); // a spring of 0 on the last platform does not matter
    }

    @Test(order = 4)
    void aSpringHelpsOnlyIfItsPlatformIsReached() {
        expect(false, 1, 0, 5, 0); // platform 2 would reach the end, but platform 1 is a dead end
    }

    @Test(order = 5)
    void onePlatform() {
        expect(true, 0);
        expect(true, 7);
    }

    @Test(order = 6)
    void landingExactlyOnTheLastPlatform() {
        expect(true, 3, 0, 0, 0);
        expect(true, 2, 0, 0);
        expect(false, 2, 0, 0, 0); // one platform short
    }

    @Test(order = 7)
    void randomShortRows() {
        Random random = new Random(1423);
        for (int round = 0; round < 300; round++) {
            int[] spring = new int[1 + random.nextInt(12)];
            for (int i = 0; i < spring.length; i++) {
                spring[i] = random.nextInt(5);
            }
            expect(everyJump(spring, 0), spring);
        }
    }

    /** Tries every sequence of jumps from platform i. */
    private static boolean everyJump(int[] spring, int i) {
        if (i == spring.length - 1) {
            return true;
        }
        for (int step = 1; step <= spring[i] && i + step < spring.length; step++) {
            if (everyJump(spring, i + step)) {
                return true;
            }
        }
        return false;
    }

    @Test(order = 8)
    void longRowsOfWeakSprings() {
        int[] spring = new int[100_000];
        Arrays.fill(spring, 1);
        expect(true, spring);
        spring[70_000] = 0;
        expect(false, spring);
    }

    @Test(order = 9, timeoutMs = 150)
    void aDeadEndInTheMiddle() {
        // Platforms 0 to 59,999 all land at most on platform 60,000, which has no spring. Every
        // platform after it could reach the end, but none of them can be reached.
        int n = 100_000;
        int[] spring = new int[n];
        for (int i = 0; i < 60_000; i++) {
            spring[i] = 60_000 - i;
        }
        for (int i = 60_001; i < n; i++) {
            spring[i] = n - 1 - i;
        }
        expect(false, spring);
    }

    @Test(order = 10, timeoutMs = 150)
    void aStrongFirstSpringOverDeadEnds() {
        // Platform 0 reaches the last platform at once. Platforms 1 to 99,997 all land at most on
        // platform 99,998, which has no spring, so none of them leads anywhere.
        int n = 100_000;
        int[] spring = new int[n];
        spring[0] = n - 1;
        for (int i = 1; i < n - 2; i++) {
            spring[i] = n - 2 - i;
        }
        expect(true, spring);
    }
}
