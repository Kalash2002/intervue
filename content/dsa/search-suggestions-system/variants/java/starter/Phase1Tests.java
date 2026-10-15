import static kit.Check.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import kit.Test;

public class Phase1Tests {

    @Test(order = 1)
    void sample() {
        String[] products = {"kiln", "kind", "kit", "kite", "lamp"};
        assertEquals(List.of(List.of("kiln", "kind", "kit"), List.of("kiln", "kind", "kit"), List.of("kit", "kite")),
                Autocomplete.suggest(products, "kit", 3));
        assertEquals(List.of(List.of("pen", "pencil"), List.of()),
                Autocomplete.suggest(new String[] {"pen", "pencil"}, "pz", 3));
        assertEquals(List.of(List.of("ab"), List.of("ab"), List.of()),
                Autocomplete.suggest(new String[] {"ab", "abc", "b"}, "abd", 1));
    }

    @Test(order = 2)
    void nothingMatchesTheFirstLetter() {
        assertEquals(List.of(List.of()), Autocomplete.suggest(new String[] {"bat", "cat"}, "a", 2));
    }

    @Test(order = 3)
    void typedTextLongerThanEveryProduct() {
        assertEquals(List.of(List.of("ab"), List.of("ab"), List.of()),
                Autocomplete.suggest(new String[] {"ab"}, "abc", 2));
    }

    @Test(order = 4)
    void fewerMatchesThanTheLimitAndAProductEqualToThePrefix() {
        String[] products = {"abd", "abc", "ab", "b"};
        assertEquals(List.of(List.of("ab", "abc", "abd"), List.of("ab", "abc", "abd"), List.of("abd")),
                Autocomplete.suggest(products, "abd", 10));
    }

    @Test(order = 5)
    void unsortedProductsAndALimitOfTwo() {
        String[] products = {"zeta", "alps", "beta", "alpha", "alp"};
        assertEquals(List.of(List.of("alp", "alpha"), List.of("alp", "alpha"), List.of("alp", "alpha"), List.of("alps")),
                Autocomplete.suggest(products, "alps", 2));
    }

    @Test(order = 6)
    void singleProductEqualToTheTypedText() {
        assertEquals(List.of(List.of("x")), Autocomplete.suggest(new String[] {"x"}, "x", 10));
    }

    @Test(order = 7)
    void productsThatArePrefixesOfEachOther() {
        String[] products = {"aaaaa", "aaa", "a", "aaaa", "aa"};
        assertEquals(List.of(List.of("a", "aa"), List.of("aa", "aaa"), List.of("aaa", "aaaa"), List.of("aaaa", "aaaaa")),
                Autocomplete.suggest(products, "aaaa", 2));
    }

    @Test(order = 8)
    void dictionaryOrderIsDecidedLetterByLetter() {
        String[] products = {"mobile", "moat", "mouse", "mop", "monitor"};
        assertEquals(List.of(List.of("moat", "mobile", "monitor"), List.of("moat", "mobile", "monitor"), List.of("mop")),
                Autocomplete.suggest(products, "mop", 3));
    }

    @Test(order = 9, timeoutMs = 3000)
    void fiftyThousandProductsSharingSixHundredLetters() {
        Random random = new Random(11);
        StringBuilder letters = new StringBuilder();
        for (int i = 0; i < 600; i++) {
            letters.append((char) ('a' + random.nextInt(26)));
        }
        String base = letters.toString();
        int n = 50_000;
        List<String> list = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            list.add(base + Integer.toString(100_000 + i).substring(1)); // distinct, and smaller numbers sort first
        }
        Collections.shuffle(list, random);
        String typed = base + "00123";

        List<String> firstThree = List.of(base + "00000", base + "00001", base + "00002");
        List<List<String>> expected = new ArrayList<>();
        for (int length = 1; length <= 602; length++) { // the base, then "0" and "00": ids 0 to 9999 and 0 to 999
            expected.add(firstThree);
        }
        expected.add(List.of(base + "00100", base + "00101", base + "00102")); // "001"
        expected.add(List.of(base + "00120", base + "00121", base + "00122")); // "0012"
        expected.add(List.of(base + "00123")); // "00123"
        assertEquals(expected, Autocomplete.suggest(list.toArray(new String[0]), typed, 3));
    }
}
