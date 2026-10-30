/** Counts the messages of letters that a string of digits could stand for. */
public class Telegram {

    /** Returns how many messages, with A as 1 up to Z as 26 and no separators, are written as digits. */
    static int messages(String digits) {
        return readings(digits, 0);
    }

    /** How many ways the digits from position i to the end can be read. */
    private static int readings(String digits, int i) {
        if (i == digits.length()) {
            return 1; // nothing left: the letters chosen so far are one message
        }
        if (digits.charAt(i) == '0') {
            return 0; // no letter is written starting with 0
        }
        int total = readings(digits, i + 1); // one digit, 1 to 9
        if (i + 1 < digits.length() && Integer.parseInt(digits.substring(i, i + 2)) <= 26) {
            total += readings(digits, i + 2); // two digits, 10 to 26: the first is not 0
        }
        return total;
    }
}
