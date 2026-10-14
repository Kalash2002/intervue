import static kit.Check.assertEquals;
import static kit.Check.assertThrows;

import java.nio.file.Path;
import java.util.List;
import kit.Test;

public class Phase2Tests {

    private static List<String> names(List<Merchant> merchants) {
        return merchants.stream().map(Merchant::name).toList();
    }

    private static Suggester sample() throws Exception {
        return new Suggester(DirectoryLoader.fromFile(Path.of("data/merchants.txt")));
    }

    @Test(order = 1)
    void matchesTheStartOfTheName() throws Exception {
        assertEquals(List.of("Zomato", "Zepto"), names(sample().suggest("z", 5)));
        assertEquals(List.of("Flipkart"), names(sample().suggest("flip", 5)));
    }

    @Test(order = 2)
    void matchesTheStartOfAnyWord() throws Exception {
        assertEquals(List.of("Domino's Pizza", "Pizza Hut"), names(sample().suggest("pi", 5)));
        assertEquals(List.of("Amazon Pay", "Domino's Pizza", "Paytm Mall", "Pizza Hut", "Chai Point"), names(sample().suggest("p", 5)));
    }

    @Test(order = 3)
    void doesNotMatchTheMiddleOfAWord() throws Exception {
        assertEquals(List.of(), names(sample().suggest("offee", 5)));
        assertEquals(List.of(), names(sample().suggest("mato", 5)));
    }

    @Test(order = 4)
    void busiestMerchantsFirstAndTiesByName() throws Exception {
        assertEquals(List.of("Cafe Coffee Day", "Starbucks Coffee", "Third Wave Coffee"), names(sample().suggest("co", 3)));
        assertEquals(List.of("Cafe Coffee Day", "Starbucks Coffee", "Third Wave Coffee", "Blue Tokai Coffee", "Coffee Culture"),
                names(sample().suggest("coffee", 10)));
    }

    @Test(order = 5)
    void typedTextMaySpanSeveralWords() throws Exception {
        assertEquals(List.of("Cafe Coffee Day"), names(sample().suggest("coffee d", 5)));
        assertEquals(List.of("Cafe Coffee Day"), names(sample().suggest("Cafe  COFFEE", 5)));
        assertEquals(List.of(), names(sample().suggest("coffee cafe", 5)));
    }

    @Test(order = 6)
    void typedTextIsNormalizedLikeNames() throws Exception {
        assertEquals(List.of("BigBasket", "Big Bazaar"), names(sample().suggest("BIG", 5)));
        assertEquals(List.of("Domino's Pizza"), names(sample().suggest("domino's", 5)));
        assertEquals(List.of("Swiggy", "Domino's Pizza", "Starbucks Coffee"), names(sample().suggest("s", 5)));
    }

    @Test(order = 7)
    void nothingTypedSuggestsNothing() throws Exception {
        assertEquals(List.of(), names(sample().suggest("", 5)));
        assertEquals(List.of(), names(sample().suggest(" ?! ", 5)));
    }

    @Test(order = 8)
    void aMerchantIsSuggestedOnce() {
        MerchantDirectory directory = new MerchantDirectory();
        directory.add(new Merchant("Coffee Coffee House", "cafe", 10));
        directory.add(new Merchant("Coffee Corner", "cafe", 20));
        directory.add(new Merchant("Tea Corner", "cafe", 30));
        assertEquals(List.of("Tea Corner", "Coffee Corner", "Coffee Coffee House"), names(new Suggester(directory).suggest("co", 5)));
        assertEquals(List.of("Coffee Corner", "Coffee Coffee House"), names(new Suggester(directory).suggest("coffee", 5)));
    }

    @Test(order = 9)
    void limitMustBeBetweenOneAndTen() throws Exception {
        Suggester suggester = sample();
        assertEquals(1, suggester.suggest("p", 1).size());
        assertEquals(5, suggester.suggest("p", 10).size());
        assertThrows(IllegalArgumentException.class, () -> suggester.suggest("p", 0));
        assertThrows(IllegalArgumentException.class, () -> suggester.suggest("p", 11));
    }

    @Test(order = 10)
    void suggestionsFollowChangesToTheDirectory() throws Exception {
        MerchantDirectory directory = DirectoryLoader.fromFile(Path.of("data/merchants.txt"));
        Suggester suggester = new Suggester(directory);
        assertEquals(List.of("Zomato", "Zepto"), names(suggester.suggest("z", 5)));
        directory.add(new Merchant("Zudio", "fashion", 999_999));
        assertEquals(List.of("Zudio", "Zomato", "Zepto"), names(suggester.suggest("z", 5)));
        directory.add(new Merchant("ZEPTO", "grocery", 1_000_000));
        assertEquals(List.of("ZEPTO", "Zudio", "Zomato"), names(suggester.suggest("z", 5)));
    }
}
