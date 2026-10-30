public class Main {
    public static void main(String[] args) {
        TreeNode root = TreeNode.of(10, 4, 15, null, 7, 12, 20, null, null, 11);
        System.out.println(MaxDepth.maxDepth(root));
    }
}
