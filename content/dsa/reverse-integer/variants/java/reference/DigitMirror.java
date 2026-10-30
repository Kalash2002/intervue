import java.util.*;

/** Shows a number as a panel wired with its digit positions in the opposite order. */
public class DigitMirror {

    /** Returns x with its decimal digits reversed and its sign kept, or 0 if that is no int. */
    static int reverse(int x) {
        int reversed = 0;
        while (x != 0) {
            int digit = x % 10; // negative when x is: Java's % keeps the sign of x
            x /= 10;
            // reversed * 10 + digit must stay inside the int range: test that before computing it
            if (reversed > Integer.MAX_VALUE / 10 || (reversed == Integer.MAX_VALUE / 10 && digit > 7)) {
                return 0;
            }
            if (reversed < Integer.MIN_VALUE / 10 || (reversed == Integer.MIN_VALUE / 10 && digit < -8)) {
                return 0;
            }
            reversed = reversed * 10 + digit;
        }
        return reversed;
    }
}
