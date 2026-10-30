/** Mirrors a binary tree in place. */
public class InvertTree {

    /** Swaps the children of every node and returns the root, now of the mirrored tree. */
    static TreeNode invert(TreeNode root) {
        if (root == null) {
            return null;
        }
        TreeNode left = root.left; // kept: the next line overwrites it
        root.left = invert(root.right);
        root.right = invert(left);
        return root;
    }
}
