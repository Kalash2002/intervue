/** Plans the trades of a desk that must sit out the day after every sale. */
public class Desk {

    /** Returns the largest total profit from trades of one share at a time, resting the day after each sale. */
    static int bestProfit(int[] prices) {
        return from(prices, 0, false);
    }

    /** Best profit from day d on, free to act on day d, holding a share or not. */
    private static int from(int[] prices, int d, boolean holding) {
        if (d >= prices.length) {
            return 0; // a share still held is never sold, so buying it only cost money
        }
        int wait = from(prices, d + 1, holding);
        if (holding) {
            return Math.max(wait, prices[d] + from(prices, d + 2, false)); // sell today; tomorrow is a rest day
        }
        return Math.max(wait, from(prices, d + 1, true) - prices[d]); // buy today
    }
}
