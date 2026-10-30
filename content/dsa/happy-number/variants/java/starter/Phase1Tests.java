import static kit.Check.assertEquals;

import java.util.Random;
import kit.Test;

public class Phase1Tests {

    private static final int MAX = Integer.MAX_VALUE;

    private static void expect(boolean want, int n) {
        assertEquals(want, DigitChain.isHappy(n), "isHappy(" + n + ")");
    }

    /** The test's own answer: every chain that is not happy passes through 4, so it stops at 1 or at 4. */
    private static boolean happy(int n) {
        while (n != 1 && n != 4) {
            int sum = 0;
            for (int rest = n; rest > 0; rest /= 10) {
                sum += (rest % 10) * (rest % 10);
            }
            n = sum;
        }
        return n == 1;
    }

    @Test(order = 1, timeoutMs = 1000)
    void sample() {
        expect(true, 356);
        expect(false, 85);
        expect(true, 1111111);
    }

    @Test(order = 2, timeoutMs = 1000)
    void oneAndThePowersOfTen() {
        expect(true, 1);
        expect(true, 10);
        expect(true, 100);
        expect(true, 1000000);
        expect(true, 1000000000);
        expect(true, 1111111111); // ten 1s give 10
    }

    @Test(order = 3, timeoutMs = 1000)
    void singleDigits() {
        // only 1 and 7 are happy among the single digits
        expect(true, 7);
        expect(false, 3);
        expect(false, 4);
        expect(false, 5);
        expect(false, 6);
        expect(false, 8);
        expect(false, 9);
    }

    @Test(order = 4, timeoutMs = 1000)
    void aSevenOnTheWayIsNotTheEnd() {
        // each reaches 7 after one step, and 7 goes on to 49, 97, 130, 10 and 1
        expect(true, 1112);
        expect(true, 2111);
        expect(true, 1121000);
        // these reach another single digit, and are not happy
        expect(false, 1111);
        expect(false, 111);
        expect(false, 1210);
    }

    @Test(order = 5, timeoutMs = 1000)
    void theLongestHappyChains() {
        // six steps to 1: 653 and 3056 give 70, then 49, 97, 130, 10, 1
        expect(true, 653);
        expect(true, 3056);
        // seven steps to 1, the most any int needs: each gives 356 first
        expect(true, 78999);
        expect(true, 99987);
        expect(true, 979890);
    }

    @Test(order = 6, timeoutMs = 1000)
    void unhappyChainsGoRoundALoop() {
        // the loop itself: 4, 16, 37, 58, 89, 145, 42, 20
        expect(false, 16);
        expect(false, 37);
        expect(false, 58);
        expect(false, 89);
        expect(false, 145);
        expect(false, 42);
        // long ways into the loop: 692 needs eleven steps (121, 6, 36, 45, 41, 17, 50, 25, 29, 85, 89)
        expect(false, 692);
        expect(false, 2962);
        expect(false, 99999);
    }

    @Test(order = 7, timeoutMs = 1000)
    void theLargestInts() {
        expect(false, MAX); // the first step gives 260
        expect(false, MAX - 1);
        expect(true, MAX - 2); // 2147483645 is the largest happy int: 236, 49, 97, 130, 10, 1
        expect(false, 2000000000);
        expect(false, 1999999999); // 1 + 9 * 81 = 730, the largest value after one step
    }

    @Test(order = 8, timeoutMs = 2000)
    void everyStartUpToOneHundredThousand() {
        int count = 0;
        for (int n = 1; n <= 100000; n++) {
            boolean want = happy(n);
            expect(want, n);
            count += want ? 1 : 0;
        }
        assertEquals(14377, count, "happy numbers from 1 to 100000");
    }

    @Test(order = 9, timeoutMs = 2000)
    void theTopOneHundredThousandInts() {
        int count = 0;
        for (int below = 0; below < 100000; below++) { // counting down, so that the loop never passes MAX
            int n = MAX - below;
            boolean want = happy(n);
            expect(want, n);
            count += want ? 1 : 0;
        }
        assertEquals(13386, count, "happy numbers from 2147383648 to 2147483647");
    }

    @Test(order = 10, timeoutMs = 2000)
    void randomStarts() {
        Random random = new Random(1441);
        for (int round = 0; round < 100000; round++) {
            int n = 1 + random.nextInt(MAX);
            expect(happy(n), n);
        }
    }
}
