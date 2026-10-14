import java.util.Random;

/** Builds the large order logs used by the phase 3 tests. Same seed, same log. */
public final class Datasets {
    private Datasets() {}

    private static final long[] PRICES = {19_900, 49_900, 99_900, 149_900, 249_900};

    /** A sale-day log: many customers, a small catalogue, a few prices, one order every few seconds. */
    public static OrderLog saleDay(int orders, long seed) {
        Random random = new Random(seed);
        OrderLog log = new OrderLog();
        for (int i = 0; i < orders; i++) {
            String customer = "C" + random.nextInt(20_000);
            String sku = "SKU-" + random.nextInt(60);
            long amount = PRICES[random.nextInt(PRICES.length)];
            log.add(new Order("O" + i, customer, sku, amount, i / 300));
        }
        return log;
    }
}
