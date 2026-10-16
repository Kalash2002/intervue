import static kit.Check.assertEquals;

import java.util.Arrays;
import kit.Test;

public class Phase1Tests {

    private static void expect(boolean want, Integer[] root, Integer[] sub) {
        TreeNode r = TreeNode.of(root);
        TreeNode s = TreeNode.of(sub);
        String trees = TreeNode.levelOrder(s) + " inside " + TreeNode.levelOrder(r);
        assertEquals(want, Subtree.isSubtree(r, s), trees);
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
        expect(true, tree(9, 4, 6, 1, 5, null, 8), tree(4, 1, 5));
    }

    @Test(order = 2)
    void extraNodeBelowTheMatch() {
        expect(false, tree(9, 4, 6, 1, 5, null, 8, null, null, 3), tree(4, 1, 5));
    }

    @Test(order = 3)
    void firstEqualValueIsNotTheMatch() {
        expect(true, tree(5, 5, null, 5), tree(5, 5));
    }

    @Test(order = 4)
    void wholeTree() {
        expect(true, tree(0, -1), tree(0, -1));
    }

    @Test(order = 5)
    void singleLeaf() {
        expect(true, tree(9, 4, 6, 1, 5, null, 8), tree(8));
    }

    @Test(order = 6)
    void sidesMatter() {
        expect(false, tree(5, 8, null, 1), tree(8, null, 1));
    }

    @Test(order = 7)
    void valuesDoNotRunTogether() {
        expect(false, tree(12), tree(2));
        expect(false, tree(-7), tree(7));
        expect(false, tree(4, 31), tree(1));
    }

    @Test(order = 8)
    void subBiggerThanRoot() {
        expect(false, tree(3), tree(3, null, 1));
    }

    @Test(order = 9, timeoutMs = 2000)
    void deepInsideALargeTree() {
        TreeNode root = complete(100_000);
        TreeNode copy = complete(100_000);
        for (int i = 0; i < 10; i++) {
            copy = i % 2 == 0 ? copy.left : copy.right; // ten levels down: a subtree of about 100 nodes
        }
        assertEquals(true, Subtree.isSubtree(root, copy), "a subtree ten levels down a complete tree of 100,000 nodes");
        TreeNode leaf = copy;
        while (leaf.right != null) {
            leaf = leaf.right;
        }
        leaf.val += leaf.val < 10_000 ? 1 : -1; // now differs from the node it was copied from
        assertEquals(false, Subtree.isSubtree(root, copy), "the same subtree with its rightmost node changed");
    }

    @Test(order = 10, timeoutMs = 400)
    void longPaths() {
        int n = 100_000; // one node per level: one call per level overflows the call stack
        int m = 20_000;
        int[] rootValues = new int[n];
        int[] subValues = new int[m];
        Arrays.fill(rootValues, 3);
        Arrays.fill(subValues, 3);
        rootValues[n - 1] = 4; // only the bottom m nodes of root match: every node above them is a near miss
        subValues[m - 1] = 4;
        TreeNode root = TreeNode.path(rootValues, true, null);
        TreeNode sub = TreeNode.path(subValues, true, null);
        assertEquals(true, Subtree.isSubtree(root, sub), "a path of 20,000 nodes at the bottom of a path of 100,000");
    }
}
