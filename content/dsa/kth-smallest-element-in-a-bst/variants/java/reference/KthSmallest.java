import java.util.ArrayDeque;

/** Reads a binary search tree in sorted order. */
public class KthSmallest {

    /** Returns the k-th smallest value in the tree, counting from 1. */
    static int kthSmallest(TreeNode root, int k) {
        ArrayDeque<TreeNode> stack = new ArrayDeque<>(); // the nodes whose left side is still being read
        TreeNode node = root;
        while (true) {
            while (node != null) {
                stack.push(node);
                node = node.left;
            }
            node = stack.pop(); // the smallest value not yet counted
            if (--k == 0) {
                return node.val; // stop here: the rest of the tree is never read
            }
            node = node.right;
        }
    }
}
