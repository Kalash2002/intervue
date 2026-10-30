public class Main {
    public static void main(String[] args) {
        TreeNode root = TreeNode.of(7, 3, 11, null, 5, 9, 2, 8);
        System.out.println(LevelOrder.levels(root));
    }
}
