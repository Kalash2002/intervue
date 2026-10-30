import static kit.Check.assertEquals;
import static kit.Check.assertTrue;

import java.util.ArrayList;
import java.util.List;
import kit.Test;

public class Phase1Tests {

    /** Inverts the tree and checks every node now holds its old children, swapped, and no new ones. */
    private static TreeNode invertAndCheckNodes(TreeNode root) {
        List<TreeNode> nodes = new ArrayList<>();
        if (root != null) {
            nodes.add(root);
        }
        for (int i = 0; i < nodes.size(); i++) {
            TreeNode n = nodes.get(i);
            if (n.left != null) nodes.add(n.left);
            if (n.right != null) nodes.add(n.right);
        }
        TreeNode[] oldLeft = new TreeNode[nodes.size()];
        TreeNode[] oldRight = new TreeNode[nodes.size()];
        for (int i = 0; i < nodes.size(); i++) {
            oldLeft[i] = nodes.get(i).left;
            oldRight[i] = nodes.get(i).right;
        }
        TreeNode got = InvertTree.invert(root);
        assertTrue(got == root, "return the same root node; the root stays the root of the mirror");
        for (int i = 0; i < nodes.size(); i++) {
            TreeNode n = nodes.get(i);
            assertTrue(n.left == oldRight[i] && n.right == oldLeft[i],
                    "the node holding " + n.val + " must have its own two children, swapped");
        }
        return got;
    }

    private static void expect(String mirrored, Integer... levelOrder) {
        TreeNode got = invertAndCheckNodes(TreeNode.of(levelOrder));
        assertEquals(mirrored, TreeNode.levelOrder(got));
    }

    @Test(order = 1)
    void sample() {
        expect("[6,9,3,null,8,4,1]", 6, 3, 9, 1, 4, 8);
    }

    @Test(order = 2)
    void leftLeaningBecomesRightLeaning() {
        expect("[5,null,2,null,7]", 5, 2, null, 7);
    }

    @Test(order = 3)
    void emptyTree() {
        assertTrue(InvertTree.invert(null) == null, "an empty tree mirrors to an empty tree (null)");
    }

    @Test(order = 4)
    void singleNode() {
        expect("[13]", 13);
    }

    @Test(order = 5)
    void fullThreeLevels() {
        expect("[0,-2,2,-1,-3,3,1]", 0, 2, -2, 1, 3, -3, -1);
    }

    @Test(order = 6)
    void repeatedValuesKeepTheirNodes() {
        expect("[4,4,4,null,null,null,4]", 4, 4, 4, 4);
    }

    @Test(order = 7)
    void valuesAtTheLimits() {
        expect("[-10000,null,10000,0]", -10_000, 10_000, null, null, 0);
    }

    @Test(order = 8)
    void deepestAllowedPath() {
        int[] values = new int[1_000]; // depth at the limit: recursion must still fit
        for (int i = 0; i < values.length; i++) {
            values[i] = i;
        }
        TreeNode root = TreeNode.path(values, true, null);
        TreeNode n = invertAndCheckNodes(root);
        for (int i = 0; i < values.length; i++, n = n.right) {
            assertEquals(i, n.val, "the left path turns into a right path, in the same order");
        }
    }

    @Test(order = 9, timeoutMs = 2000)
    void largeCompleteTree() {
        int n = 100_000;
        Integer[] values = new Integer[n];
        for (int i = 0; i < n; i++) {
            values[i] = i % 20_001 - 10_000;
        }
        invertAndCheckNodes(TreeNode.of(values));
    }
}
