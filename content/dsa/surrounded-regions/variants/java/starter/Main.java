public class Main {
    public static void main(String[] args) {
        char[][] board = {"XXXXX".toCharArray(), "XOOXO".toCharArray(), "XXOXO".toCharArray(), "XOXXX".toCharArray()};
        Capture.surround(board);
        for (char[] row : board) {
            System.out.println(new String(row));
        }
    }
}
