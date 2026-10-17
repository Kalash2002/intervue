public class Main {
    public static void main(String[] args) {
        TreeNode root = TreeNode.of(9, 4, 6, null, 7, 2, 8);
        TreeNode sub = TreeNode.of(6, 2, 8);
        System.out.println(Subtree.isSubtree(root, sub));
    }
}
