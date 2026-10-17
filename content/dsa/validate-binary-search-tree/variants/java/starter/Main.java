public class Main {
    public static void main(String[] args) {
        TreeNode root = TreeNode.of(41, 17, 63, 8, 29, 50, 77);
        System.out.println(ValidBst.isValidBST(root));
    }
}
