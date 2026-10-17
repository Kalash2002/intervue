import static kit.Check.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import kit.Test;

public class Phase1Tests {

    @Test(order = 1)
    void sample() {
        assertEquals(3L, Clusters.count(6, new int[][] {{0, 4}, {4, 2}, {1, 5}}));
        assertEquals(2L, Clusters.count(5, new int[][] {{0, 1}, {1, 2}, {2, 0}, {3, 4}}));
    }

    @Test(order = 2)
    void noCablesAtAll() {
        assertEquals(1L, Clusters.count(1, new int[][] {}));
        assertEquals(4L, Clusters.count(4, new int[][] {}));
    }

    @Test(order = 3)
    void aLoopIsStillOneNetwork() {
        // three cables among three machines: counting machines minus cables would give 0
        assertEquals(1L, Clusters.count(3, new int[][] {{0, 1}, {1, 2}, {0, 2}}));
    }

    @Test(order = 4)
    void repeatedCables() {
        assertEquals(3L, Clusters.count(4, new int[][] {{2, 3}, {3, 2}, {2, 3}}));
    }

    @Test(order = 5)
    void twoCablesIntoTheSameMachine() {
        // machine 1 is joined to 0 and then to 2; all three are one network
        assertEquals(1L, Clusters.count(3, new int[][] {{0, 1}, {2, 1}}));
    }

    @Test(order = 6)
    void groupsThatMergeLate() {
        assertEquals(1L, Clusters.count(6, new int[][] {{0, 1}, {2, 3}, {4, 5}, {1, 2}, {3, 4}}));
        assertEquals(2L, Clusters.count(7, new int[][] {{5, 6}, {0, 1}, {2, 3}, {3, 0}, {6, 4}}));
    }

    @Test(order = 7)
    void randomNetworksWithAKnownCount() {
        Random random = new Random(41);
        for (int round = 0; round < 40; round++) {
            int n = 1 + random.nextInt(14);
            int groups = 1 + random.nextInt(n);
            int[] group = new int[n]; // the network each machine is planted in; machine g founds network g
            List<int[]> links = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                group[i] = i < groups ? i : random.nextInt(groups);
                if (i >= groups) {
                    List<Integer> earlier = new ArrayList<>();
                    for (int j = 0; j < i; j++) {
                        if (group[j] == group[i]) {
                            earlier.add(j);
                        }
                    }
                    links.add(new int[] {i, earlier.get(random.nextInt(earlier.size()))});
                }
            }
            int extra = random.nextInt(n);
            for (int k = 0; k < extra; k++) {
                int a = random.nextInt(n);
                int b = random.nextInt(n);
                if (a != b && group[a] == group[b]) {
                    links.add(new int[] {a, b}); // a loop inside one network
                }
            }
            Collections.shuffle(links, random);
            for (int[] link : links) {
                if (random.nextBoolean()) {
                    int swap = link[0];
                    link[0] = link[1];
                    link[1] = swap;
                }
            }
            int[][] given = links.toArray(new int[0][]);
            assertEquals((long) groups, (long) Clusters.count(n, given), "round " + round);
        }
    }

    @Test(order = 8, timeoutMs = 1000)
    void aHundredThousandMachinesInOneLine() {
        int n = 100_000;
        int[][] links = new int[n - 1][];
        for (int i = 0; i < n - 1; i++) {
            links[i] = new int[] {i, i + 1};
        }
        assertEquals(1L, Clusters.count(n, links));
    }

    @Test(order = 9, timeoutMs = 600)
    void aLongLineListedFromTheFarEnd() {
        int n = 60_001; // machines 0 to 59,999 in a line, and machine 60,000 on its own
        int[][] links = new int[n - 2][];
        for (int i = 0; i < n - 2; i++) {
            int k = n - 3 - i;
            links[i] = new int[] {k, k + 1};
        }
        assertEquals(2L, Clusters.count(n, links));
    }
}
