public class Main {
    public static void main(String[] args) {
        for (java.util.List<String> board : NQueens.solve(6)) {
            System.out.println(board);
        }
    }
}
