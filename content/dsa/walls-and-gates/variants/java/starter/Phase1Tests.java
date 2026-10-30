import static kit.Check.assertEquals;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Random;
import kit.Test;

public class Phase1Tests {

    private static final int INF = Integer.MAX_VALUE;

    private static String text(int[][] rooms) {
        StringBuilder out = new StringBuilder();
        for (int r = 0; r < rooms.length; r++) {
            if (r > 0) {
                out.append('/');
            }
            for (int c = 0; c < rooms[r].length; c++) {
                out.append(c > 0 ? "," : "").append(rooms[r][c] == INF ? "INF" : String.valueOf(rooms[r][c]));
            }
        }
        return out.toString();
    }

    private static void expect(int[][] rooms, int[][] want) {
        FloorPlan.fillDistances(rooms);
        assertEquals(text(want), text(rooms), "the plan after the call, rows separated by /");
    }

    @Test(order = 1)
    void sample() {
        expect(new int[][] {{0, INF, INF, -1}, {-1, -1, INF, INF}, {INF, INF, INF, 0}},
                new int[][] {{0, 1, 2, -1}, {-1, -1, 2, 1}, {3, 2, 1, 0}});
    }

    @Test(order = 2)
    void roomsNoExitCanReachKeepTheMarker() {
        expect(new int[][] {{INF, -1, 0}, {-1, INF, INF}}, new int[][] {{INF, -1, 0}, {-1, 2, 1}});
        // two rooms walled in together: each is next to an unreachable room, and still keeps the marker
        expect(new int[][] {{INF, INF, -1, 0}, {-1, -1, -1, INF}}, new int[][] {{INF, INF, -1, 0}, {-1, -1, -1, 1}});
    }

    @Test(order = 3)
    void noExitAnywhere() {
        expect(new int[][] {{INF, INF}}, new int[][] {{INF, INF}});
        expect(new int[][] {{INF, -1}, {-1, INF}}, new int[][] {{INF, -1}, {-1, INF}});
    }

    @Test(order = 4)
    void theNearestExitWins() {
        expect(new int[][] {{0, INF, INF, INF, 0}}, new int[][] {{0, 1, 2, 1, 0}});
        expect(new int[][] {{0}, {INF}, {INF}, {INF}, {INF}, {0}}, new int[][] {{0}, {1}, {2}, {2}, {1}, {0}});
    }

    @Test(order = 5)
    void wallsForceADetour() {
        expect(new int[][] {{0, -1, INF}, {INF, -1, INF}, {INF, INF, INF}},
                new int[][] {{0, -1, 6}, {1, -1, 5}, {2, 3, 4}});
    }

    @Test(order = 6)
    void plansWithoutOpenRooms() {
        expect(new int[][] {{0, -1}, {-1, 0}}, new int[][] {{0, -1}, {-1, 0}});
        expect(new int[][] {{0}}, new int[][] {{0}});
        expect(new int[][] {{-1}}, new int[][] {{-1}});
    }

    @Test(order = 7)
    void randomSmallPlansAgainstOneSearchPerRoom() {
        Random random = new Random(86);
        for (int round = 0; round < 40; round++) {
            int[][] rooms = new int[1 + random.nextInt(8)][1 + random.nextInt(8)];
            for (int[] row : rooms) {
                for (int c = 0; c < row.length; c++) {
                    int roll = random.nextInt(10);
                    row[c] = roll < 3 ? -1 : roll < 4 ? 0 : INF;
                }
            }
            int[][] want = new int[rooms.length][];
            for (int r = 0; r < rooms.length; r++) {
                want[r] = rooms[r].clone();
                for (int c = 0; c < rooms[r].length; c++) {
                    if (rooms[r][c] == INF) {
                        want[r][c] = stepsToExit(rooms, r, c);
                    }
                }
            }
            expect(rooms, want);
        }
    }

    @Test(order = 8, timeoutMs = 2000)
    void aCorridorWindingThroughAMillionCells() {
        // even rows are open; odd rows hold one open room that joins them, alternately on the right and the left,
        // so the corridor is one chain of 500,500 cells with the exit at its start and room k is k steps away
        int size = 1000;
        int[][] rooms = new int[size][size];
        for (int r = 0; r < size; r++) {
            Arrays.fill(rooms[r], r % 2 == 0 ? INF : -1);
            if (r % 2 == 1) {
                rooms[r][r % 4 == 1 ? size - 1 : 0] = INF;
            }
        }
        rooms[0][0] = 0;
        FloorPlan.fillDistances(rooms);
        assertEquals(999, rooms[0][size - 1]);
        assertEquals(1001, rooms[2][size - 1]);
        assertEquals(500_499, rooms[size - 1][0]);
        long sum = 0;
        int walls = 0;
        for (int[] row : rooms) {
            for (int value : row) {
                if (value == -1) {
                    walls++;
                } else {
                    sum += value; // an unfilled room would add 2147483647
                }
            }
        }
        assertEquals(499_500, walls);
        assertEquals(125_249_874_750L, sum, "the sum of 0 + 1 + ... + 500,499");
    }

    @Test(order = 9, timeoutMs = 2000)
    void aQuarterOfAMillionExits() {
        // every fourth column is an exit; each row reads 0,1,2,1 repeated, ending 0,1,2,3
        int size = 1000;
        int[][] rooms = new int[size][size];
        for (int[] row : rooms) {
            for (int c = 0; c < size; c++) {
                row[c] = c % 4 == 0 ? 0 : INF;
            }
        }
        FloorPlan.fillDistances(rooms);
        long sum = 0;
        for (int[] row : rooms) {
            for (int value : row) {
                sum += value;
            }
        }
        assertEquals(3, rooms[size - 1][size - 1]);
        assertEquals(1_002_000L, sum, "1,002 per row over 1,000 rows");
    }

    // Fewest steps from (r, c) to an exit by a search from that room alone; used for the random test.
    private static int stepsToExit(int[][] rooms, int r, int c) {
        int[][] dist = new int[rooms.length][rooms[0].length];
        for (int[] row : dist) {
            Arrays.fill(row, -1);
        }
        ArrayDeque<int[]> queue = new ArrayDeque<>();
        queue.add(new int[] {r, c});
        dist[r][c] = 0;
        int[][] steps = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        while (!queue.isEmpty()) {
            int[] cell = queue.poll();
            if (rooms[cell[0]][cell[1]] == 0) {
                return dist[cell[0]][cell[1]];
            }
            for (int[] step : steps) {
                int nr = cell[0] + step[0];
                int nc = cell[1] + step[1];
                if (nr >= 0 && nc >= 0 && nr < rooms.length && nc < rooms[0].length
                        && rooms[nr][nc] != -1 && dist[nr][nc] < 0) {
                    dist[nr][nc] = dist[cell[0]][cell[1]] + 1;
                    queue.add(new int[] {nr, nc});
                }
            }
        }
        return INF;
    }
}
