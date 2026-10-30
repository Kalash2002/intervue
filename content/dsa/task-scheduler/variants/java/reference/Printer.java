/** Plans print jobs that need a cool-down between jobs of the same kind. */
public class Printer {

    /** Returns the fewest slots needed to run all jobs when equal kinds must be more than gap slots apart. */
    static int minSlots(char[] jobs, int gap) {
        int[] count = new int[26];
        int most = 0;
        for (char job : jobs) {
            most = Math.max(most, ++count[job - 'A']);
        }
        int tied = 0; // how many kinds have the highest count
        for (int c : count) {
            if (c == most) {
                tied++;
            }
        }
        if (most == 0) {
            return 0;
        }
        // The busiest kind forms (most - 1) full frames of gap + 1 slots, plus a last partial frame.
        long frame = (long) (most - 1) * (gap + 1) + tied;
        return (int) Math.max(frame, jobs.length); // with enough other jobs there is no idle slot
    }
}
