import java.util.*;

/** Adds two ints for a controller whose instruction set has no add or subtract. */
public class LogicAdder {

    /** Returns a + b as Java's int addition gives it, wrap-around included, without + or -. */
    static int add(int a, int b) {
        int generate = a & b; // columns that make a carry on their own
        int propagate = a ^ b; // columns that pass on a carry coming in from below
        // double the blocks from 1 column to 2, 4, 8, 16 and 32: each generate line must use
        // the propagate of the blocks before this round's doubling, so it comes first
        generate |= propagate & (generate << 1);
        propagate &= propagate << 1;
        generate |= propagate & (generate << 2);
        propagate &= propagate << 2;
        generate |= propagate & (generate << 4);
        propagate &= propagate << 4;
        generate |= propagate & (generate << 8);
        propagate &= propagate << 8;
        generate |= propagate & (generate << 16); // now: a carry leaves this column
        return (a ^ b) ^ (generate << 1); // the carry out of each column goes into the next
    }
}
