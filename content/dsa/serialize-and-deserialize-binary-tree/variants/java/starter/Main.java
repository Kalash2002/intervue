public class Main {
    public static void main(String[] args) {
        TreeNode root = TreeNode.of(31, -6, 4, null, null, 120, -6);
        TreeCodec codec = new TreeCodec();
        String data = codec.serialize(root);
        System.out.println(data);
        System.out.println(TreeNode.levelOrder(codec.deserialize(data)));
    }
}
