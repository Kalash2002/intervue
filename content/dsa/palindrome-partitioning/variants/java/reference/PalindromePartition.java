import java.util.ArrayList;
import java.util.List;

/** Lists every way to cut a string into palindromic pieces. */
public class PalindromePartition {

    /** Returns each way to cut s into palindromes once, as its pieces from left to right. */
    static List<List<String>> all(String s) {
        int n = s.length();
        // pal[i][j]: s[i..j] is a palindrome; filled from the right so pal[i + 1][j - 1] is ready
        boolean[][] pal = new boolean[n][n];
        for (int i = n - 1; i >= 0; i--) {
            for (int j = i; j < n; j++) {
                pal[i][j] = s.charAt(i) == s.charAt(j) && (j - i < 2 || pal[i + 1][j - 1]);
            }
        }
        List<List<String>> result = new ArrayList<>();
        cut(s, pal, 0, new ArrayList<>(), result);
        return result;
    }

    // Chooses the next piece, which starts at start; pieces holds the pieces chosen so far.
    private static void cut(String s, boolean[][] pal, int start, List<String> pieces, List<List<String>> result) {
        if (start == s.length()) {
            result.add(new ArrayList<>(pieces)); // a copy: pieces keeps changing after this
            return;
        }
        for (int end = start; end < s.length(); end++) {
            if (pal[start][end]) {
                pieces.add(s.substring(start, end + 1));
                cut(s, pal, end + 1, pieces, result);
                pieces.remove(pieces.size() - 1);
            }
        }
    }
}
