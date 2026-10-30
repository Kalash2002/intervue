public class Main {
    public static void main(String[] args) {
        TreeNode root = TreeNode.of(8, 4, 12, 2, 6, null, 14, 1);
        System.out.println(Balanced.isBalanced(root));
    }
}
