/** Times how long mould takes to spread through a crate of oranges. */
public class Spoilage {

    /** Returns the minutes until no fresh orange (1) is left, or -1 if one is never reached; 2 is mouldy, 0 empty. */
    static int minutesUntilSpoiled(int[][] grid) {
        int height = grid.length;
        int width = grid[0].length;
        int[][] now = new int[height][];
        for (int r = 0; r < height; r++) {
            now[r] = grid[r].clone();
        }
        int minutes = 0;
        while (true) {
            int[][] next = new int[height][];
            for (int r = 0; r < height; r++) {
                next[r] = now[r].clone();
            }
            boolean changed = false;
            boolean anyFresh = false;
            for (int r = 0; r < height; r++) {
                for (int c = 0; c < width; c++) {
                    if (now[r][c] != 1) {
                        continue;
                    }
                    // read neighbours from this minute's crate, write to the next one
                    if ((r > 0 && now[r - 1][c] == 2) || (r + 1 < height && now[r + 1][c] == 2)
                            || (c > 0 && now[r][c - 1] == 2) || (c + 1 < width && now[r][c + 1] == 2)) {
                        next[r][c] = 2;
                        changed = true;
                    } else {
                        anyFresh = true;
                    }
                }
            }
            if (!changed) {
                return anyFresh ? -1 : minutes;
            }
            minutes++;
            now = next;
        }
    }
}
