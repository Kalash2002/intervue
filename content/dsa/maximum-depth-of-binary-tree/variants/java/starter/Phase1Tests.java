import static kit.Check.assertEquals;

import kit.Test;

public class Phase1Tests {

    private static void expect(int want, Integer... levelOrder) {
        TreeNode root = TreeNode.of(levelOrder);
        assertEquals(want, MaxDepth.maxDepth(root), "depth of " + TreeNode.levelOrder(root));
    }

    @Test(order = 1)
    void sample() {
        expect(4, 10, 4, 15, null, 7, 12, 20, null, null, 11);
    }

    @Test(order = 2)
    void rightOnlyPath() {
        expect(3, -3, null, 8, null, 2);
    }

    @Test(order = 3)
    void emptyTree() {
        assertEquals(0, MaxDepth.maxDepth(null), "an empty tree has depth 0");
    }

    @Test(order = 4)
    void singleNodeCountsAsOne() {
        expect(1, 9);
    }

    @Test(order = 5)
    void deepestLeafOnTheLeft() {
        expect(5, 1, 2, 3, 4, null, null, 5, 6, null, null, null, 7);
    }

    @Test(order = 6)
    void valuesAtTheLimits() {
        expect(2, 10_000, -10_000, 10_000);
    }

    @Test(order = 7, timeoutMs = 2000)
    void largeCompleteTree() {
        int n = 100_000; // 2^16 <= n < 2^17, so 17 levels
        Integer[] values = new Integer[n];
        for (int i = 0; i < n; i++) {
            values[i] = i % 20_001 - 10_000;
        }
        assertEquals(17, MaxDepth.maxDepth(TreeNode.of(values)));
    }

    @Test(order = 8, timeoutMs = 2000)
    void longPathOfAllTheNodes() {
        int n = 100_000; // one node per level: one call per level overflows the call stack
        int[] values = new int[n];
        for (int i = 0; i < n; i++) {
            values[i] = i % 2 == 0 ? i % 10_001 : -(i % 10_001);
        }
        TreeNode root = TreeNode.path(values, true, null);
        assertEquals(n, MaxDepth.maxDepth(root));
    }
}
