import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

/** Reads a binary tree level by level. */
public class LevelOrder {

    /** Returns the values of each level, top level first, each level from left to right. */
    static List<List<Integer>> levels(TreeNode root) {
        List<List<Integer>> out = new ArrayList<>();
        ArrayDeque<TreeNode> queue = new ArrayDeque<>();
        if (root != null) {
            queue.add(root);
        }
        while (!queue.isEmpty()) {
            int size = queue.size(); // exactly the nodes of the current level
            List<Integer> level = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                TreeNode n = queue.poll();
                level.add(n.val);
                if (n.left != null) queue.add(n.left);
                if (n.right != null) queue.add(n.right);
            }
            out.add(level);
        }
        return out;
    }
}
