/** Rebuilds a binary tree from two of its traversals. */
public class BuildTree {

    /** Returns the root of the tree whose pre-order is preorder and whose in-order is inorder. */
    static TreeNode buildTree(int[] preorder, int[] inorder) {
        return new Walk(preorder, inorder).build(Long.MAX_VALUE); // no stop value: the whole tree
    }

    /** One rebuild: both arrays are read once, left to right, by the two cursors. */
    private static final class Walk {
        private final int[] preorder;
        private final int[] inorder;
        private int pre; // next value in preorder: the root of the next subtree to build
        private int in; // next value in inorder not yet placed

        Walk(int[] preorder, int[] inorder) {
            this.preorder = preorder;
            this.inorder = inorder;
        }

        /** Builds the subtree whose in-order run ends just before the value stop. */
        TreeNode build(long stop) {
            if (pre == preorder.length || inorder[in] == stop) {
                return null; // in-order has reached the parent: this side is empty
            }
            TreeNode node = new TreeNode(preorder[pre++]);
            node.left = build(node.val); // the left run ends at the node itself
            in++; // in-order now reads the node
            node.right = build(stop); // the right run ends where this subtree's run ends
            return node;
        }
    }
}
