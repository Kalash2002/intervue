import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Groups words made of the same letters. */
public class GroupAnagrams {

    /** Returns the groups of anagrams; order of groups and of words in a group is free. */
    static List<List<String>> group(String[] words) {
        Map<String, List<String>> groups = new HashMap<>();
        for (String word : words) {
            // counts[i] is how many times letter 'a' + i occurs; a char holds up to 65,535, and a word has at most 100
            char[] counts = new char[26];
            for (int i = 0; i < word.length(); i++) {
                counts[word.charAt(i) - 'a']++;
            }
            groups.computeIfAbsent(new String(counts), k -> new ArrayList<>()).add(word); // anagrams give equal counts
        }
        return new ArrayList<>(groups.values());
    }
}
