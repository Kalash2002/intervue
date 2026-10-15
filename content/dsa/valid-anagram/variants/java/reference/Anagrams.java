/** Decides whether two words use exactly the same letters the same number of times. */
public class Anagrams {

    /** Returns true when a can be rearranged to spell b. */
    static boolean areAnagrams(String a, String b) {
        if (a.length() != b.length()) {
            return false; // different lengths can never use the same letters
        }
        int[] counts = new int[26]; // how many of each letter a has not yet been matched by b
        for (int i = 0; i < a.length(); i++) {
            counts[a.charAt(i) - 'a']++;
        }
        for (int i = 0; i < b.length(); i++) {
            if (--counts[b.charAt(i) - 'a'] < 0) {
                return false; // b uses a letter more often than a does
            }
        }
        return true; // equal lengths and no overdraw: every count is back to zero
    }
}
