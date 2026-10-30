import static kit.Check.assertEquals;

import kit.Test;

public class Phase1Tests {

    private static void expect(int want, TreeNode root) {
        String tree = TreeNode.levelOrder(root);
        tree = tree.length() > 100 ? "the tree" : tree;
        assertEquals(want, MaxPathSum.maxPathSum(root), "best path sum in " + tree);
    }

    /** A tree of the given size, each subtree split at its middle, every node holding value. */
    private static TreeNode balanced(int size, int value) {
        if (size == 0) {
            return null;
        }
        int left = (size - 1) / 2;
        return new TreeNode(value, balanced(left, value), balanced(size - 1 - left, value));
    }

    @Test(order = 1)
    void sample() {
        expect(21, TreeNode.of(-4, 9, 6, -2, 7, null, -5, null, null, null, null, 8));
    }

    @Test(order = 2)
    void bestPathAvoidsTheRoot() {
        expect(23, TreeNode.of(-3, 12, -8, 5, 6));
    }

    @Test(order = 3)
    void allNegative() {
        expect(-3, TreeNode.of(-7, -3, -9));
        expect(-5, TreeNode.of(-5, -5, -5, -5));
        expect(-10_000, TreeNode.of(-10_000));
        expect(-1, TreeNode.of(-8, -6, -4, null, -1, -2));
    }

    @Test(order = 4)
    void singleNode() {
        expect(10_000, TreeNode.of(10_000));
        expect(0, TreeNode.of(0));
    }

    @Test(order = 5)
    void losingBranchIsLeftOut() {
        expect(8, TreeNode.of(6, -9, 2));
        expect(10, TreeNode.of(1, -6, 4, 10, null, null, -2));
    }

    @Test(order = 6)
    void forkCannotBeExtended() {
        expect(12, TreeNode.of(1, 5, null, 4, 3));
        expect(20, TreeNode.of(2, 9, -30, 3, 8, null, 4));
    }

    @Test(order = 7)
    void deepestAllowedPaths() {
        int[] values = new int[1_000]; // depth at the limit: recursion must still fit
        for (int i = 0; i < values.length; i++) {
            values[i] = i % 2 == 0 ? 3 : -1; // ends on -1, so the best path stops one short
        }
        expect(1_001, TreeNode.path(values, true, null));
        expect(1_001, TreeNode.path(values, false, null));
        java.util.Arrays.fill(values, 10_000);
        expect(10_000_000, TreeNode.path(values, true, null));
    }

    @Test(order = 8, timeoutMs = 2000)
    void largeTree() {
        int handle = 983; // a path on top of a balanced block of 99,017 nodes: depth exactly 1,000
        int[] top = new int[handle];
        for (int i = 0; i < handle; i++) {
            top[i] = i < 100 ? -50 : 10;
        }
        // The block's -1s only lower a sum, so the best path is the 883 tens of the handle.
        expect(8_830, TreeNode.path(top, true, balanced(100_000 - handle, -1)));
        java.util.Arrays.fill(top, -10_000);
        // Now the best path is the longest one in the block: 16 nodes down each side of its root, plus the root.
        expect(33, TreeNode.path(top, true, balanced(100_000 - handle, 1)));
    }
}
