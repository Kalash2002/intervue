import java.util.*;

/** Adds one to a number stored as its decimal digits. */
public class Odometer {

    /** Returns the digits of the number plus one, most significant first; digits may be changed. */
    static int[] plusOne(int[] digits) {
        for (int i = digits.length - 1; i >= 0; i--) {
            if (digits[i] < 9) {
                digits[i]++;
                return digits; // the carry is used up, so nothing to the left changes
            }
            digits[i] = 0; // a 9 turns into 0 and carries one to the left
        }
        // only all nines get here: 99...9 + 1 is 1 followed by as many zeros
        int[] longer = new int[digits.length + 1];
        longer[0] = 1;
        return longer;
    }
}
