/** Plans print jobs that need a cool-down between jobs of the same kind. */
public class Printer {

    /** Returns the fewest slots needed to run all jobs when equal kinds must be more than gap slots apart. */
    static int minSlots(char[] jobs, int gap) {
        int[] left = new int[26];
        for (char job : jobs) {
            left[job - 'A']++;
        }
        int[] lastSlot = new int[26];
        java.util.Arrays.fill(lastSlot, Integer.MIN_VALUE / 2);
        int remaining = jobs.length;
        int slot = 0;
        while (remaining > 0) {
            slot++;
            int pick = -1;
            for (int kind = 0; kind < 26; kind++) { // the busiest kind that is allowed to run now
                if (left[kind] > 0 && slot - lastSlot[kind] > gap && (pick < 0 || left[kind] > left[pick])) {
                    pick = kind;
                }
            }
            if (pick >= 0) {
                left[pick]--;
                lastSlot[pick] = slot;
                remaining--;
            }
        }
        return slot;
    }
}
