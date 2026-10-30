public class Main {
    public static void main(String[] args) {
        TreeNode root = TreeNode.of(5, 3, 8, 6, 1, 7, 9, null, 6);
        System.out.println(GoodNodes.goodNodes(root));
    }
}
