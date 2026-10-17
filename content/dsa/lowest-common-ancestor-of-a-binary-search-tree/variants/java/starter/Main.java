public class Main {
    public static void main(String[] args) {
        TreeNode root = TreeNode.of(40, 20, 60, 10, 30, 50, 70, null, 15, 25);
        TreeNode p = root.left.left.right; // 15
        TreeNode q = root.left.right.left; // 25
        TreeNode got = CommonAncestor.lca(root, p, q);
        System.out.println(got == null ? "null" : got.val);
    }
}
