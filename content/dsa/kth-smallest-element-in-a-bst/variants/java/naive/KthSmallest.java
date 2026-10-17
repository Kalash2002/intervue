import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Reads a binary search tree in sorted order. */
public class KthSmallest {

    /** Returns the k-th smallest value in the tree, counting from 1. */
    static int kthSmallest(TreeNode root, int k) {
        List<Integer> values = new ArrayList<>();
        ArrayDeque<TreeNode> todo = new ArrayDeque<>();
        todo.push(root);
        while (!todo.isEmpty()) { // every node, in any order
            TreeNode n = todo.pop();
            values.add(n.val);
            if (n.left != null) todo.push(n.left);
            if (n.right != null) todo.push(n.right);
        }
        Collections.sort(values);
        return values.get(k - 1);
    }
}
