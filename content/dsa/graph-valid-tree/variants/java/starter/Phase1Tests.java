import static kit.Check.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import kit.Test;

public class Phase1Tests {

    @Test(order = 1)
    void sample() {
        assertEquals(true, Pipes.isTree(5, new int[][] {{3, 0}, {0, 2}, {2, 4}, {0, 1}}));
        assertEquals(false, Pipes.isTree(5, new int[][] {{0, 1}, {1, 2}, {2, 0}, {3, 4}}));
    }

    @Test(order = 2)
    void oneAndTwoValves() {
        assertEquals(true, Pipes.isTree(1, new int[][] {}));
        assertEquals(false, Pipes.isTree(2, new int[][] {}));
        assertEquals(true, Pipes.isTree(2, new int[][] {{1, 0}}));
    }

    @Test(order = 3)
    void theRightNumberOfPipesIsNotEnough() {
        // five pipes for six valves, but 1, 2, 3 form a loop and 4, 5 are cut off
        assertEquals(false, Pipes.isTree(6, new int[][] {{0, 1}, {1, 2}, {2, 3}, {3, 1}, {4, 5}}));
    }

    @Test(order = 4)
    void noLoopIsNotEnough() {
        assertEquals(false, Pipes.isTree(4, new int[][] {{0, 1}, {2, 3}}));
        assertEquals(false, Pipes.isTree(3, new int[][] {{2, 1}}));
    }

    @Test(order = 5)
    void connectedWithOneExtraPipe() {
        assertEquals(false, Pipes.isTree(4, new int[][] {{0, 1}, {1, 2}, {2, 3}, {3, 0}}));
    }

    @Test(order = 6)
    void aStarAndAZigzagLine() {
        assertEquals(true, Pipes.isTree(6, new int[][] {{5, 0}, {5, 1}, {5, 2}, {5, 3}, {5, 4}}));
        assertEquals(true, Pipes.isTree(6, new int[][] {{4, 2}, {2, 0}, {0, 5}, {5, 1}, {1, 3}}));
    }

    @Test(order = 7)
    void randomTreesAndTheirBrokenCopies() {
        Random random = new Random(59);
        for (int round = 0; round < 40; round++) {
            int n = 3 + random.nextInt(12);
            int[] label = new int[n];
            for (int i = 0; i < n; i++) {
                label[i] = i;
            }
            for (int i = n - 1; i > 0; i--) {
                int j = random.nextInt(i + 1);
                int swap = label[i];
                label[i] = label[j];
                label[j] = swap;
            }
            List<int[]> tree = new ArrayList<>();
            Set<Long> joined = new HashSet<>();
            for (int i = 1; i < n; i++) {
                int a = label[i];
                int b = label[random.nextInt(i)]; // hang each valve on an earlier one
                tree.add(random.nextBoolean() ? new int[] {a, b} : new int[] {b, a});
                joined.add(key(a, b));
            }
            Collections.shuffle(tree, random);
            assertEquals(true, Pipes.isTree(n, tree.toArray(new int[0][])), "a random tree, round " + round);

            int a;
            int b;
            do {
                a = random.nextInt(n);
                b = random.nextInt(n);
            } while (a == b || joined.contains(key(a, b)));
            List<int[]> looped = new ArrayList<>(tree);
            looped.add(random.nextInt(looped.size() + 1), new int[] {a, b});
            assertEquals(false, Pipes.isTree(n, looped.toArray(new int[0][])), "the tree plus one pipe, round " + round);

            List<int[]> cut = new ArrayList<>(tree);
            cut.remove(random.nextInt(cut.size()));
            assertEquals(false, Pipes.isTree(n, cut.toArray(new int[0][])), "the tree minus one pipe, round " + round);
        }
    }

    private static long key(int a, int b) {
        return (long) Math.min(a, b) * 1_000_000L + Math.max(a, b);
    }

    @Test(order = 8, timeoutMs = 1000)
    void aHundredThousandValvesInOneLine() {
        int n = 100_000;
        int[][] links = new int[n - 1][];
        for (int i = 0; i < n - 1; i++) {
            links[i] = new int[] {i, i + 1};
        }
        assertEquals(true, Pipes.isTree(n, links));
    }
}
