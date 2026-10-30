import static kit.Check.assertEquals;
import static kit.Check.assertTrue;

import java.util.ArrayList;
import java.util.List;
import kit.Test;

public class Phase1Tests {

    /** Compares level by level, so a mismatch names the first level that differs. */
    private static void expect(List<List<Integer>> want, TreeNode root) {
        String tree = TreeNode.levelOrder(root);
        tree = tree.length() > 100 ? "the tree" : tree;
        List<List<Integer>> got = LevelOrder.levels(root);
        assertTrue(got != null, "return a list, empty for an empty tree, not null");
        for (int d = 0; d < Math.min(want.size(), got.size()); d++) {
            assertEquals(want.get(d), got.get(d), "level " + d + " of " + tree);
        }
        assertEquals(want.size(), got.size(), "number of levels of " + tree);
    }

    private static void expect(String want, Integer... levelOrder) {
        TreeNode root = TreeNode.of(levelOrder);
        List<List<Integer>> got = LevelOrder.levels(root);
        assertEquals(want, String.valueOf(got), "levels of " + TreeNode.levelOrder(root));
    }

    private static int value(int i) {
        return i % 20_001 - 10_000;
    }

    @Test(order = 1)
    void sample() {
        expect("[[7], [3, 11], [5, 9, 2], [8]]", 7, 3, 11, null, 5, 9, 2, 8);
    }

    @Test(order = 2)
    void zigzagPath() {
        expect("[[4], [-1], [6], [0]]", 4, -1, null, null, 6, 0);
    }

    @Test(order = 3)
    void emptyTree() {
        expect("[]");
    }

    @Test(order = 4)
    void singleNode() {
        expect("[[0]]", 0);
    }

    @Test(order = 5)
    void gapsInsideALevel() {
        expect("[[1], [2, 3], [4, 5], [6, 7]]", 1, 2, 3, null, 4, null, 5, 6, null, null, 7);
    }

    @Test(order = 6)
    void repeatedValues() {
        expect("[[5], [5, 5], [5, 5]]", 5, 5, 5, 5, null, null, 5);
    }

    @Test(order = 7)
    void valuesAtTheLimits() {
        expect("[[10000], [-10000, 10000], [-10000]]", 10_000, -10_000, 10_000, null, null, -10_000);
    }

    @Test(order = 8)
    void deepestAllowedPath() {
        int[] values = new int[1_000]; // depth at the limit: recursion must still fit
        List<List<Integer>> want = new ArrayList<>();
        for (int i = 0; i < values.length; i++) {
            values[i] = value(7 * i);
            want.add(List.of(values[i]));
        }
        expect(want, TreeNode.path(values, true, null));
    }

    @Test(order = 9, timeoutMs = 2000)
    void largeTreeWithALongTail() {
        int block = 99_017; // a complete tree of 17 levels with a path of 983 below: depth exactly 1,000
        int tail = 100_000 - block;
        Integer[] values = new Integer[block];
        for (int i = 0; i < block; i++) {
            values[i] = value(i);
        }
        TreeNode root = TreeNode.of(values);
        List<TreeNode> order = new ArrayList<>();
        order.add(root);
        for (int i = 0; i < order.size(); i++) {
            TreeNode n = order.get(i);
            if (n.left != null) order.add(n.left);
            if (n.right != null) order.add(n.right);
        }
        int[] path = new int[tail];
        for (int i = 0; i < tail; i++) {
            path[i] = value(3 * i + 5);
        }
        order.get(block - 1).left = TreeNode.path(path, true, null); // under the last node of the block
        List<List<Integer>> want = new ArrayList<>();
        for (int start = 0; start < block; start = 2 * start + 1) {
            List<Integer> level = new ArrayList<>();
            for (int i = start; i < Math.min(2 * start + 1, block); i++) {
                level.add(values[i]);
            }
            want.add(level);
        }
        for (int v : path) {
            want.add(List.of(v));
        }
        expect(want, root);
    }
}
