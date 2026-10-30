import static kit.Check.assertEquals;

import kit.Test;

public class Phase1Tests {

    private static void expect(boolean want, TreeNode root) {
        String tree = TreeNode.levelOrder(root);
        assertEquals(want, Balanced.isBalanced(root), "balanced: " + tree);
    }

    private static void expect(boolean want, Integer... levelOrder) {
        expect(want, TreeNode.of(levelOrder));
    }

    /** A complete tree of n nodes. */
    private static TreeNode complete(int n) {
        Integer[] values = new Integer[n];
        for (int i = 0; i < n; i++) {
            values[i] = i % 20_001 - 10_000;
        }
        return TreeNode.of(values);
    }

    @Test(order = 1)
    void sample() {
        expect(true, 8, 4, 12, 2, 6, null, 14, 1);
    }

    @Test(order = 2)
    void rootLooksFineButAChildIsNot() {
        expect(false, 5, 3, 9, 1, null, 7, 10, 0, null, 6);
    }

    @Test(order = 3)
    void emptyTree() {
        expect(true, (TreeNode) null);
    }

    @Test(order = 4)
    void singleNode() {
        expect(true, 21);
    }

    @Test(order = 5)
    void twoInALineIsBalanced() {
        expect(true, -4, null, -9);
    }

    @Test(order = 6)
    void threeInALineIsNot() {
        expect(false, 2, 1, null, 0);
    }

    @Test(order = 7)
    void leavesMayBeTwoLevelsApart() {
        expect(true, 50, 30, 70, 20, 40, 60, 80, 10, 25, 35, null, 55, null, null, null, 5);
    }

    @Test(order = 8)
    void deepestAllowedPathIsNotBalanced() {
        TreeNode root = TreeNode.path(new int[1_000], true, null); // depth at the limit: recursion must still fit
        assertEquals(false, Balanced.isBalanced(root), "a path of 1,000 nodes");
    }

    @Test(order = 9, timeoutMs = 2000)
    void largeCompleteTreeIsBalanced() {
        assertEquals(true, Balanced.isBalanced(complete(100_000)), "a complete tree is balanced");
    }

    @Test(order = 10, timeoutMs = 2000)
    void largeTreeWithOneDeepFlaw() {
        TreeNode root = complete(99_998);
        TreeNode n = root;
        while (n.left != null) {
            n = n.left; // down to the leftmost leaf, on the deepest level
        }
        n.left = TreeNode.path(new int[] {-1, 1}, true, null); // that leaf now has a two-node tail
        assertEquals(false, Balanced.isBalanced(root), "the leftmost leaf has a two-node tail and no right child");
    }
}
