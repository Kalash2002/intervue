import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/** Finds the heaviest path in a binary tree. */
public class MaxPathSum {

    /** Returns the largest sum of the values on any path of at least one node. */
    static int maxPathSum(TreeNode root) {
        Map<TreeNode, TreeNode> parent = new IdentityHashMap<>();
        List<TreeNode> nodes = new ArrayList<>();
        collect(root, null, parent, nodes);
        int best = Integer.MIN_VALUE;
        for (TreeNode start : nodes) {
            best = Math.max(best, walk(start, null, 0, parent)); // every path, from each of its two ends
        }
        return best;
    }

    private static void collect(TreeNode node, TreeNode up, Map<TreeNode, TreeNode> parent, List<TreeNode> nodes) {
        if (node == null) {
            return;
        }
        parent.put(node, up); // so a walk can go up as well as down
        nodes.add(node);
        collect(node.left, node, parent, nodes);
        collect(node.right, node, parent, nodes);
    }

    /** Returns the best sum of a path that has come to node from from with sum so far and goes on or ends. */
    private static int walk(TreeNode node, TreeNode from, int sum, Map<TreeNode, TreeNode> parent) {
        sum += node.val;
        int best = sum; // the path may end here
        for (TreeNode next : new TreeNode[] {node.left, node.right, parent.get(node)}) {
            if (next != null && next != from) { // never step back
                best = Math.max(best, walk(next, node, sum, parent));
            }
        }
        return best;
    }
}
