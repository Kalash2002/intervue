import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

/** A node of a binary tree. Read-only: the tests build and read trees with the helpers. */
public class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode() {}

    TreeNode(int val) {
        this.val = val;
    }

    TreeNode(int val, TreeNode left, TreeNode right) {
        this.val = val;
        this.left = left;
        this.right = right;
    }

    /** Builds a tree from level order, null marking a missing child; no values gives null. */
    static TreeNode of(Integer... levelOrder) {
        if (levelOrder.length == 0 || levelOrder[0] == null) {
            return null;
        }
        TreeNode root = new TreeNode(levelOrder[0]);
        ArrayDeque<TreeNode> parents = new ArrayDeque<>();
        parents.add(root);
        int i = 1;
        while (i < levelOrder.length) {
            TreeNode parent = parents.poll();
            for (int side = 0; side < 2 && i < levelOrder.length; side++, i++) {
                if (levelOrder[i] != null) {
                    TreeNode child = new TreeNode(levelOrder[i]);
                    if (side == 0) {
                        parent.left = child;
                    } else {
                        parent.right = child;
                    }
                    parents.add(child);
                }
            }
        }
        return root;
    }

    /**
     * Builds a path of nodes holding the values from the top down, each the left child (or the
     * right child, when left is false) of the one above; the last one gets bottom as that child.
     */
    static TreeNode path(int[] values, boolean left, TreeNode bottom) {
        TreeNode below = bottom;
        for (int i = values.length - 1; i >= 0; i--) {
            below = left ? new TreeNode(values[i], below, null) : new TreeNode(values[i], null, below);
        }
        return below;
    }

    /** Writes the tree in level order, as "[6,9,3,null,8]"; fails on a tree that links back on itself. */
    static String levelOrder(TreeNode root) {
        List<TreeNode> order = new ArrayList<>();
        order.add(root);
        for (int i = 0; i < order.size(); i++) {
            TreeNode n = order.get(i);
            if (n != null) {
                if (order.size() > 2_000_000) { // more than any test tree holds: it must loop
                    throw new IllegalStateException("the tree does not end: some node links back");
                }
                order.add(n.left);
                order.add(n.right);
            }
        }
        int end = order.size();
        while (end > 0 && order.get(end - 1) == null) {
            end--; // trailing missing children are left out
        }
        StringBuilder out = new StringBuilder("[");
        for (int i = 0; i < end; i++) {
            out.append(i > 0 ? "," : "").append(order.get(i) == null ? "null" : order.get(i).val);
        }
        return out.append(']').toString();
    }
}
