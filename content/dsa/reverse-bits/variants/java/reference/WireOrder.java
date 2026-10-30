import java.util.*;

/** Mirrors the 32 bits of a word received in the opposite bit order. */
public class WireOrder {

    /** Returns word with bit i moved to bit 31 - i; a negative value has bit 31 set. */
    static int mirror(int word) {
        // swap ever smaller halves: the two 16-bit halves, then bytes, nibbles, pairs, single bits
        word = (word >>> 16) | (word << 16); // >>>: with >> a set bit 31 would fill the top half
        word = ((word >>> 8) & 0x00ff00ff) | ((word & 0x00ff00ff) << 8);
        word = ((word >>> 4) & 0x0f0f0f0f) | ((word & 0x0f0f0f0f) << 4);
        word = ((word >>> 2) & 0x33333333) | ((word & 0x33333333) << 2);
        word = ((word >>> 1) & 0x55555555) | ((word & 0x55555555) << 1);
        return word;
    }
}
