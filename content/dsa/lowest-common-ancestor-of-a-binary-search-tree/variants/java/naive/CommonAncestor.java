/** Finds the lowest common ancestor of two nodes in a binary search tree. */
public class CommonAncestor {

    /** Returns the lowest node that has both p and q in its subtree (a node is in its own subtree). */
    static TreeNode lca(TreeNode root, TreeNode p, TreeNode q) {
        // Ignores the ordering: searches both sides of every node, like in any binary tree.
        if (root == null || root == p || root == q) {
            return root;
        }
        TreeNode left = lca(root.left, p, q);
        TreeNode right = lca(root.right, p, q);
        if (left != null && right != null) {
            return root;
        }
        return left != null ? left : right;
    }
}
