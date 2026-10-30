import static kit.Check.assertTrue;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import kit.Test;

public class Phase1Tests {

    /** The node holding value, found by visiting every node. */
    private static TreeNode node(TreeNode root, int value) {
        ArrayDeque<TreeNode> todo = new ArrayDeque<>();
        todo.add(root);
        while (!todo.isEmpty()) {
            TreeNode n = todo.poll();
            if (n.val == value) {
                return n;
            }
            if (n.left != null) todo.add(n.left);
            if (n.right != null) todo.add(n.right);
        }
        throw new IllegalArgumentException("no node holds " + value);
    }

    /** True when target is in the subtree of from, searching by value as the tree is ordered. */
    private static boolean reaches(TreeNode from, TreeNode target) {
        TreeNode n = from;
        while (n != null && n != target) {
            n = target.val < n.val ? n.left : n.right;
        }
        return n == target;
    }

    /** Asks for the ancestor of p and q both ways round; it must be the very node holding want. */
    private static void expect(TreeNode root, int p, int q, int want) {
        TreeNode pn = node(root, p);
        TreeNode qn = node(root, q);
        TreeNode wantNode = node(root, want);
        String tree = TreeNode.levelOrder(root);
        tree = tree.length() > 100 ? "the tree" : tree;
        for (TreeNode[] args : new TreeNode[][] {{pn, qn}, {qn, pn}}) {
            TreeNode got = CommonAncestor.lca(root, args[0], args[1]);
            assertTrue(got == wantNode, "p = " + args[0].val + ", q = " + args[1].val + " in " + tree
                    + ": expected the node holding " + want + ", got "
                    + (got == null ? "null" : "a node holding " + got.val));
        }
    }

    private static void expect(Integer[] levelOrder, int p, int q, int want) {
        expect(TreeNode.of(levelOrder), p, q, want);
    }

    private static Integer[] tree(Integer... levelOrder) {
        return levelOrder;
    }

    /** A search tree holding values[lo..hi], each subtree split at its middle. */
    private static TreeNode balanced(int[] values, int lo, int hi) {
        if (lo > hi) {
            return null;
        }
        int mid = (lo + hi) >>> 1;
        return new TreeNode(values[mid], balanced(values, lo, mid - 1), balanced(values, mid + 1, hi));
    }

    @Test(order = 1)
    void sample() {
        expect(tree(40, 20, 60, 10, 30, 50, 70, null, 15, 25), 15, 25, 20);
    }

    @Test(order = 2)
    void oneIsAboveTheOther() {
        expect(tree(40, 20, 60, 10, 30, 50, 70, null, 15, 25), 30, 25, 30);
        expect(tree(40, 20, 60, 10, 30, 50, 70, null, 15, 25), 10, 15, 10);
    }

    @Test(order = 3)
    void splitAtTheRoot() {
        expect(tree(40, 20, 60, 10, 30, 50, 70, null, 15, 25), 15, 50, 40);
    }

    @Test(order = 4)
    void rootIsOneOfThem() {
        expect(tree(40, 20, 60, 10, 30, 50, 70, null, 15, 25), 40, 70, 40);
        expect(tree(40, 20, 60, 10, 30, 50, 70, null, 15, 25), 25, 40, 40);
    }

    @Test(order = 5)
    void twoNodes() {
        expect(tree(-6, null, 9), 9, -6, -6);
    }

    @Test(order = 6)
    void valuesFarApart() {
        Integer[] wide = tree(0, -65_536, 1_000_000_000, -131_072, null, 65_536, null, null, null, null, 196_608);
        expect(wide, -131_072, -65_536, -65_536);
        expect(wide, 65_536, 196_608, 65_536);
        expect(wide, 196_608, 1_000_000_000, 1_000_000_000);
        expect(wide, -131_072, 196_608, 0);
        Integer[] ends = tree(-1_000_000_000, null, 1_000_000_000, 0, null, null, 999_999_999);
        expect(ends, 0, 999_999_999, 0);
        expect(ends, -1_000_000_000, 999_999_999, -1_000_000_000);
    }

    @Test(order = 7)
    void deepestAllowedPath() {
        int[] values = new int[1_000]; // depth at the limit: recursion must still fit
        for (int i = 0; i < values.length; i++) {
            values[i] = 3 * i - 1_500;
        }
        TreeNode root = TreeNode.path(values, false, null); // increasing to the right: a search tree
        expect(root, values[997], values[999], values[997]);
        expect(root, values[0], values[999], values[0]);
    }

    @Test(order = 8, timeoutMs = 700)
    void manyPairsInALargeTree() {
        int[] values = new int[100_000];
        for (int i = 0; i < values.length; i++) {
            values[i] = 20_000 * i - 1_000_000_000;
        }
        TreeNode root = balanced(values, 0, values.length - 1); // 17 levels: each walk is short
        List<TreeNode> nodes = new ArrayList<>();
        nodes.add(root);
        for (int i = 0; i < nodes.size(); i++) {
            TreeNode n = nodes.get(i);
            if (n.left != null) nodes.add(n.left);
            if (n.right != null) nodes.add(n.right);
        }
        Random random = new Random(51);
        for (int k = 0; k < 20_000; k++) {
            TreeNode p = nodes.get(random.nextInt(nodes.size()));
            TreeNode q = nodes.get(random.nextInt(nodes.size()));
            if (p == q) {
                continue;
            }
            TreeNode got = CommonAncestor.lca(root, p, q);
            // The one common ancestor whose value lies between p and q is the lowest one.
            boolean between = got != null && Math.min(p.val, q.val) <= got.val && got.val <= Math.max(p.val, q.val);
            assertTrue(between && reaches(got, p) && reaches(got, q),
                    "p = " + p.val + ", q = " + q.val + ": got " + (got == null ? "null" : "a node holding " + got.val));
        }
    }
}
