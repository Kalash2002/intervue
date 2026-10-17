public class Main {
    public static void main(String[] args) {
        char[][] board = {"plum".toCharArray(), "ears".toCharArray(), "ntok".toCharArray()};
        System.out.println(WordHunt.findAll(board, new String[] {"plum", "rot", "pear", "sort", "pan"}));
    }
}
