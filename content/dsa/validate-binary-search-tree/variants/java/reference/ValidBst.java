/** Checks the ordering rule of a binary search tree. */
public class ValidBst {

    /** Returns true when every node is larger than all values to its left and smaller than all to its right. */
    static boolean isValidBST(TreeNode root) {
        return within(root, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    /** True when every value in node's subtree lies strictly between low and high. */
    private static boolean within(TreeNode node, long low, long high) {
        if (node == null) {
            return true;
        }
        // long bounds: no int value can equal them, so Integer.MIN_VALUE and MAX_VALUE stay legal
        if (node.val <= low || node.val >= high) {
            return false;
        }
        return within(node.left, low, node.val) && within(node.right, node.val, high);
    }
}
