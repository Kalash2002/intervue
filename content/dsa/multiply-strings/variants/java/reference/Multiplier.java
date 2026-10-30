/** Multiplies non-negative integers written as decimal strings. */
public class Multiplier {

    /** Returns num1 times num2 as a decimal string with no leading zeros. */
    static String multiply(String num1, String num2) {
        int m = num1.length(), n = num2.length();
        int[] place = new int[m + n]; // place[k]: digit k of the product, most significant first
        for (int i = m - 1; i >= 0; i--) {
            for (int j = n - 1; j >= 0; j--) {
                // digit i times digit j lands at position i + j + 1, and its carry at i + j
                int sum = (num1.charAt(i) - '0') * (num2.charAt(j) - '0') + place[i + j + 1];
                place[i + j + 1] = sum % 10;
                place[i + j] += sum / 10; // may pass 9 for a moment; the next step that lands here settles it
            }
        }
        StringBuilder out = new StringBuilder();
        for (int digit : place) {
            if (out.length() > 0 || digit != 0) { // skip the leading zeros only
                out.append(digit);
            }
        }
        return out.length() == 0 ? "0" : out.toString();
    }
}
