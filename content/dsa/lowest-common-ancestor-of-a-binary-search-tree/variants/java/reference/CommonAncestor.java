/** Finds the lowest common ancestor of two nodes in a binary search tree. */
public class CommonAncestor {

    /** Returns the lowest node that has both p and q in its subtree (a node is in its own subtree). */
    static TreeNode lca(TreeNode root, TreeNode p, TreeNode q) {
        TreeNode node = root;
        while (true) {
            if (p.val < node.val && q.val < node.val) {
                node = node.left; // both smaller: both live in the left subtree
            } else if (p.val > node.val && q.val > node.val) {
                node = node.right;
            } else {
                return node; // they split here, or node is one of them
            }
        }
    }
}
