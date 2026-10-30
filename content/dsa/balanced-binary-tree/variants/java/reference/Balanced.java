/** Checks whether a binary tree is height-balanced. */
public class Balanced {

    /** Returns true when, at every node, the two subtree heights differ by at most one. */
    static boolean isBalanced(TreeNode root) {
        return height(root) >= 0;
    }

    /** Returns the height of node in nodes, or -1 as soon as any node below it is out of balance. */
    private static int height(TreeNode node) {
        if (node == null) {
            return 0;
        }
        int left = height(node.left);
        if (left < 0) {
            return -1; // already out of balance: skip the right side
        }
        int right = height(node.right);
        if (right < 0 || Math.abs(left - right) > 1) {
            return -1;
        }
        return 1 + Math.max(left, right);
    }
}
