/** Multiplies non-negative integers written as decimal strings. */
public class Multiplier {

    /** Returns num1 times num2 as a decimal string with no leading zeros. */
    static String multiply(String num1, String num2) {
        String total = "0";
        for (String done = "0"; !done.equals(num2); done = add(done, "1")) { // num2 rounds of adding num1
            total = add(total, num1);
        }
        return total;
    }

    /** Adds two decimal strings digit by digit from the right, as on paper. */
    private static String add(String a, String b) {
        StringBuilder sum = new StringBuilder();
        int carry = 0;
        for (int i = a.length() - 1, j = b.length() - 1; i >= 0 || j >= 0 || carry > 0; i--, j--) {
            int digit = carry + (i >= 0 ? a.charAt(i) - '0' : 0) + (j >= 0 ? b.charAt(j) - '0' : 0);
            sum.append((char) ('0' + digit % 10));
            carry = digit / 10;
        }
        return sum.reverse().toString();
    }
}
