public class Main {
    public static void main(String[] args) {
        TreeNode root = TreeNode.of(9, 4, 6, 1, 5, null, 8);
        TreeNode sub = TreeNode.of(4, 1, 5);
        System.out.println(Subtree.isSubtree(root, sub));
    }
}
