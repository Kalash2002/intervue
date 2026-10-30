/** Fills a floor plan with each room's distance to the nearest exit. */
public class FloorPlan {

    private static final int OPEN = Integer.MAX_VALUE;

    /** Writes into every open room (Integer.MAX_VALUE) its fewest steps to an exit (0); walls are -1. */
    static void fillDistances(int[][] rooms) {
        int height = rooms.length;
        int width = rooms[0].length;
        int[] seen = new int[height * width]; // holds the number of the search that last saw each cell
        int[] queue = new int[height * width];
        int search = 0;
        for (int start = 0; start < height * width; start++) {
            if (rooms[start / width][start % width] != OPEN) {
                continue;
            }
            search++;
            int head = 0;
            int tail = 0;
            queue[tail++] = start;
            seen[start] = search;
            int steps = 0;
            boolean found = false;
            while (head < tail && !found) { // one ring of cells per step, until a ring holds an exit
                int end = tail;
                for (; head < end && !found; head++) {
                    int r = queue[head] / width;
                    int c = queue[head] % width;
                    if (rooms[r][c] == 0) {
                        found = true;
                        break;
                    }
                    int[] near = {r > 0 ? queue[head] - width : -1, r + 1 < height ? queue[head] + width : -1,
                            c > 0 ? queue[head] - 1 : -1, c + 1 < width ? queue[head] + 1 : -1};
                    for (int cell : near) {
                        if (cell >= 0 && seen[cell] != search && rooms[cell / width][cell % width] != -1) {
                            seen[cell] = search;
                            queue[tail++] = cell;
                        }
                    }
                }
                if (!found) {
                    steps++;
                }
            }
            if (found) {
                rooms[start / width][start % width] = steps;
            }
        }
    }
}
