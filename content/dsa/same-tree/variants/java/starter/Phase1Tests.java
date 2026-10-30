import static kit.Check.assertEquals;

import kit.Test;

public class Phase1Tests {

    private static void expect(boolean want, Integer[] p, Integer[] q) {
        TreeNode a = TreeNode.of(p);
        TreeNode b = TreeNode.of(q);
        String trees = TreeNode.levelOrder(a) + " and " + TreeNode.levelOrder(b);
        assertEquals(want, SameTree.isSame(a, b), "same: " + trees);
        assertEquals(want, SameTree.isSame(b, a), "same, asked the other way round: " + trees);
    }

    private static Integer[] tree(Integer... levelOrder) {
        return levelOrder;
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
        expect(true, tree(7, 2, 9, null, 4), tree(7, 2, 9, null, 4));
    }

    @Test(order = 2)
    void sameValuesOnTheOtherSide() {
        expect(false, tree(7, 2, 9, null, 4), tree(7, 2, 9, 4));
    }

    @Test(order = 3)
    void oneEmptyOneNot() {
        expect(false, tree(), tree(0));
    }

    @Test(order = 4)
    void bothEmpty() {
        assertEquals(true, SameTree.isSame(null, null), "two empty trees are the same");
    }

    @Test(order = 5)
    void oneValueDiffersDeepDown() {
        expect(false, tree(3, 1, 6, 0, 2, 5, 8), tree(3, 1, 6, 0, 2, 5, -8));
    }

    @Test(order = 6)
    void oneTreeHasAnExtraLeaf() {
        expect(false, tree(3, 1, 6), tree(3, 1, 6, null, null, 4));
    }

    @Test(order = 7)
    void mirrorImagesAreNotTheSame() {
        expect(false, tree(6, -1, 4), tree(6, 4, -1));
    }

    @Test(order = 8)
    void valuesAtTheLimits() {
        expect(true, tree(-10_000, 10_000, null, -10_000), tree(-10_000, 10_000, null, -10_000));
    }

    @Test(order = 9)
    void deepestAllowedPaths() {
        int[] values = new int[1_000]; // depth at the limit: recursion must still fit
        for (int i = 0; i < values.length; i++) {
            values[i] = i;
        }
        TreeNode a = TreeNode.path(values, false, null);
        TreeNode b = TreeNode.path(values, false, null);
        assertEquals(true, SameTree.isSame(a, b), "two equal right paths of 1,000 nodes");
        values[999] = -1;
        assertEquals(false, SameTree.isSame(a, TreeNode.path(values, false, null)), "paths that differ at the last node");
    }

    @Test(order = 10, timeoutMs = 2000)
    void largeTrees() {
        assertEquals(true, SameTree.isSame(complete(100_000), complete(100_000)), "two equal complete trees");
        TreeNode b = complete(100_000);
        TreeNode n = b;
        while (n.right != null) {
            n = n.right; // the rightmost node, compared last
        }
        n.val++;
        assertEquals(false, SameTree.isSame(complete(100_000), b), "complete trees that differ at the rightmost node");
    }
}
