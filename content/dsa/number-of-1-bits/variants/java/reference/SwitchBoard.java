import java.util.*;

/** Counts the switches that are on in a 32-bit pattern. */
public class SwitchBoard {

    /** Returns how many of the 32 bits of switches are 1; a negative value has bit 31 set. */
    static int countOn(int switches) {
        int on = 0;
        // != 0, not > 0: a pattern with bit 31 set is negative and still has bits to count
        while (switches != 0) {
            switches &= switches - 1; // clears the lowest 1 bit
            on++;
        }
        return on;
    }
}
