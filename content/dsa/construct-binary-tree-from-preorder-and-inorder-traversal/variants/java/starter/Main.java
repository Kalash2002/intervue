public class Main {
    public static void main(String[] args) {
        int[] preorder = {14, -3, 8, 40, 19, 27, 6};
        int[] inorder = {8, -3, 19, 40, 14, 27, 6};
        System.out.println(TreeNode.levelOrder(BuildTree.buildTree(preorder, inorder)));
    }
}
