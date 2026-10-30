public class Main {
    public static void main(String[] args) {
        String[] rows = {
            "1..4..7..",
            "..6..9..3",
            ".8..2..5.",
            "2..5..8..",
            "..7..1..4",
            ".9..3..6.",
            "3..6..9..",
            "..8..2..5",
            ".1..4..7."
        };
        System.out.println(Sudoku.isValid(rows));
    }
}
