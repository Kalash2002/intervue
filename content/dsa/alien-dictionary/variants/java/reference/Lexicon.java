/** Recovers the alphabet of a language from a sorted word list. */
public class Lexicon {

    /**
     * Returns every letter used in words exactly once, in an order under which words is sorted, or
     * "" when no such order exists.
     */
    static String letterOrder(String[] words) {
        boolean[] used = new boolean[26];
        int letters = 0;
        for (String word : words) {
            for (int i = 0; i < word.length(); i++) {
                if (!used[word.charAt(i) - 'a']) {
                    used[word.charAt(i) - 'a'] = true;
                    letters++;
                }
            }
        }
        boolean[][] before = new boolean[26][26]; // before[x][y]: letter x must come before letter y
        int[] waiting = new int[26]; // rules not yet met before each letter
        for (int w = 0; w + 1 < words.length; w++) {
            String first = words[w];
            String second = words[w + 1];
            int shorter = Math.min(first.length(), second.length());
            int k = 0;
            while (k < shorter && first.charAt(k) == second.charAt(k)) {
                k++;
            }
            if (k == shorter) {
                if (first.length() > second.length()) {
                    return ""; // a word before its own proper prefix
                }
                continue; // equal, or a prefix first: no rule
            }
            int x = first.charAt(k) - 'a'; // only the first difference says anything
            int y = second.charAt(k) - 'a';
            if (!before[x][y]) { // count each rule once, however often it repeats
                before[x][y] = true;
                waiting[y]++;
            }
        }
        int[] queue = new int[26];
        int head = 0;
        int tail = 0;
        for (int x = 0; x < 26; x++) {
            if (used[x] && waiting[x] == 0) {
                queue[tail++] = x;
            }
        }
        StringBuilder order = new StringBuilder();
        while (head < tail) {
            int x = queue[head++];
            order.append((char) ('a' + x));
            for (int y = 0; y < 26; y++) {
                if (before[x][y] && --waiting[y] == 0) {
                    queue[tail++] = y;
                }
            }
        }
        return order.length() == letters ? order.toString() : ""; // letters left over sit on a cycle
    }
}
