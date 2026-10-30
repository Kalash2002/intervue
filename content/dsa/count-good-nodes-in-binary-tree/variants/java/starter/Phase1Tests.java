import static kit.Check.assertEquals;

import java.util.ArrayList;
import java.util.List;
import kit.Test;

public class Phase1Tests {

    private static void expect(int want, TreeNode root) {
        String tree = TreeNode.levelOrder(root);
        tree = tree.length() > 100 ? "the tree" : tree;
        assertEquals(want, GoodNodes.goodNodes(root), "good nodes in " + tree);
    }

    private static void expect(int want, Integer... levelOrder) {
        expect(want, TreeNode.of(levelOrder));
    }

    @Test(order = 1)
    void sample() {
        expect(5, 5, 3, 8, 6, 1, 7, 9, null, 6);
    }

    @Test(order = 2)
    void allNegative() {
        expect(2, -4, -7, -2, null, -5);
    }

    @Test(order = 3)
    void singleNode() {
        expect(1, -10_000);
    }

    @Test(order = 4)
    void tiesCount() {
        expect(5, 2, 2, 2, 2, null, 2);
    }

    @Test(order = 5)
    void fallingPath() {
        expect(1, 9, 4, null, 1, null, -3);
    }

    @Test(order = 6)
    void goodNodeBelowABadOne() {
        expect(3, 0, -9, null, null, 0, 4);
    }

    @Test(order = 7)
    void beatingTheParentIsNotEnough() {
        expect(1, 10, 2, null, 5);
    }

    @Test(order = 8)
    void valuesAtTheLimits() {
        expect(5, -10_000, 10_000, -10_000, 10_000, -10_000, null, -10_000);
    }

    @Test(order = 9)
    void deepestAllowedPath() {
        int[] values = new int[1_000]; // depth at the limit: recursion must still fit
        for (int i = 0; i < values.length; i++) {
            values[i] = i % 3 == 0 ? i : -i; // every third node is a new maximum
        }
        expect(334, TreeNode.path(values, false, null));
    }

    @Test(order = 10, timeoutMs = 2000)
    void largeTree() {
        int handle = 983; // a rising path on top of a complete block: depth exactly 1,000
        int block = 100_000 - handle;
        int[] top = new int[handle];
        for (int i = 0; i < handle; i++) {
            top[i] = -5_000 + i; // every node of the path is good
        }
        int peak = top[handle - 1];
        Integer[] values = new Integer[block];
        for (int i = 0; i < block; i++) {
            int depth = 31 - Integer.numberOfLeadingZeros(i + 1);
            // Left children sit below the peak (never good); the root and right children rise with depth (always good).
            values[i] = i % 2 == 1 ? peak - 1 - i % 100 : peak + depth;
        }
        TreeNode root = TreeNode.path(top, true, TreeNode.of(values));
        expect(handle + (block + 1) / 2, root);
    }
}
