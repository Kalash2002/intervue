/** Measures how deep a binary tree goes. */
public class MaxDepth {

    /** Returns the number of nodes on the longest path from the root down to a leaf; 0 when empty. */
    static int maxDepth(TreeNode root) {
        if (root == null) {
            return 0;
        }
        return 1 + Math.max(maxDepth(root.left), maxDepth(root.right)); // one level more than the deeper side
    }
}
