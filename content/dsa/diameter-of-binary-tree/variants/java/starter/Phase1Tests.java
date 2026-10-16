import static kit.Check.assertEquals;

import kit.Test;

public class Phase1Tests {

    private static void expect(int want, Integer... levelOrder) {
        assertEquals(want, Diameter.diameter(TreeNode.of(levelOrder)), "diameter of " + TreeNode.levelOrder(TreeNode.of(levelOrder)));
    }

    /** A complete tree of n nodes, values spread over -10^4 .. 10^4. */
    private static TreeNode complete(int n) {
        Integer[] values = new Integer[n];
        for (int i = 0; i < n; i++) {
            values[i] = i % 200 - 100;
        }
        return TreeNode.of(values);
    }

    @Test(order = 1)
    void sample() {
        expect(5, 2, 7, 5, 1, 6, null, 9, null, null, 3, 8);
    }

    @Test(order = 2)
    void longestPathMissesTheRoot() {
        expect(4, 1, 8, null, 4, 3, 6, null, null, 2);
    }

    @Test(order = 3)
    void singleNode() {
        expect(0, 11);
    }

    @Test(order = 4)
    void twoNodes() {
        expect(1, 4, -6);
    }

    @Test(order = 5)
    void valuesAtTheLimits() {
        expect(3, -10_000, 10_000, -10_000, null, 10_000);
    }

    @Test(order = 6)
    void pathShapedTree() {
        TreeNode root = TreeNode.path(new int[] {5, 0, -5, 0, 5, 0}, false, null);
        assertEquals(5, Diameter.diameter(root), "six nodes in a line have five edges");
    }

    @Test(order = 7)
    void deepestAllowedPath() {
        int[] values = new int[1_000]; // depth at the limit: recursion must still fit
        TreeNode root = TreeNode.path(values, true, null);
        assertEquals(999, Diameter.diameter(root));
    }

    @Test(order = 8, timeoutMs = 2000)
    void longHandleOverABigBlock() {
        int block = 98_017; // complete, 17 levels deep
        int[] handle = new int[983]; // 983 + 17 = 1,000 levels, the depth limit
        for (int i = 0; i < handle.length; i++) {
            handle[i] = i % 7 - 3;
        }
        TreeNode root = TreeNode.path(handle, true, complete(block));
        assertEquals(983 + 17 - 1, Diameter.diameter(root), "from the top of the handle to the deepest leaf");
    }
}
