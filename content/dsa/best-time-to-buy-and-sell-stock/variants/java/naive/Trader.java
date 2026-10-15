/** Finds the best profit from one buy and one later sell. */
public class Trader {

    /** Returns the largest profit from buying on one day and selling on a later one, or 0. */
    static int bestProfit(int[] prices) {
        int best = 0;
        for (int buy = 0; buy < prices.length; buy++) {
            for (int sell = buy + 1; sell < prices.length; sell++) {
                if (prices[sell] - prices[buy] > best) {
                    best = prices[sell] - prices[buy];
                }
            }
        }
        return best;
    }
}
