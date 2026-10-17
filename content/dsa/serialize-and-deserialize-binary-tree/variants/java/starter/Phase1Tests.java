import static kit.Check.assertEquals;
import static kit.Check.assertTrue;

import kit.Test;

public class Phase1Tests {

    /** Serializes root, deserializes the text on a fresh codec and expects the same tree back. */
    private static String roundTrip(TreeNode root, int nodes) {
        String data = new TreeCodec().serialize(root);
        assertTrue(data != null, "serialize returned null");
        long limit = 20L * (nodes + 1);
        assertTrue(data.length() <= limit, "the text for " + nodes + " nodes is " + data.length() + " characters, over the limit of " + limit);
        TreeNode back = new TreeCodec().deserialize(data);
        String want = TreeNode.levelOrder(root);
        String got = TreeNode.levelOrder(back);
        if (want.length() > 200) {
            assertTrue(want.equals(got), "the " + nodes + "-node tree came back different");
        } else {
            assertEquals(want, got, "the tree must come back unchanged");
        }
        assertTrue(root == null || back != root, "deserialize must build new nodes, not return the original tree");
        return data;
    }

    /** A tree of the given size, each subtree split at its middle, values from a running counter. */
    private static TreeNode balanced(int size, int[] next) {
        if (size == 0) {
            return null;
        }
        int left = (size - 1) / 2;
        TreeNode l = balanced(left, next);
        int value = next[0]++ % 2_001 - 1_000; // repeats on purpose: values need not be distinct
        return new TreeNode(value, l, balanced(size - 1 - left, next));
    }

    @Test(order = 1)
    void sample() {
        roundTrip(TreeNode.of(31, -6, 4, null, null, 120, -6), 5);
    }

    @Test(order = 2)
    void emptyTree() {
        roundTrip(null, 0);
    }

    @Test(order = 3)
    void singleNode() {
        roundTrip(TreeNode.of(0), 1);
        roundTrip(TreeNode.of(-1_000_000_000), 1);
        roundTrip(TreeNode.of(1_000_000_000), 1);
    }

    @Test(order = 4)
    void sameValuesDifferentShapes() {
        String left = roundTrip(TreeNode.of(7, 7), 2);
        String right = roundTrip(TreeNode.of(7, null, 7), 2);
        assertTrue(!left.equals(right), "[7,7] and [7,null,7] are different trees but got the same text");
        roundTrip(TreeNode.of(7, 7, 7, null, 7, 7), 5);
    }

    @Test(order = 5)
    void negativeAndLongValues() {
        roundTrip(TreeNode.of(-1, -12, 123_456_789, null, -1_000_000_000, 1_000_000_000, -999_999_999, 10), 7);
    }

    @Test(order = 6)
    void unevenShapes() {
        roundTrip(TreeNode.of(2, null, 3, 4, null, null, 5, 6), 5);
        roundTrip(TreeNode.of(8, 1, 9, 0, 2, null, 10, null, null, null, 3), 7);
    }

    @Test(order = 7)
    void sizeStaysLinear() {
        int[] values = new int[18];
        for (int i = 0; i < values.length; i++) {
            values[i] = 40 - 3 * i;
        }
        roundTrip(TreeNode.path(values, false, null), 18); // a numbered-slot format would need 2^18 slots
    }

    @Test(order = 8)
    void deepestAllowedPaths() {
        int[] values = new int[1_000]; // depth at the limit: recursion must still fit
        for (int i = 0; i < values.length; i++) {
            values[i] = 1_000_000_000 - 1_999_999 * i;
        }
        roundTrip(TreeNode.path(values, true, null), 1_000);
        roundTrip(TreeNode.path(values, false, null), 1_000);
        TreeNode zigzag = null;
        for (int i = values.length - 1; i >= 0; i--) {
            zigzag = i % 2 == 0 ? new TreeNode(values[i], zigzag, null) : new TreeNode(values[i], null, zigzag);
        }
        roundTrip(zigzag, 1_000);
    }

    @Test(order = 9, timeoutMs = 2000)
    void largeTree() {
        int handle = 983; // a path on top of a balanced block: depth exactly 1,000
        int[] top = new int[handle];
        for (int i = 0; i < handle; i++) {
            top[i] = i % 3 == 0 ? -1_000_000_000 + i : 1_000_000_000 - i;
        }
        roundTrip(TreeNode.path(top, true, balanced(100_000 - handle, new int[1])), 100_000);
    }
}
