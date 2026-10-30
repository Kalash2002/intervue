/** Decides whether two words use exactly the same letters the same number of times. */
public class Anagrams {

    /** Returns true when a can be rearranged to spell b. */
    static boolean areAnagrams(String a, String b) {
        if (a.length() != b.length()) {
            return false;
        }
        boolean[] used = new boolean[b.length()]; // letters of b already matched
        for (int i = 0; i < a.length(); i++) {
            boolean found = false;
            for (int j = 0; j < b.length() && !found; j++) {
                if (!used[j] && b.charAt(j) == a.charAt(i)) {
                    used[j] = true;
                    found = true;
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }
}
