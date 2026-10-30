/** Counts the nodes that no node above them outranks. */
public class GoodNodes {

    /** Returns how many nodes hold a value at least as large as every value on the path above them. */
    static int goodNodes(TreeNode root) {
        return count(root, root.val); // the root has nothing above it, so it is good
    }

    /** Counts the good nodes in node's subtree, given the largest value on the path above it. */
    private static int count(TreeNode node, int maxAbove) {
        if (node == null) {
            return 0;
        }
        int good = node.val >= maxAbove ? 1 : 0; // a tie still counts
        int max = Math.max(maxAbove, node.val);
        return good + count(node.left, max) + count(node.right, max);
    }
}
