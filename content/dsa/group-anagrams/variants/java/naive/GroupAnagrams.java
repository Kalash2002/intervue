import java.util.ArrayList;
import java.util.List;

/** Groups words made of the same letters. */
public class GroupAnagrams {

    /** Returns the groups of anagrams; order of groups and of words in a group is free. */
    static List<List<String>> group(String[] words) {
        List<List<String>> groups = new ArrayList<>();
        for (String word : words) {
            boolean placed = false;
            for (List<String> group : groups) { // compare with a member of every existing group
                if (sameLetters(group.get(0), word)) {
                    group.add(word);
                    placed = true;
                    break;
                }
            }
            if (!placed) {
                List<String> fresh = new ArrayList<>();
                fresh.add(word);
                groups.add(fresh);
            }
        }
        return groups;
    }

    private static boolean sameLetters(String a, String b) {
        if (a.length() != b.length()) {
            return false;
        }
        int[] counts = new int[26];
        for (int i = 0; i < a.length(); i++) {
            counts[a.charAt(i) - 'a']++;
            counts[b.charAt(i) - 'a']--;
        }
        for (int count : counts) {
            if (count != 0) {
                return false;
            }
        }
        return true;
    }
}
