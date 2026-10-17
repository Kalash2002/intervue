public class Main {
    public static void main(String[] args) {
        KthLargest board = new KthLargest(2, new int[] {5, -1, 8});
        System.out.println(board.add(3) + " " + board.add(9) + " " + board.add(10) + " " + board.add(-4));
    }
}
