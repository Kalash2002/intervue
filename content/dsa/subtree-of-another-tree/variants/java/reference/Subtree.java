import java.util.ArrayList;
import java.util.List;

/** Looks for one binary tree inside another. */
public class Subtree {

    private static final int MISSING = Integer.MIN_VALUE; // outside the value range: an empty child

    /** Returns true when some node of root, with everything below it, is identical to sub. */
    static boolean isSubtree(TreeNode root, TreeNode sub) {
        int[] text = preorder(root);
        int[] pattern = preorder(sub);
        int[] border = new int[pattern.length]; // border[i]: longest proper prefix of pattern[0..i] that is also its suffix
        for (int i = 1, k = 0; i < pattern.length; i++) {
            while (k > 0 && pattern[i] != pattern[k]) k = border[k - 1];
            if (pattern[i] == pattern[k]) k++;
            border[i] = k;
        }
        for (int i = 0, k = 0; i < text.length; i++) {
            while (k > 0 && text[i] != pattern[k]) k = border[k - 1]; // fall back, never re-read text
            if (text[i] == pattern[k]) k++;
            if (k == pattern.length) return true;
        }
        return false;
    }

    /** Values in preorder with a marker for every empty child, so the shape is part of the sequence. */
    private static int[] preorder(TreeNode root) {
        List<TreeNode> stack = new ArrayList<>(); // a list, because empty children are pushed as null
        stack.add(root);
        List<Integer> out = new ArrayList<>();
        while (!stack.isEmpty()) {
            TreeNode n = stack.remove(stack.size() - 1);
            if (n == null) {
                out.add(MISSING);
                continue;
            }
            out.add(n.val);
            stack.add(n.right); // pushed first, so the left side comes out first
            stack.add(n.left);
        }
        return out.stream().mapToInt(Integer::intValue).toArray();
    }
}
