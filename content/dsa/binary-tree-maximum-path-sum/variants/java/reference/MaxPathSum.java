/** Finds the heaviest path in a binary tree. */
public class MaxPathSum {

    /** Returns the largest sum of the values on any path of at least one node. */
    static int maxPathSum(TreeNode root) {
        int[] best = {Integer.MIN_VALUE}; // not 0: every value may be negative
        down(root, best);
        return best[0];
    }

    /** Returns the best sum of a path that starts at node and goes down; records paths that turn at node. */
    private static int down(TreeNode node, int[] best) {
        if (node == null) {
            return 0;
        }
        int left = Math.max(0, down(node.left, best)); // a branch that loses is left out
        int right = Math.max(0, down(node.right, best));
        best[0] = Math.max(best[0], node.val + left + right); // the path that turns here uses both sides
        return node.val + Math.max(left, right); // the parent can extend only one side
    }
}
