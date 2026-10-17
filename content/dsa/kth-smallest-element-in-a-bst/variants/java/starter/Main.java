public class Main {
    public static void main(String[] args) {
        TreeNode root = TreeNode.of(31, 12, 47, 5, 20, null, 60, null, 9);
        System.out.println(KthSmallest.kthSmallest(root, 3));
    }
}
