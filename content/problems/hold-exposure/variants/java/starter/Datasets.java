import java.util.Random;

/** Builds the large hold books used by the phase 3 tests. Same seed, same book. */
public final class Datasets {
    private Datasets() {}

    /** One account with many holds scattered over about two years of minutes. */
    public static HoldBook oneBusyAccount(String account, int holds, long seed) {
        Random random = new Random(seed);
        HoldBook book = new HoldBook();
        for (int i = 0; i < holds; i++) {
            int start = random.nextInt(1_000_000);
            int length = 1 + random.nextInt(5_000);
            long amount = 1 + random.nextInt(50_000_000);
            book.add(new Hold("B" + i, account, amount, start, start + length));
        }
        return book;
    }

    /** Many accounts with a handful of holds each, arriving interleaved as they would from a feed. */
    public static HoldBook manySmallAccounts(int accounts, int holdsPerAccount, long seed) {
        Random random = new Random(seed);
        HoldBook book = new HoldBook();
        for (int round = 0; round < holdsPerAccount; round++) {
            for (int a = 0; a < accounts; a++) {
                int start = random.nextInt(10_000);
                int length = 1 + random.nextInt(2_000);
                long amount = 1 + random.nextInt(1_000_000);
                book.add(new Hold("S" + a + "-" + round, "ACC" + a, amount, start, start + length));
            }
        }
        return book;
    }
}
