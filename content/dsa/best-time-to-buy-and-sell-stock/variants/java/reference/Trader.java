/** Finds the best profit from one buy and one later sell. */
public class Trader {

    /** Returns the largest profit from buying on one day and selling on a later one, or 0. */
    static int bestProfit(int[] prices) {
        int best = 0;
        int lowest = Integer.MAX_VALUE; // cheapest price seen on an earlier day
        for (int price : prices) {
            best = Math.max(best, price - lowest); // sell on this day against the cheapest earlier day
            lowest = Math.min(lowest, price);
        }
        return best;
    }
}
