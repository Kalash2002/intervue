public class Main {
    public static void main(String[] args) {
        TreeNode p = TreeNode.of(7, 2, 9, null, 4);
        TreeNode q = TreeNode.of(7, 2, 9, 4);
        System.out.println(SameTree.isSame(p, q));
    }
}
