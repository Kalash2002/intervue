public class Main {
    public static void main(String[] args) {
        TreeNode root = TreeNode.of(2, 7, 5, 1, 6, null, 9, null, null, 3, 8);
        System.out.println(Diameter.diameter(root));
    }
}
