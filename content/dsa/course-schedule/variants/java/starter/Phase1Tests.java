import static kit.Check.assertEquals;

import java.util.Random;
import kit.Test;

public class Phase1Tests {

    @Test(order = 1)
    void sample() {
        assertEquals(true, Curriculum.canFinish(5, new int[][] {{3, 4}, {0, 4}, {1, 3}, {1, 0}, {2, 1}}));
        assertEquals(false, Curriculum.canFinish(3, new int[][] {{0, 2}, {1, 0}, {2, 1}}));
    }

    @Test(order = 2)
    void aCourseThatListsItself() {
        assertEquals(false, Curriculum.canFinish(2, new int[][] {{1, 1}}));
        assertEquals(false, Curriculum.canFinish(4, new int[][] {{1, 0}, {2, 1}, {2, 2}, {3, 2}}));
    }

    @Test(order = 3)
    void noPrerequisites() {
        assertEquals(true, Curriculum.canFinish(1, new int[][] {}));
        assertEquals(true, Curriculum.canFinish(5, new int[][] {}));
    }

    @Test(order = 4)
    void aLoopOfTwoBlocksThePlanEvenWithFreeCourses() {
        assertEquals(false, Curriculum.canFinish(5, new int[][] {{3, 4}, {4, 3}, {1, 0}}));
    }

    @Test(order = 5)
    void twoRoutesToTheSameCourseAreNotALoop() {
        // course 4 is reached through 2 and through 3; a search that treats "seen before" as a loop fails here
        assertEquals(true, Curriculum.canFinish(5, new int[][] {{2, 0}, {3, 0}, {4, 2}, {4, 3}, {1, 4}}));
        assertEquals(true, Curriculum.canFinish(5, new int[][] {{4, 0}, {4, 1}, {4, 2}, {4, 3}, {1, 0}, {2, 1}}));
    }

    @Test(order = 6)
    void aLoopFarFromCourseZero() {
        // courses 3, 4 and 5 form a loop that no search starting at course 0 reaches
        assertEquals(false, Curriculum.canFinish(6, new int[][] {{1, 0}, {2, 1}, {4, 3}, {5, 4}, {3, 5}}));
    }

    @Test(order = 7)
    void repeatedPairs() {
        assertEquals(true, Curriculum.canFinish(3, new int[][] {{1, 0}, {1, 0}, {2, 1}, {2, 1}}));
        assertEquals(false, Curriculum.canFinish(3, new int[][] {{1, 0}, {1, 0}, {0, 1}}));
    }

    @Test(order = 8)
    void randomPlansWithAndWithoutALoop() {
        Random random = new Random(87);
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
            assertEquals(true, Curriculum.canFinish(n, pairs), "a plan that follows a hidden order, round " + round);
            int[][] looped = new int[m + 1][];
            System.arraycopy(pairs, 0, looped, 0, m);
            int[] flip = pairs[random.nextInt(m)];
            looped[m] = new int[] {flip[1], flip[0]}; // the same two courses, each before the other
            assertEquals(false, Curriculum.canFinish(n, looped), "the same plan with one pair reversed, round " + round);
        }
    }

    @Test(order = 9, timeoutMs = 1000)
    void aChainNumberedUpwards() {
        int n = 100_000;
        int[][] pairs = new int[n - 1][];
        for (int i = 0; i < n - 1; i++) {
            pairs[i] = new int[] {i + 1, i}; // each course needs the one numbered below it
        }
        assertEquals(true, Curriculum.canFinish(n, pairs));
    }

    @Test(order = 10, timeoutMs = 1000)
    void aChainNumberedDownwards() {
        // course n - 1 comes first; a sweep over courses from 0 upwards can free only one course per sweep
        int n = 100_000;
        int[][] pairs = new int[n - 1][];
        for (int i = 0; i < n - 1; i++) {
            pairs[i] = new int[] {i, i + 1}; // each course needs the one numbered above it
        }
        assertEquals(true, Curriculum.canFinish(n, pairs));
    }

    @Test(order = 11, timeoutMs = 1000)
    void aDownwardChainThatEndsInALoop() {
        int n = 100_000;
        int[][] pairs = new int[n][];
        for (int i = 0; i < n - 1; i++) {
            pairs[i] = new int[] {i, i + 1};
        }
        pairs[n - 1] = new int[] {1, 0}; // courses 0 and 1, the last on the chain, each need the other
        assertEquals(false, Curriculum.canFinish(n, pairs));
    }
}
