import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Builds the large directory and the search text used by the phase 3 tests. Same seed, same data. */
public final class Datasets {
    private Datasets() {}

    private static final String[] STARTS = {"ka", "ro", "mi", "sha", "ve", "ni", "ta", "pra", "su", "de", "an", "gi", "lo", "ba", "chi", "ha", "ju", "ma", "pe", "za"};
    private static final String[] MIDDLES = {"na", "vi", "ri", "to", "sha", "lu", "me", "ko", "di", "ra", "si", "bu"};
    private static final String[] ENDS = {"n", "sh", "ra", "ya", "m", "ta", "l", "ka", "x", "dev"};
    private static final String[] KINDS = {"Stores", "Traders", "Foods", "Mart", "Cafe", "Pharmacy", "Electronics", "Travels", "Sweets", "Fashion"};

    /** A pronounceable word such as "Kanavin"; the vocabulary has a few thousand distinct words. */
    private static String word(Random random) {
        String w = STARTS[random.nextInt(STARTS.length)] + MIDDLES[random.nextInt(MIDDLES.length)];
        if (random.nextBoolean()) {
            w += MIDDLES[random.nextInt(MIDDLES.length)];
        }
        w += ENDS[random.nextInt(ENDS.length)];
        return Character.toUpperCase(w.charAt(0)) + w.substring(1);
    }

    /** Merchants named like "Kanavin Rotom Stores 4821". The trailing number makes every name unique. */
    public static List<Merchant> merchants(int count, long seed) {
        Random random = new Random(seed);
        List<Merchant> merchants = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            String kind = KINDS[random.nextInt(KINDS.length)];
            String name = word(random) + " " + word(random) + " " + kind + " " + i;
            merchants.add(new Merchant(name, kind.toLowerCase(), random.nextInt(1_000_000)));
        }
        return merchants;
    }

    /** A directory filled one merchant at a time, in the order a nightly feed delivers them. */
    public static MerchantDirectory directory(List<Merchant> merchants) {
        MerchantDirectory directory = new MerchantDirectory();
        for (Merchant merchant : merchants) {
            directory.add(merchant);
        }
        return directory;
    }

    /** One of the first two words of a random merchant, lower case. */
    public static String randomWord(List<Merchant> merchants, Random random) {
        String[] words = merchants.get(random.nextInt(merchants.size())).name().split(" ");
        return words[random.nextInt(2)].toLowerCase();
    }
}
