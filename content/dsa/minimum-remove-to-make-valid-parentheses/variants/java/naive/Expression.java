/** Fixes the round brackets of a text by removing as few as possible. */
public class Expression {

    static String tidy(String text) {
        int n = text.length();
        boolean[] keep = new boolean[n];
        boolean[] paired = new boolean[n];
        for (int i = 0; i < n; i++) {
            if (text.charAt(i) != ')') {
                continue;
            }
            // look back for the nearest opener that is still unpaired
            int j = i - 1;
            while (j >= 0 && !(text.charAt(j) == '(' && !paired[j])) {
                j--;
            }
            if (j >= 0) {
                paired[j] = true;
                keep[j] = true;
                keep[i] = true;
            }
        }
        StringBuilder kept = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            char c = text.charAt(i);
            if ((c != '(' && c != ')') || keep[i]) {
                kept.append(c);
            }
        }
        return kept.toString();
    }
}
