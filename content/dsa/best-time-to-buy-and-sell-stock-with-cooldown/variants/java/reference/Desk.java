/** Plans the trades of a desk that must sit out the day after every sale. */
public class Desk {

    /** Returns the largest total profit from trades of one share at a time, resting the day after each sale. */
    static int bestProfit(int[] prices) {
        // far below any real profit, and adding a price to it cannot overflow
        int impossible = Integer.MIN_VALUE / 2;
        int hold = impossible; // best profit so far, ending the day with a share
        int sold = impossible; // best profit so far, having sold today, so tomorrow is a rest day
        int idle = 0; // best profit so far, holding nothing and free to buy tomorrow
        for (int price : prices) {
            int newHold = Math.max(hold, idle - price); // keep the share, or buy: never on the day after a sale
            int newSold = hold + price; // sell the share bought on an earlier day
            int newIdle = Math.max(idle, sold); // wait, or sit out the rest day after yesterday's sale
            hold = newHold;
            sold = newSold;
            idle = newIdle;
        }
        return Math.max(sold, idle);
    }
}
