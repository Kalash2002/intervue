/** Counts the messages of letters that a string of digits could stand for. */
public class Telegram {

    /** Returns how many messages, with A as 1 up to Z as 26 and no separators, are written as digits. */
    static int messages(String digits) {
        int beforeLast = 0; // ways to read all but the last digit seen; unused until a second digit
        int last = 1; // ways to read the digits seen so far: one, the empty message, before the first
        for (int i = 0; i < digits.length(); i++) {
            int here = 0;
            if (digits.charAt(i) != '0') {
                here = last; // digit i is a letter on its own, 1 to 9
            }
            if (i > 0 && isLetterPair(digits.charAt(i - 1), digits.charAt(i))) {
                here += beforeLast; // digits i - 1 and i are one letter, 10 to 26
            }
            beforeLast = last;
            last = here;
        }
        return last;
    }

    /** Whether two digits form 10 to 26; a first digit of 0 never does. */
    private static boolean isLetterPair(char first, char second) {
        return first == '1' || (first == '2' && second <= '6');
    }
}
