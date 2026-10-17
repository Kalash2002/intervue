import static kit.Check.assertEquals;

import java.util.Random;
import kit.Test;

public class Phase1Tests {

    private static void expect(int want, TreeNode root, int k) {
        String tree = TreeNode.levelOrder(root);
        tree = tree.length() > 100 ? "the tree" : tree;
        assertEquals(want, KthSmallest.kthSmallest(root, k), "k = " + k + " in " + tree);
    }

    /** A search tree holding values[lo..hi], each subtree split at its middle. */
    private static TreeNode balanced(int[] values, int lo, int hi) {
        if (lo > hi) {
            return null;
        }
        int mid = (lo + hi) >>> 1;
        return new TreeNode(values[mid], balanced(values, lo, mid - 1), balanced(values, mid + 1, hi));
    }

    @Test(order = 1)
    void sample() {
        expect(12, TreeNode.of(31, 12, 47, 5, 20, null, 60, null, 9), 3);
    }

    @Test(order = 2)
    void largestValue() {
        expect(60, TreeNode.of(31, 12, 47, 5, 20, null, 60, null, 9), 7);
    }

    @Test(order = 3)
    void smallestValue() {
        expect(5, TreeNode.of(31, 12, 47, 5, 20, null, 60, null, 9), 1);
    }

    @Test(order = 4)
    void everyK() {
        TreeNode root = TreeNode.of(31, 12, 47, 5, 20, null, 60, null, 9);
        int[] sorted = {5, 9, 12, 20, 31, 47, 60};
        for (int k = 1; k <= sorted.length; k++) {
            expect(sorted[k - 1], root, k);
        }
    }

    @Test(order = 5)
    void singleNode() {
        expect(-1_000_000_000, TreeNode.of(-1_000_000_000), 1);
    }

    @Test(order = 6)
    void negativeAndExtremeValues() {
        TreeNode root = TreeNode.of(-4, -999_999_999, 1_000_000_000, null, -8, 6);
        int[] sorted = {-999_999_999, -8, -4, 6, 1_000_000_000};
        for (int k = 1; k <= sorted.length; k++) {
            expect(sorted[k - 1], root, k);
        }
    }

    @Test(order = 7)
    void deepestAllowedPaths() {
        int[] values = new int[1_000]; // depth at the limit: recursion must still fit
        for (int i = 0; i < values.length; i++) {
            values[i] = 1_000 - 2 * i; // falling to the left: the smallest value is at the bottom
        }
        TreeNode left = TreeNode.path(values, true, null);
        expect(-998, left, 1);
        expect(0, left, 500);
        expect(1_000, left, 1_000);
        for (int i = 0; i < values.length; i++) {
            values[i] = 2 * i - 1_000; // rising to the right
        }
        TreeNode right = TreeNode.path(values, false, null);
        expect(-1_000, right, 1);
        expect(998, right, 1_000);
    }

    @Test(order = 8, timeoutMs = 700)
    void manySmallKInALargeTree() {
        int handle = 983; // a path on top of a balanced block: depth exactly 1,000
        int[] block = new int[100_000 - handle];
        for (int i = 0; i < block.length; i++) {
            block[i] = 3 * i - 150_000;
        }
        int[] top = new int[handle];
        for (int i = 0; i < handle; i++) {
            top[i] = 1_000_000 - i; // larger than the block, falling going down
        }
        TreeNode root = TreeNode.path(top, true, balanced(block, 0, block.length - 1));
        Random random = new Random(56);
        for (int q = 0; q < 2_000; q++) {
            int k = 1 + random.nextInt(1_000);
            assertEquals(block[k - 1], KthSmallest.kthSmallest(root, k), "k = " + k + " in the large tree");
        }
        assertEquals(top[0], KthSmallest.kthSmallest(root, 100_000), "k = 100000, the largest value");
    }
}
