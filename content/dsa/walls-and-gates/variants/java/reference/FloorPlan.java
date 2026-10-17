/** Fills a floor plan with each room's distance to the nearest exit. */
public class FloorPlan {

    private static final int OPEN = Integer.MAX_VALUE;

    /** Writes into every open room (Integer.MAX_VALUE) its fewest steps to an exit (0); walls are -1. */
    static void fillDistances(int[][] rooms) {
        int height = rooms.length;
        int width = rooms[0].length;
        int[] queue = new int[height * width];
        int tail = 0;
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                if (rooms[r][c] == 0) {
                    queue[tail++] = r * width + c; // every exit starts the search at distance 0
                }
            }
        }
        for (int head = 0; head < tail; head++) {
            int r = queue[head] / width;
            int c = queue[head] % width;
            int next = rooms[r][c] + 1; // never OPEN + 1: only rooms that already have a distance are queued
            if (r > 0) tail = reach(rooms, queue, tail, r - 1, c, next);
            if (r + 1 < height) tail = reach(rooms, queue, tail, r + 1, c, next);
            if (c > 0) tail = reach(rooms, queue, tail, r, c - 1, next);
            if (c + 1 < width) tail = reach(rooms, queue, tail, r, c + 1, next);
        }
    }

    /** Gives an open room its distance and queues it; returns the new queue length. */
    private static int reach(int[][] rooms, int[] queue, int tail, int r, int c, int distance) {
        if (rooms[r][c] == OPEN) {
            rooms[r][c] = distance; // the first exit to arrive is the nearest one
            queue[tail++] = r * rooms[0].length + c;
        }
        return tail;
    }
}
