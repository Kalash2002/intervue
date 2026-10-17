import static kit.Check.assertEquals;
import static kit.Check.fail;

import java.util.Arrays;
import java.util.Random;
import kit.Test;

public class Phase1Tests {

    // Any valid order passes: every course once, and each pair's prerequisite before its course.
    private static void expectValidOrder(int n, int[][] pairs) {
        int[] order = StudyPlan.findOrder(n, pairs);
        assertEquals(n, order.length, "length of the order, which must list every course");
        int[] position = new int[n];
        Arrays.fill(position, -1);
        for (int i = 0; i < n; i++) {
            int course = order[i];
            if (course < 0 || course >= n) {
                fail("course " + course + " at index " + i + " does not exist");
            }
            if (position[course] != -1) {
                fail("course " + course + " appears twice in " + shown(order));
            }
            position[course] = i;
        }
        for (int[] pair : pairs) {
            if (position[pair[1]] >= position[pair[0]]) {
                fail("course " + pair[1] + " must come before course " + pair[0] + " in " + shown(order));
            }
        }
    }

    private static void expectNoOrder(int n, int[][] pairs) {
        int[] order = StudyPlan.findOrder(n, pairs);
        assertEquals("[]", shown(order), "no order exists, so the array must be empty");
    }

    private static String shown(int[] order) {
        return order.length > 20 ? order.length + " courses" : Arrays.toString(order);
    }

    @Test(order = 1)
    void sample() {
        expectValidOrder(5, new int[][] {{2, 0}, {2, 1}, {4, 2}, {3, 1}});
        expectNoOrder(3, new int[][] {{1, 2}, {2, 1}});
        expectValidOrder(3, new int[][] {});
    }

    @Test(order = 2)
    void aCourseThatListsItself() {
        expectNoOrder(2, new int[][] {{1, 1}});
        expectNoOrder(4, new int[][] {{1, 0}, {2, 1}, {2, 2}, {3, 2}});
    }

    @Test(order = 3)
    void coursesWithoutPairsStillAppear() {
        expectValidOrder(1, new int[][] {});
        expectValidOrder(4, new int[][] {});
        expectValidOrder(6, new int[][] {{1, 0}, {3, 1}});
    }

    @Test(order = 4)
    void theOnlyOrderOfAChainIsNotReversed() {
        // one valid order only: 0, 1, 2, 3; listing courses as a search finishes them gives it backwards
        expectValidOrder(4, new int[][] {{1, 0}, {2, 1}, {3, 2}});
        expectValidOrder(4, new int[][] {{0, 3}, {3, 1}, {1, 2}});
    }

    @Test(order = 5)
    void twoRoutesToTheSameCourse() {
        expectValidOrder(5, new int[][] {{2, 0}, {3, 0}, {4, 2}, {4, 3}, {1, 4}});
        expectValidOrder(5, new int[][] {{4, 0}, {4, 1}, {4, 2}, {4, 3}, {1, 0}, {2, 1}});
    }

    @Test(order = 6)
    void aLoopAnywhereMeansNoOrder() {
        // courses 3, 4 and 5 form a loop that no search starting at course 0 reaches; 0, 1, 2 alone is not enough
        expectNoOrder(6, new int[][] {{1, 0}, {2, 1}, {4, 3}, {5, 4}, {3, 5}});
        expectNoOrder(5, new int[][] {{3, 4}, {4, 3}, {1, 0}});
    }

    @Test(order = 7)
    void repeatedPairs() {
        expectValidOrder(3, new int[][] {{1, 0}, {1, 0}, {2, 1}, {2, 1}});
        expectNoOrder(3, new int[][] {{1, 0}, {1, 0}, {0, 1}});
    }

    @Test(order = 8)
    void randomPlansWithAndWithoutALoop() {
        Random random = new Random(88);
        for (int round = 0; round < 30; round++) {
            int n = 2 + random.nextInt(11);
            int[] rank = new int[n]; // a hidden order the plan respects
            for (int i = 0; i < n; i++) {
                rank[i] = i;
            }
            for (int i = n - 1; i > 0; i--) {
                int j = random.nextInt(i + 1);
                int swap = rank[i];
                rank[i] = rank[j];
                rank[j] = swap;
            }
            int m = 1 + random.nextInt(2 * n);
            int[][] pairs = new int[m][];
            for (int i = 0; i < m; i++) {
                int u = random.nextInt(n);
                int v = random.nextInt(n - 1);
                if (v >= u) {
                    v++; // two different courses
                }
                pairs[i] = rank[u] > rank[v] ? new int[] {u, v} : new int[] {v, u}; // the lower rank comes first
            }
            expectValidOrder(n, pairs);
            int[][] looped = new int[m + 1][];
            System.arraycopy(pairs, 0, looped, 0, m);
            int[] flip = pairs[random.nextInt(m)];
            looped[m] = new int[] {flip[1], flip[0]}; // the same two courses, each before the other
            expectNoOrder(n, looped);
        }
    }

    @Test(order = 9, timeoutMs = 1000)
    void aChainNumberedUpwards() {
        int n = 100_000;
        int[][] pairs = new int[n - 1][];
        for (int i = 0; i < n - 1; i++) {
            pairs[i] = new int[] {i + 1, i}; // each course needs the one numbered below it
        }
        expectValidOrder(n, pairs);
    }

    @Test(order = 10, timeoutMs = 1000)
    void aChainNumberedDownwards() {
        // course n - 1 comes first; a sweep over courses from 0 upwards can add only one course per sweep
        int n = 100_000;
        int[][] pairs = new int[n - 1][];
        for (int i = 0; i < n - 1; i++) {
            pairs[i] = new int[] {i, i + 1}; // each course needs the one numbered above it
        }
        expectValidOrder(n, pairs);
    }

    @Test(order = 11, timeoutMs = 1000)
    void aDownwardChainThatEndsInALoop() {
        int n = 100_000;
        int[][] pairs = new int[n][];
        for (int i = 0; i < n - 1; i++) {
            pairs[i] = new int[] {i, i + 1};
        }
        pairs[n - 1] = new int[] {1, 0}; // courses 0 and 1, the last on the chain, each need the other
        expectNoOrder(n, pairs);
    }
}
