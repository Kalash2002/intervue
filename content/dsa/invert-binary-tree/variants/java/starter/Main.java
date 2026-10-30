public class Main {
    public static void main(String[] args) {
        TreeNode root = TreeNode.of(6, 3, 9, 1, 4, 8);
        System.out.println(TreeNode.levelOrder(InvertTree.invert(root)));
    }
}
