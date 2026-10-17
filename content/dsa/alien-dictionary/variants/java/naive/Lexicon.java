/** Recovers the alphabet of a language from a sorted word list. */
public class Lexicon {

    /**
     * Returns every letter used in words exactly once, in an order under which words is sorted, or
     * "" when no such order exists.
     */
    static String letterOrder(String[] words) {
        boolean[] used = new boolean[26];
        for (String word : words) {
            for (int i = 0; i < word.length(); i++) {
                used[word.charAt(i) - 'a'] = true;
            }
        }
        boolean[][] before = new boolean[26][26]; // before[x][y]: letter x must come before letter y
        for (int i = 0; i < words.length; i++) {
            for (int j = i + 1; j < words.length; j++) { // every pair, not just neighbours
                String first = words[i];
                String second = words[j];
                int shorter = Math.min(first.length(), second.length());
                int k = 0;
                while (k < shorter && first.charAt(k) == second.charAt(k)) {
                    k++;
                }
                if (k < shorter) {
                    before[first.charAt(k) - 'a'][second.charAt(k) - 'a'] = true;
                } else if (first.length() > second.length()) {
                    return ""; // a word before its own proper prefix
                }
            }
        }
        StringBuilder order = new StringBuilder();
        boolean[] placed = new boolean[26];
        boolean progress = true;
        while (progress) { // place any letter whose earlier letters are all placed
            progress = false;
            for (int y = 0; y < 26; y++) {
                if (!used[y] || placed[y]) {
                    continue;
                }
                boolean free = true;
                for (int x = 0; x < 26; x++) {
                    free &= !(before[x][y] && !placed[x]);
                }
                if (free) {
                    placed[y] = true;
                    order.append((char) ('a' + y));
                    progress = true;
                }
            }
        }
        for (int y = 0; y < 26; y++) {
            if (used[y] && !placed[y]) {
                return ""; // a letter on a cycle never becomes free
            }
        }
        return order.toString();
    }
}
