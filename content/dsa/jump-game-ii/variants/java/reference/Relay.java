/** Counts the fewest hops a message needs along a row of relay stations. */
public class Relay {

    /** Returns the fewest hops from station 0 to the last station. */
    static int fewestHops(int[] range) {
        int hops = 0;
        int stretchEnd = 0; // last station the message can reach with `hops` hops
        int farthest = 0; // farthest station it can reach with one hop more
        for (int i = 0; i < range.length - 1; i++) { // the last station sends nothing on
            farthest = Math.max(farthest, i + range[i]);
            if (i == stretchEnd) { // every station of this stretch has been seen: one hop more
                hops++;
                stretchEnd = farthest;
            }
        }
        return hops;
    }
}
