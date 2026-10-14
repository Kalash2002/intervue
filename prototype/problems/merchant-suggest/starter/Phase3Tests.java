import static kit.Check.assertEquals;
import static kit.Check.assertTrue;

import java.util.List;
import java.util.Random;
import kit.Test;

/**
 * Large inputs. Each test has a time limit; a correct but slow implementation fails with TIMEOUT.
 * The three tests stress different parts of the code.
 */
public class Phase3Tests {

    /** The nightly feed: 500,000 merchants added one at a time, then a few lookups. */
    @Test(order = 1, timeoutMs = 8000)
    void loadingTheNightlyFeed() {
        List<Merchant> merchants = Datasets.merchants(500_000, 11L);
        MerchantDirectory directory = Datasets.directory(merchants);
        assertEquals(500_000, directory.size());
        for (int i = 0; i < merchants.size(); i += 1_000) {
            assertTrue(directory.find(merchants.get(i).name()).isPresent(), "not found: " + merchants.get(i).name());
        }
        assertEquals("Anbubudev Chirara Traders 258093", directory.all().get(0).name(), "first merchant in sorted order");
    }

    /** 20,000 customers who each type four to seven letters of a word. */
    @Test(order = 2, timeoutMs = 6000)
    void specificSearches() {
        List<Merchant> merchants = Datasets.merchants(100_000, 2026L);
        Suggester suggester = new Suggester(Datasets.directory(merchants));
        Random random = new Random(5);
        long checksum = 0;
        for (int i = 0; i < 20_000; i++) {
            String word = Datasets.randomWord(merchants, random);
            String typed = word.substring(0, Math.min(word.length(), 4 + random.nextInt(4)));
            checksum = checksum * 31 + fingerprint(suggester.suggest(typed, 5));
        }
        assertEquals(EXPECTED_SPECIFIC, checksum, "checksum over 20,000 searches");
    }

    /** 30,000 customers typing a word letter by letter; the box asks for suggestions after every letter. */
    @Test(order = 3, timeoutMs = 6000)
    void typingLetterByLetter() {
        List<Merchant> merchants = Datasets.merchants(100_000, 2026L);
        Suggester suggester = new Suggester(Datasets.directory(merchants));
        Random random = new Random(9);
        long checksum = 0;
        for (int i = 0; i < 30_000; i++) {
            String word = Datasets.randomWord(merchants, random);
            for (int length = 1; length <= word.length(); length++) {
                checksum = checksum * 31 + fingerprint(suggester.suggest(word.substring(0, length), 10));
            }
        }
        assertEquals(EXPECTED_TYPING, checksum, "checksum over every keystroke");
    }

    private static long fingerprint(List<Merchant> suggestions) {
        long value = suggestions.size();
        for (Merchant merchant : suggestions) {
            value = value * 131 + merchant.txnCount() * 7 + merchant.name().hashCode();
        }
        return value;
    }

    static final long EXPECTED_SPECIFIC = -5854093640147881315L;
    static final long EXPECTED_TYPING = -2407696411267135862L;
}
