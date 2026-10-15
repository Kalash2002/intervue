import static kit.Check.assertEquals;

import kit.Test;

public class Phase1Tests {

    @Test(order = 1)
    void sample() {
        assertEquals(true, Alerts.repeatsNearby(new int[] {4, 9, 4}, 2));
        assertEquals(false, Alerts.repeatsNearby(new int[] {4, 9, 4}, 1));
    }

    @Test(order = 2)
    void noCodesOrOneCode() {
        assertEquals(false, Alerts.repeatsNearby(new int[] {}, 5));
        assertEquals(false, Alerts.repeatsNearby(new int[] {8}, 5));
    }

    @Test(order = 3)
    void gapOfZeroNeverMatches() {
        assertEquals(false, Alerts.repeatsNearby(new int[] {7, 7}, 0));
    }

    @Test(order = 4)
    void neighboursWithGapOne() {
        assertEquals(true, Alerts.repeatsNearby(new int[] {5, 5}, 1));
        assertEquals(true, Alerts.repeatsNearby(new int[] {1, 2, 3, 3}, 1));
    }

    @Test(order = 5)
    void repeatsAreTooFarApartUntilTheGapReachesThem() {
        int[] codes = {9, 4, 7, 9, 4};
        assertEquals(false, Alerts.repeatsNearby(codes, 2));
        assertEquals(true, Alerts.repeatsNearby(codes, 3));
    }

    @Test(order = 6)
    void extremeAndNegativeValues() {
        assertEquals(true, Alerts.repeatsNearby(new int[] {Integer.MIN_VALUE, 0, Integer.MIN_VALUE}, 2));
        assertEquals(false, Alerts.repeatsNearby(new int[] {-3, 3, -2, 2, Integer.MAX_VALUE}, 4));
    }

    @Test(order = 7)
    void onlyTheLatestPairIsCloseEnough() {
        // 5 sits at positions 0, 5 and 7: the pair (5, 7) is 2 apart, the others are 5 and 7 apart
        int[] codes = {5, 1, 2, 3, 4, 5, 6, 5};
        assertEquals(true, Alerts.repeatsNearby(codes, 2));
        assertEquals(false, Alerts.repeatsNearby(codes, 1));
    }

    @Test(order = 8, timeoutMs = 320)
    void hundredThousandDistinctCodes() {
        int n = 100_000;
        int[] codes = new int[n];
        for (int i = 0; i < n; i++) {
            codes[i] = 3 * i; // all distinct, so every position has to be examined
        }
        assertEquals(false, Alerts.repeatsNearby(codes, 99_999));
    }
}
