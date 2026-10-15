import static kit.Check.assertEquals;

import kit.Test;

public class Phase1Tests {

    @Test(order = 1)
    void sample() {
        assertEquals(11, Trader.bestProfit(new int[] {9, 4, 12, 6, 15, 3}));
    }

    @Test(order = 2)
    void tooFewDaysToTrade() {
        assertEquals(0, Trader.bestProfit(new int[] {}));
        assertEquals(0, Trader.bestProfit(new int[] {7}));
    }

    @Test(order = 3)
    void fallingPrices() {
        assertEquals(0, Trader.bestProfit(new int[] {8, 6, 5, 2}));
    }

    @Test(order = 4)
    void flatPrices() {
        assertEquals(0, Trader.bestProfit(new int[] {5, 5, 5}));
    }

    @Test(order = 5)
    void risingPrices() {
        assertEquals(3, Trader.bestProfit(new int[] {1, 2, 3, 4}));
    }

    @Test(order = 6)
    void highestPriceComesBeforeTheLowest() {
        assertEquals(2, Trader.bestProfit(new int[] {6, 5, 4, 1, 3}));
    }

    @Test(order = 7)
    void bestBuyIsNotTheGlobalLow() {
        assertEquals(10, Trader.bestProfit(new int[] {2, 12, 1, 6}));
    }

    @Test(order = 8)
    void sellOnTheLastDay() {
        assertEquals(8, Trader.bestProfit(new int[] {9, 3, 7, 2, 10}));
    }

    @Test(order = 9, timeoutMs = 400)
    void largeNonIncreasing() {
        int[] prices = new int[200_000];
        for (int i = 0; i < prices.length; i++) {
            prices[i] = 10_000 - i / 20;
        }
        assertEquals(0, Trader.bestProfit(prices));
    }

    @Test(order = 10, timeoutMs = 400)
    void largeSawtooth() {
        int[] prices = new int[200_000];
        for (int i = 0; i < prices.length; i++) {
            prices[i] = i % 10_000; // climbs from 0 to 9999, then drops back to 0
        }
        assertEquals(9_999, Trader.bestProfit(prices));
    }
}
