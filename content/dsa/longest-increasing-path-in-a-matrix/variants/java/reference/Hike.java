/** Finds the longest walk on a height map that rises at every step. */
public class Hike {

    private static final int[][] STEPS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

    /** Returns the number of cells on the longest walk that steps to side neighbours and always rises. */
    static int longestClimb(int[][] heights) {
        int rows = heights.length;
        int cols = heights[0].length;
        int[] higher = new int[rows * cols]; // higher[cell]: its higher neighbours not peeled off yet
        int[] queue = new int[rows * cols];
        int tail = 0;
        for (int cell = 0; cell < rows * cols; cell++) {
            for (int[] step : STEPS) {
                int r = cell / cols + step[0];
                int c = cell % cols + step[1];
                if (r >= 0 && c >= 0 && r < rows && c < cols && heights[r][c] > heights[cell / cols][cell % cols]) {
                    higher[cell]++;
                }
            }
            if (higher[cell] == 0) {
                queue[tail++] = cell; // nothing higher beside it: its longest climb is the cell alone
            }
        }
        int rounds = 0;
        for (int head = 0; head < tail; ) {
            rounds++; // the longest climb from each cell peeled in this round has exactly `rounds` cells
            for (int end = tail; head < end; head++) {
                int height = heights[queue[head] / cols][queue[head] % cols];
                for (int[] step : STEPS) {
                    int r = queue[head] / cols + step[0];
                    int c = queue[head] % cols + step[1];
                    // a lower neighbour whose last higher neighbour this was is peeled in the next round
                    if (r >= 0 && c >= 0 && r < rows && c < cols && heights[r][c] < height && --higher[r * cols + c] == 0) {
                        queue[tail++] = r * cols + c;
                    }
                }
            }
        }
        return rounds;
    }
}
