/** Counts the groups of cars that reach a gate when overtaking is not possible. */
public class Convoy {

    /** Returns the number of groups of cars that arrive at the gate. */
    static int groups(int gate, int[] start, int[] speed) {
        int groups = 0;
        for (int i = 0; i < start.length; i++) {
            boolean joinsAGroupAhead = false;
            for (int j = 0; j < start.length && !joinsAGroupAhead; j++) {
                // car j is ahead of car i and arrives no earlier: car i catches it
                if (start[j] > start[i]
                        && (long) (gate - start[j]) * speed[i] >= (long) (gate - start[i]) * speed[j]) {
                    joinsAGroupAhead = true;
                }
            }
            if (!joinsAGroupAhead) {
                groups++;
            }
        }
        return groups;
    }
}
