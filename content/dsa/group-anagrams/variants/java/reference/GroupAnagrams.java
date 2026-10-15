import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Groups words made of the same letters. */
public class GroupAnagrams {

    /** Returns the groups of anagrams; order of groups and of words in a group is free. */
    static List<List<String>> group(String[] words) {
        Map<String, List<String>> groups = new HashMap<>();
        for (String word : words) {
            char[] letters = word.toCharArray();
            Arrays.sort(letters); // anagrams sort to the same string
            String key = new String(letters);
            groups.computeIfAbsent(key, k -> new ArrayList<>()).add(word);
        }
        return new ArrayList<>(groups.values());
    }
}
