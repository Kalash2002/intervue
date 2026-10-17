import static kit.Check.assertEquals;

import kit.Test;

public class Phase1Tests {

    private static final int MIN = Integer.MIN_VALUE;
    private static final int MAX = Integer.MAX_VALUE;

    private static void expect(boolean want, TreeNode root) {
        String tree = TreeNode.levelOrder(root);
        tree = tree.length() > 100 ? "the tree" : tree;
        assertEquals(want, ValidBst.isValidBST(root), "valid search tree: " + tree);
    }

    private static void expect(boolean want, Integer... levelOrder) {
        expect(want, TreeNode.of(levelOrder));
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
        expect(true, 41, 17, 63, 8, 29, 50, 77);
    }

    @Test(order = 2)
    void rightSubtreeHidesASmallValue() {
        expect(false, 41, 17, 63, 8, 29, 35, 77);
    }

    @Test(order = 3)
    void leftSubtreeHidesALargeValue() {
        expect(false, 50, 30, 80, 20, 40, null, null, null, null, 35, 55);
    }

    @Test(order = 4)
    void extremeValuesAreLegal() {
        expect(true, MIN, null, MAX);
        expect(true, MAX);
        expect(true, MIN);
        expect(true, MAX, MIN);
        expect(true, 0, MIN, MAX);
    }

    @Test(order = 5)
    void equalValuesAreNot() {
        expect(false, 7, 7);
        expect(false, 7, null, 7);
        expect(false, 10, 5, 15, null, 10);
    }

    @Test(order = 6)
    void equalExtremeValuesAreNot() {
        expect(false, MIN, MIN);
        expect(false, MAX, null, MAX);
        expect(false, 0, MIN, MAX, null, MIN);
    }

    @Test(order = 7)
    void singleNode() {
        expect(true, -3);
    }

    @Test(order = 8)
    void deepestAllowedPath() {
        int[] values = new int[1_000]; // depth at the limit: recursion must still fit
        for (int i = 0; i < values.length; i++) {
            values[i] = 5 * i - 2_500;
        }
        expect(true, TreeNode.path(values, false, null)); // rising to the right: valid
        values[999] = values[0]; // the bottom repeats the top
        expect(false, TreeNode.path(values, false, null));
    }

    @Test(order = 9, timeoutMs = 2000)
    void largeTree() {
        int handle = 983; // a path on top of a balanced block: depth exactly 1,000
        int[] block = new int[100_000 - handle];
        for (int i = 0; i < block.length; i++) {
            block[i] = 2 * i - 100_000;
        }
        int[] top = new int[handle];
        for (int i = 0; i < handle; i++) {
            top[i] = 2 * block.length + 2 * (handle - i); // larger than the block, falling going down
        }
        TreeNode blockRoot = balanced(block, 0, block.length - 1);
        TreeNode root = TreeNode.path(top, true, blockRoot);
        expect(true, root);
        TreeNode leaf = blockRoot.right;
        while (leaf.left != null) {
            leaf = leaf.left;
        }
        leaf.val = blockRoot.val - 1; // still below its parent, but now on the wrong side of the block's root
        expect(false, root);
    }
}
