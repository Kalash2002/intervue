/** Looks for rearrangements of a word inside a text. */
public class Scramble {

    /** Returns true if some stretch of the text is a rearrangement of the word. */
    static boolean appearsIn(String word, String text) {
        int m = word.length();
        if (m > text.length()) {
            return false; // no stretch is long enough
        }
        int[] diff = new int[26]; // per letter: count in the stretch minus count in the word
        int off = 0; // how many letters have a non-zero difference
        for (int i = 0; i < m; i++) {
            off += shift(diff, word.charAt(i) - 'a', -1);
            off += shift(diff, text.charAt(i) - 'a', +1);
        }
        if (off == 0) {
            return true;
        }
        for (int end = m; end < text.length(); end++) {
            off += shift(diff, text.charAt(end) - 'a', +1); // enters the stretch
            off += shift(diff, text.charAt(end - m) - 'a', -1); // leaves the stretch
            if (off == 0) {
                return true;
            }
        }
        return false;
    }

    /** Changes one letter's difference and returns how the number of unequal letters changed. */
    private static int shift(int[] diff, int letter, int by) {
        boolean wasOff = diff[letter] != 0;
        diff[letter] += by;
        return (diff[letter] != 0 ? 1 : 0) - (wasOff ? 1 : 0);
    }
}
