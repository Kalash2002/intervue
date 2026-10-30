/** Finds where a shuttle on a circular route can start and still get all the way round. */
public class Shuttle {

    /** Returns the lowest-numbered stop from which the loop can be completed, or -1. */
    static int startStop(int[] charge, int[] drain) {
        int total = 0; // charge minus drain over the whole loop
        int battery = 0; // battery since the current candidate start
        int start = 0;
        for (int i = 0; i < charge.length; i++) {
            int gain = charge[i] - drain[i];
            total += gain;
            battery += gain;
            if (battery < 0) { // ran out on the leg from stop i: no stop from start to i works
                start = i + 1;
                battery = 0;
            }
        }
        return total < 0 ? -1 : start; // with enough charge overall, the last candidate works
    }
}
