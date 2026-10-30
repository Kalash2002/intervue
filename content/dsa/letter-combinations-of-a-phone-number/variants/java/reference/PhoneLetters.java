import java.util.ArrayList;
import java.util.List;

/** Lists the letter strings a sequence of phone-keypad digits could stand for. */
public class PhoneLetters {

    // KEYS[d] holds the letters of digit d; 0 and 1 have none
    private static final String[] KEYS = {"", "", "abc", "def", "ghi", "jkl", "mno", "pqrs", "tuv", "wxyz"};

    /** Returns every string made of one letter per digit, each once; none for no digits. */
    static List<String> combinations(String digits) {
        List<String> result = new ArrayList<>();
        if (digits.isEmpty()) {
            return result; // no digits stand for nothing, not for the empty string
        }
        pick(digits, 0, new StringBuilder(), result);
        return result;
    }

    // Picks a letter for digits[i] onward; picked holds the letters for the digits before i.
    private static void pick(String digits, int i, StringBuilder picked, List<String> result) {
        if (i == digits.length()) {
            result.add(picked.toString());
            return;
        }
        for (char letter : KEYS[digits.charAt(i) - '0'].toCharArray()) {
            picked.append(letter);
            pick(digits, i + 1, picked, result);
            picked.setLength(picked.length() - 1); // take it back before trying the next letter
        }
    }
}
