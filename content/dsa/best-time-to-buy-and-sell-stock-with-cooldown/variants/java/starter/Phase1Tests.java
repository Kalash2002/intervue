import static kit.Check.assertEquals;

import java.util.Arrays;
import java.util.Random;
import kit.Test;

public class Phase1Tests {

    private static void expect(int want, int[] prices) {
        String input = prices.length <= 20 ? Arrays.toString(prices) : prices.length + " days";
        assertEquals(want, Desk.bestProfit(prices.clone()), "prices = " + input);
    }

    @Test(order = 1)
    void sample() {
        expect(16, new int[] {6, 2, 9, 4, 8, 3, 12});
        expect(9, new int[] {2, 7, 6, 11});
        expect(0, new int[] {8, 8, 8, 8});
    }

    @Test(order = 2)
    void theRestDayRulesOutBackToBackTrades() {
        // 1 to 4 and 2 to 7 would make 8, but selling at 4 makes day 2 a rest day
        expect(6, new int[] {1, 4, 2, 7});
    }

    @Test(order = 3)
    void sellingADayBeforeThePeakCanPay() {
        // 0 to 3, rest on the 11, then 0 to 9; holding 0 to 11 leaves the next 0 as a rest day
        expect(12, new int[] {11, 2, 0, 3, 11, 0, 9});
    }

    @Test(order = 4)
    void keepingTheShareThroughSeveralDips() {
        expect(10, new int[] {1, 5, 4, 8, 7, 11});
    }

    @Test(order = 5)
    void pricesOfZeroAndASaleOnTheLastDay() {
        expect(1000, new int[] {0, 1000});
        expect(7, new int[] {4, 0, 0, 7});
    }

    @Test(order = 6)
    void randomShortRuns() {
        Random random = new Random(309);
        for (int round = 0; round < 300; round++) {
            int[] prices = new int[1 + random.nextInt(9)];
            for (int i = 0; i < prices.length; i++) {
                prices[i] = random.nextInt(21);
            }
            expect(everyPlan(prices), prices);
        }
    }

    /** Tries every choice of buy, sell or nothing for each day and keeps the best plan the rules allow. */
    private static int everyPlan(int[] prices) {
        int plans = 1;
        for (int i = 0; i < prices.length; i++) {
            plans *= 3;
        }
        int best = 0;
        for (int plan = 0; plan < plans; plan++) {
            int code = plan;
            boolean holding = false;
            int restDay = -1;
            int profit = 0;
            boolean allowed = true;
            for (int day = 0; day < prices.length && allowed; day++, code /= 3) {
                int action = code % 3; // 0 nothing, 1 buy, 2 sell
                if (action == 1) {
                    allowed = !holding && day != restDay;
                    holding = true;
                    profit -= prices[day];
                } else if (action == 2) {
                    allowed = holding;
                    holding = false;
                    profit += prices[day];
                    restDay = day + 1;
                }
            }
            if (allowed && !holding) {
                best = Math.max(best, profit);
            }
        }
        return best;
    }

    @Test(order = 7, timeoutMs = 200)
    void aRepeatingRiseWithARestDayBuiltIn() {
        // 0, 1000, 500 repeated: buy at 0, sell at 1000, rest on the 500. A trade and its rest day take
        // three days, so at most 1,667 trades fit in 5,000 days, and each earns at most 1,000.
        int[] prices = new int[5000];
        int[] pattern = {0, 1000, 500};
        for (int i = 0; i < prices.length; i++) {
            prices[i] = pattern[i % 3];
        }
        expect(1_667_000, prices);
    }

    @Test(order = 8, timeoutMs = 200)
    void aZigzagThatTheRestDayHalves() {
        // 1000, 0 repeated: a trade earns 1,000 only from a 0 to the next 1000, and after its rest day
        // the next 0 is three days on, so a 0 can be bought once every four days: 1,250 trades
        int[] prices = new int[5000];
        for (int i = 0; i < prices.length; i++) {
            prices[i] = i % 2 == 0 ? 1000 : 0;
        }
        expect(1_250_000, prices);
    }
}
