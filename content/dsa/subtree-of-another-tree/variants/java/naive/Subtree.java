import java.util.ArrayDeque;

/** Looks for one binary tree inside another. */
public class Subtree {

    /** Returns true when some node of root, with everything below it, is identical to sub. */
    static boolean isSubtree(TreeNode root, TreeNode sub) {
        ArrayDeque<TreeNode> todo = new ArrayDeque<>();
        todo.push(root);
        while (!todo.isEmpty()) {
            TreeNode node = todo.pop();
            if (same(node, sub)) {
                return true;
            }
            if (node.left != null) todo.push(node.left);
            if (node.right != null) todo.push(node.right);
        }
        return false;
    }

    private static boolean same(TreeNode a, TreeNode b) {
        ArrayDeque<TreeNode[]> pairs = new ArrayDeque<>(); // a stack instead of recursion: trees can be long paths
        pairs.push(new TreeNode[] {a, b});
        while (!pairs.isEmpty()) {
            TreeNode[] p = pairs.pop();
            if (p[0] == null || p[1] == null) {
                if (p[0] != p[1]) return false;
                continue;
            }
            if (p[0].val != p[1].val) return false;
            pairs.push(new TreeNode[] {p[0].right, p[1].right});
            pairs.push(new TreeNode[] {p[0].left, p[1].left});
        }
        return true;
    }
}
