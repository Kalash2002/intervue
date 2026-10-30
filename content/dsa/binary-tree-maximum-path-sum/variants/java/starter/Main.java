public class Main {
    public static void main(String[] args) {
        TreeNode root = TreeNode.of(-4, 9, 6, -2, 7, null, -5, null, null, null, null, 8);
        System.out.println(MaxPathSum.maxPathSum(root));
    }
}
