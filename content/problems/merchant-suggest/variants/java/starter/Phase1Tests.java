import static kit.Check.assertEquals;
import static kit.Check.assertTrue;

import java.nio.file.Path;
import java.util.Optional;
import kit.Test;

public class Phase1Tests {

    @Test(order = 1)
    void normalizerLowersCaseAndDropsPunctuation() {
        assertEquals("domino s pizza", Normalizer.normalize("Domino's  Pizza!"));
        assertEquals("big bazaar", Normalizer.normalize("  BIG-BAZAAR "));
        assertEquals("", Normalizer.normalize(" ?! "));
    }

    @Test(order = 2)
    void directoryStaysSortedByNormalizedName() {
        MerchantDirectory directory = new MerchantDirectory();
        directory.add(new Merchant("Zomato", "food", 5));
        directory.add(new Merchant("amazon pay", "shopping", 9));
        directory.add(new Merchant("Flipkart", "shopping", 7));
        assertEquals("amazon pay", directory.all().get(0).name());
        assertEquals("Flipkart", directory.all().get(1).name());
        assertEquals("Zomato", directory.all().get(2).name());
    }

    @Test(order = 3)
    void addingTheSameNameReplacesTheMerchant() {
        MerchantDirectory directory = new MerchantDirectory();
        directory.add(new Merchant("Uber", "travel", 1));
        directory.add(new Merchant("Flipkart", "shopping", 7));
        directory.add(new Merchant("UBER", "travel", 2));
        assertEquals(2, directory.size());
    }

    @Test(order = 4)
    void findsAMerchantInTheMiddle() {
        MerchantDirectory directory = new MerchantDirectory();
        directory.add(new Merchant("Amazon Pay", "shopping", 9));
        directory.add(new Merchant("Flipkart", "shopping", 7));
        directory.add(new Merchant("Zomato", "food", 5));
        assertEquals(Optional.of(new Merchant("Flipkart", "shopping", 7)), directory.find("flipkart"));
        assertEquals(Optional.empty(), directory.find("myntra"));
    }

    @Test(order = 5)
    void findsTheOnlyMerchant() {
        MerchantDirectory directory = new MerchantDirectory();
        directory.add(new Merchant("Swiggy", "food", 3));
        assertTrue(directory.find("Swiggy").isPresent(), "Swiggy is the only merchant and should be found");
    }

    @Test(order = 6)
    void findsEveryMerchantInTheSampleFile() throws Exception {
        MerchantDirectory directory = DirectoryLoader.fromFile(Path.of("data/merchants.txt"));
        assertEquals(19, directory.size());
        StringBuilder missing = new StringBuilder();
        for (Merchant merchant : directory.all()) {
            if (directory.find(merchant.name()).isEmpty()) {
                missing.append(merchant.name()).append("; ");
            }
        }
        assertEquals("", missing.toString(), "merchants that are in the directory but were not found");
    }

    @Test(order = 7)
    void findIgnoresCaseAndPunctuation() throws Exception {
        MerchantDirectory directory = DirectoryLoader.fromFile(Path.of("data/merchants.txt"));
        assertEquals("Domino's Pizza", directory.find("DOMINO'S PIZZA").map(Merchant::name).orElse("not found"));
        assertEquals("Zomato", directory.find(" zomato ").map(Merchant::name).orElse("not found"));
    }
}
