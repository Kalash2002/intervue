import static kit.Check.assertTrue;

import java.util.Arrays;
import kit.Test;

public class Phase1Tests {

    private static void expect(int[] temps, int... want) {
        int[] copy = temps.clone();
        int[] got = Forecast.daysToWarmer(temps);
        assertTrue(Arrays.equals(want, got), "expected " + Arrays.toString(want) + " but was " + Arrays.toString(got));
        assertTrue(Arrays.equals(copy, temps), "the input array must not be modified");
    }

    @Test(order = 1)
    void sample() {
        expect(new int[] {20, 18, 25, 19, 19, 30}, 2, 1, 3, 2, 1, 0);
    }

    @Test(order = 2)
    void emptyAndSingleDay() {
        expect(new int[] {});
        expect(new int[] {3}, 0);
    }

    @Test(order = 3)
    void equalTemperaturesAreNotWarmer() {
        expect(new int[] {5, 5, 5}, 0, 0, 0);
        expect(new int[] {3, 3, 4}, 2, 1, 0);
    }

    @Test(order = 4)
    void fallingTemperatures() {
        expect(new int[] {9, 8, 7}, 0, 0, 0);
    }

    @Test(order = 5)
    void risingTemperatures() {
        expect(new int[] {1, 2, 3, 4}, 1, 1, 1, 0);
    }

    @Test(order = 6)
    void warmerDayIsFarAway() {
        expect(new int[] {12, 7, 9, 8, 15, 4, 5}, 4, 1, 2, 1, 0, 1, 0);
    }

    @Test(order = 7)
    void oneHotDayAnswersManyWaitingDays() {
        expect(new int[] {5, 4, 3, 2, 1, 9}, 5, 4, 3, 2, 1, 0);
    }

    @Test(order = 8)
    void negativeTemperatures() {
        expect(new int[] {-5, -10, -3}, 2, 1, 0);
    }

    @Test(order = 9, timeoutMs = 400)
    void largeAllEqual() {
        int[] temps = new int[200_000];
        Arrays.fill(temps, 7);
        assertTrue(Arrays.equals(new int[200_000], Forecast.daysToWarmer(temps)), "an all-equal row has no warmer day");
    }

    @Test(order = 10, timeoutMs = 400)
    void largeFlatRunThenOneWarmerDay() {
        int n = 200_000;
        int[] temps = new int[n];
        Arrays.fill(temps, 10);
        temps[n - 1] = 11;
        int[] want = new int[n];
        for (int i = 0; i < n - 1; i++) {
            want[i] = n - 1 - i;
        }
        assertTrue(Arrays.equals(want, Forecast.daysToWarmer(temps)), "every flat day waits for the last day");
    }
}
