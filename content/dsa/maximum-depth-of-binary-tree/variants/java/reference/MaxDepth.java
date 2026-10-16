import java.util.ArrayDeque;

/** Measures how deep a binary tree goes. */
public class MaxDepth {

    /** Returns the number of nodes on the longest path from the root down to a leaf; 0 when empty. */
    static int maxDepth(TreeNode root) {
        if (root == null) {
            return 0;
        }
        ArrayDeque<TreeNode> level = new ArrayDeque<>();
        level.add(root);
        int depth = 0;
        while (!level.isEmpty()) {
            depth++; // the queue holds exactly one whole level here
            for (int i = level.size(); i > 0; i--) {
                TreeNode n = level.poll();
                if (n.left != null) level.add(n.left);
                if (n.right != null) level.add(n.right);
            }
        }
        return depth;
    }
}
