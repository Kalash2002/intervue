import static kit.Check.assertEquals;
import static kit.Check.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import kit.Test;

public class Phase1Tests {

    private static void roundTrip(List<String> strings) {
        Codec codec = new Codec();
        List<String> back = codec.decode(codec.encode(strings));
        assertEquals(strings, back, "the list must come back unchanged");
    }

    @Test(order = 1)
    void sample() {
        roundTrip(List.of("red", "green"));
    }

    @Test(order = 2)
    void emptyList() {
        roundTrip(List.of());
    }

    @Test(order = 3)
    void oneEmptyString() {
        roundTrip(List.of(""));
    }

    @Test(order = 4)
    void severalEmptyStrings() {
        roundTrip(List.of("", "", ""));
        roundTrip(List.of("", "x", ""));
    }

    @Test(order = 5)
    void textThatLooksLikeASeparator() {
        roundTrip(List.of("#", "3#abc", ",", "5#", "##", "0#", " "));
        roundTrip(List.of("a,b", "c,d", ",,"));
        roundTrip(List.of("3#ab", "#"));
    }

    @Test(order = 6)
    void digitsAndMarkersTogether() {
        roundTrip(List.of("12", "123456789", "7#7#7#", "2#", "10#abcdefghij"));
    }

    @Test(order = 7)
    void unicodeAndControlCharacters() {
        roundTrip(List.of("héllo", "日本語", "smile 😀", "tab\there", "line1\nline2", "nul\u0000byte"));
    }

    @Test(order = 8)
    void decodeOnAnotherInstance() {
        List<String> strings = List.of("first", "", "se#cond", "4#");
        String text = new Codec().encode(strings);
        assertEquals(strings, new Codec().decode(text), "decode must not rely on state held by the encoding object");
    }

    @Test(order = 9)
    void differentListsGiveDifferentText() {
        Codec codec = new Codec();
        assertTrue(!codec.encode(List.of()).equals(codec.encode(List.of(""))), "[] and [\"\"] must encode differently");
        assertTrue(!codec.encode(List.of("a", "b")).equals(codec.encode(List.of("ab"))), "[a, b] and [ab] must encode differently");
        assertTrue(!codec.encode(List.of("", "")).equals(codec.encode(List.of(""))), "two empty strings and one must encode differently");
    }

    @Test(order = 10, timeoutMs = 5000)
    void largeRoundTrip() {
        Random random = new Random(11);
        String alphabet = "ab1#,é \n0";
        List<String> strings = new ArrayList<>();
        for (int i = 0; i < 99_999; i++) {
            StringBuilder s = new StringBuilder();
            int length = random.nextInt(21);
            for (int j = 0; j < length; j++) {
                s.append(alphabet.charAt(random.nextInt(alphabet.length())));
            }
            strings.add(s.toString());
        }
        strings.add("x".repeat(500_000));
        roundTrip(strings);
    }
}
