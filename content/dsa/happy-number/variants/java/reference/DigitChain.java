import java.util.*;

/** Follows the chain of digit-square sums that starts at a number. */
public class DigitChain {

    /** Returns true if the chain that starts at n reaches 1. */
    static boolean isHappy(int n) {
        int slow = n;
        int fast = next(n);
        // fast moves two steps a round, so it reaches 1 first, or meets slow on the loop
        while (fast != 1 && slow != fast) {
            slow = next(slow);
            fast = next(next(fast));
        }
        return fast == 1;
    }

    private static int next(int n) {
        int sum = 0;
        for (; n > 0; n /= 10) {
            int digit = n % 10;
            sum += digit * digit;
        }
        return sum;
    }
}
