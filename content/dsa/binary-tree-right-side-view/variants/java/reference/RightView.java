import java.util.ArrayList;
import java.util.List;

/** Reads what can be seen of a binary tree from its right-hand side. */
public class RightView {

    /** Returns the rightmost value of each level, top level first. */
    static List<Integer> rightSideView(TreeNode root) {
        List<Integer> view = new ArrayList<>();
        visit(root, 0, view);
        return view;
    }

    private static void visit(TreeNode node, int depth, List<Integer> view) {
        if (node == null) {
            return;
        }
        if (depth == view.size()) {
            view.add(node.val); // the first node reached at a new depth is the rightmost one there
        }
        visit(node.right, depth + 1, view); // right side first
        visit(node.left, depth + 1, view);
    }
}
