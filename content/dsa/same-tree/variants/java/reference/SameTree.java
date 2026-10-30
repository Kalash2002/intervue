/** Compares two binary trees. */
public class SameTree {

    /** Returns true when both trees have the same shape and the same value at every node. */
    static boolean isSame(TreeNode p, TreeNode q) {
        if (p == null || q == null) {
            return p == q; // equal only when both are missing
        }
        return p.val == q.val && isSame(p.left, q.left) && isSame(p.right, q.right);
    }
}
