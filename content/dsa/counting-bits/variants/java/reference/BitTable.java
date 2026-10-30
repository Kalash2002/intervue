import java.util.*;

/** Builds the table of how many 1 bits each number from 0 to n has. */
public class BitTable {

    /** Returns an array of n + 1 entries whose entry i is the number of 1 bits of i. */
    static int[] build(int n) {
        int[] ones = new int[n + 1]; // entries 0 to n, both included; ones[0] stays 0
        for (int i = 1; i <= n; i++) {
            ones[i] = ones[i >> 1] + (i & 1); // i is i >> 1 with its lowest bit put back
        }
        return ones;
    }
}
