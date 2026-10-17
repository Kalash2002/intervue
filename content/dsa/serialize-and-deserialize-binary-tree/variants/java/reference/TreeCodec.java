/** Turns a binary tree into a string and back. Keep no state between calls. */
public class TreeCodec {

    /** Returns a string from which deserialize can rebuild the tree; root may be null. */
    String serialize(TreeNode root) {
        StringBuilder out = new StringBuilder();
        write(root, out);
        return out.toString();
    }

    /** Pre-order, with # for every missing child, so the shape is in the text. */
    private static void write(TreeNode node, StringBuilder out) {
        if (node == null) {
            out.append("#,");
            return;
        }
        out.append(node.val).append(',');
        write(node.left, out);
        write(node.right, out);
    }

    /** Rebuilds the tree that serialize was given, with new nodes. */
    TreeNode deserialize(String data) {
        return read(data.split(","), new int[1]);
    }

    /** Reads one subtree from tokens, starting at next[0], in the order write wrote it. */
    private static TreeNode read(String[] tokens, int[] next) {
        String token = tokens[next[0]++];
        if (token.equals("#")) {
            return null; // a missing child ends this side
        }
        TreeNode node = new TreeNode(Integer.parseInt(token));
        node.left = read(tokens, next);
        node.right = read(tokens, next);
        return node;
    }
}
