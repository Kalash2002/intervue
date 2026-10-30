import static kit.Check.assertEquals;
import static kit.Check.assertTrue;

import java.util.Arrays;
import kit.Test;

public class Phase1Tests {

    private static void expect(String want, int[] preorder, int[] inorder) {
        String args = preorder.length > 12 ? "the " + preorder.length + "-node input" : "preorder " + Arrays.toString(preorder) + ", inorder " + Arrays.toString(inorder);
        String got = TreeNode.levelOrder(BuildTree.buildTree(preorder, inorder));
        if (want.length() > 200) {
            assertTrue(want.equals(got), "the tree rebuilt from " + args + " differs from the original");
        } else {
            assertEquals(want, got, "tree rebuilt from " + args);
        }
    }

    /** Hands the two traversals of tree to buildTree and expects tree back. */
    private static void rebuild(TreeNode tree) {
        int n = size(tree);
        int[] preorder = new int[n];
        int[] inorder = new int[n];
        fill(tree, preorder, new int[1], inorder, new int[1]);
        expect(TreeNode.levelOrder(tree), preorder, inorder);
    }

    private static int size(TreeNode node) {
        return node == null ? 0 : 1 + size(node.left) + size(node.right);
    }

    private static void fill(TreeNode node, int[] pre, int[] preAt, int[] in, int[] inAt) {
        if (node == null) {
            return;
        }
        pre[preAt[0]++] = node.val;
        fill(node.left, pre, preAt, in, inAt);
        in[inAt[0]++] = node.val;
        fill(node.right, pre, preAt, in, inAt);
    }

    /** A tree holding values[lo..hi] in in-order, each subtree split at its middle. */
    private static TreeNode balanced(int[] values, int lo, int hi) {
        if (lo > hi) {
            return null;
        }
        int mid = (lo + hi) >>> 1;
        return new TreeNode(values[mid], balanced(values, lo, mid - 1), balanced(values, mid + 1, hi));
    }

    @Test(order = 1)
    void sample() {
        expect("[14,-3,27,8,40,null,6,null,null,19]", new int[] {14, -3, 8, 40, 19, 27, 6}, new int[] {8, -3, 19, 40, 14, 27, 6});
    }

    @Test(order = 2)
    void zigzag() {
        expect("[5,null,9,2]", new int[] {5, 9, 2}, new int[] {5, 2, 9});
    }

    @Test(order = 3)
    void leftPath() {
        expect("[4,3,null,2,null,1]", new int[] {4, 3, 2, 1}, new int[] {1, 2, 3, 4});
    }

    @Test(order = 4)
    void singleNode() {
        expect("[-1000000000]", new int[] {-1_000_000_000}, new int[] {-1_000_000_000});
        expect("[1000000000]", new int[] {1_000_000_000}, new int[] {1_000_000_000});
    }

    @Test(order = 5)
    void rightPathWithExtremeValues() {
        int[] both = {1_000_000_000, -1_000_000_000, 0, 999_999_999};
        expect("[1000000000,null,-1000000000,null,0,null,999999999]", both, both.clone());
    }

    @Test(order = 6)
    void unevenSubtrees() {
        rebuild(TreeNode.of(50, 20, 80, 10, 30, null, 90, 5, null, 25, 35, 85));
        rebuild(TreeNode.of(7, 3, null, 1, 6, null, 2, 4, null, null, null, null, 5));
        rebuild(TreeNode.of(-6, null, 11, 13, 12, null, 0, 15, null, -2));
        rebuild(TreeNode.of(60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74));
    }

    @Test(order = 7)
    void twoCallsInARow() {
        expect("[14,-3,27,8,40,null,6,null,null,19]", new int[] {14, -3, 8, 40, 19, 27, 6}, new int[] {8, -3, 19, 40, 14, 27, 6});
        expect("[5,null,9,2]", new int[] {5, 9, 2}, new int[] {5, 2, 9});
        expect("[-1000000000]", new int[] {-1_000_000_000}, new int[] {-1_000_000_000});
    }

    @Test(order = 8)
    void deepestAllowedPaths() {
        int[] values = new int[1_000]; // depth at the limit: recursion must still fit
        for (int i = 0; i < values.length; i++) {
            values[i] = 7 * i - 3_000;
        }
        rebuild(TreeNode.path(values, true, null));
        rebuild(TreeNode.path(values, false, null));
        TreeNode zigzag = null;
        for (int i = values.length - 1; i >= 0; i--) {
            zigzag = i % 2 == 0 ? new TreeNode(values[i], zigzag, null) : new TreeNode(values[i], null, zigzag);
        }
        rebuild(zigzag);
    }

    @Test(order = 9, timeoutMs = 1000)
    void largeTree() {
        int handle = 983; // a path on top of a balanced block: depth exactly 1,000
        int[] block = new int[100_000 - handle];
        for (int i = 0; i < block.length; i++) {
            block[i] = 5 * i - 250_000;
        }
        int[] top = new int[handle];
        for (int i = 0; i < handle; i++) {
            top[i] = 900_000_000 - 3 * i; // each one the left child of the one above
        }
        rebuild(TreeNode.path(top, true, balanced(block, 0, block.length - 1)));
    }
}
