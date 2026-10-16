/** Measures the longest path between two nodes of a binary tree. */
public class Diameter {

    /** Returns the number of edges on the longest path between any two nodes. */
    static int diameter(TreeNode root) {
        int[] best = {0};
        height(root, best);
        return best[0];
    }

    /** Returns the height of node in nodes, recording in best[0] the longest path that bends at it. */
    private static int height(TreeNode node, int[] best) {
        if (node == null) {
            return 0;
        }
        int left = height(node.left, best);
        int right = height(node.right, best);
        best[0] = Math.max(best[0], left + right); // edges: down left, down right, bending here
        return 1 + Math.max(left, right);
    }
}
