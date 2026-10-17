/** Times how long mould takes to spread through a crate of oranges. */
public class Spoilage {

    /** Returns the minutes until no fresh orange (1) is left, or -1 if one is never reached; 2 is mouldy, 0 empty. */
    static int minutesUntilSpoiled(int[][] grid) {
        int height = grid.length;
        int width = grid[0].length;
        int[] state = new int[height * width]; // a copy, so the caller's grid is left alone
        int[] queue = new int[height * width];
        int tail = 0;
        int fresh = 0;
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                state[r * width + c] = grid[r][c];
                if (grid[r][c] == 2) {
                    queue[tail++] = r * width + c; // every mouldy orange starts spreading at minute 0
                } else if (grid[r][c] == 1) {
                    fresh++;
                }
            }
        }
        int minutes = 0;
        int head = 0;
        while (head < tail && fresh > 0) {
            int end = tail; // the oranges that turned in the previous minute
            minutes++;
            for (; head < end; head++) {
                int cell = queue[head];
                int r = cell / width;
                int c = cell % width;
                if (r > 0) tail = spread(state, queue, tail, cell - width);
                if (r + 1 < height) tail = spread(state, queue, tail, cell + width);
                if (c > 0) tail = spread(state, queue, tail, cell - 1);
                if (c + 1 < width) tail = spread(state, queue, tail, cell + 1);
            }
            fresh -= tail - end;
        }
        return fresh == 0 ? minutes : -1;
    }

    /** Turns a fresh orange mouldy and queues it; returns the new queue length. */
    private static int spread(int[] state, int[] queue, int tail, int cell) {
        if (state[cell] == 1) {
            state[cell] = 2; // marked when queued, so it is counted once
            queue[tail++] = cell;
        }
        return tail;
    }
}
