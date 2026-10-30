/** Fixes the round brackets of a text by removing as few as possible. */
public class Expression {

    static String tidy(String text) {
        int n = text.length();
        boolean[] drop = new boolean[n];
        int[] open = new int[n]; // positions of the openers still waiting, latest on top
        int top = 0;
        for (int i = 0; i < n; i++) {
            char c = text.charAt(i);
            if (c == '(') {
                open[top++] = i;
            } else if (c == ')') {
                if (top == 0) {
                    drop[i] = true; // nothing to close
                } else {
                    top--; // paired with the nearest waiting opener
                }
            }
        }
        while (top > 0) {
            drop[open[--top]] = true; // never closed
        }
        StringBuilder kept = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            if (!drop[i]) {
                kept.append(text.charAt(i));
            }
        }
        return kept.toString();
    }
}
