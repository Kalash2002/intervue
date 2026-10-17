public class Main {
    public static void main(String[] args) {
        TreeNode root = TreeNode.of(12, 6, 15, 4, 8, null, 20, null, null, 7);
        System.out.println(RightView.rightSideView(root));
    }
}
