import static kit.Check.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import kit.Test;

public class Phase1Tests {

    /** Sorts inside each group and then the groups, so the comparison ignores order. */
    private static List<String> normalise(List<List<String>> groups) {
        List<String> lines = new ArrayList<>();
        for (List<String> group : groups) {
            List<String> copy = new ArrayList<>(group);
            Collections.sort(copy);
            lines.add(String.join(",", copy) + "|" + copy.size());
        }
        Collections.sort(lines);
        return lines;
    }

    private static void expect(String[] words, String... groupLines) {
        List<String> want = new ArrayList<>(List.of(groupLines));
        Collections.sort(want);
        assertEquals(want, normalise(GroupAnagrams.group(words)));
    }

    @Test(order = 1)
    void sample() {
        expect(new String[] {"tide", "diet", "edit", "rain", "iran"}, "diet,edit,tide|3", "iran,rain|2");
    }

    @Test(order = 2)
    void singleWord() {
        expect(new String[] {"solo"}, "solo|1");
    }

    @Test(order = 3)
    void noWords() {
        assertEquals(0, GroupAnagrams.group(new String[] {}).size());
    }

    @Test(order = 4)
    void emptyWordsGroupTogether() {
        expect(new String[] {"", "", "a"}, ",|2", "a|1");
    }

    @Test(order = 5)
    void repeatedWordsStayInTheGroup() {
        expect(new String[] {"ab", "ba", "ab"}, "ab,ab,ba|3");
    }

    @Test(order = 6)
    void nothingGroups() {
        expect(new String[] {"abc", "abd", "abe"}, "abc|1", "abd|1", "abe|1");
    }

    @Test(order = 7)
    void sameLengthDifferentLetterCounts() {
        expect(new String[] {"aabb", "abbb", "baba", "bbba"}, "aabb,baba|2", "abbb,bbba|2");
    }

    @Test(order = 8)
    void oneBigGroup() {
        expect(new String[] {"stop", "pots", "tops", "spot", "opts", "post"},
                "opts,post,pots,spot,stop,tops|6");
    }

    @Test(order = 9, timeoutMs = 2000)
    void manyRandomWords() {
        Random random = new Random(7);
        int n = 60_000;
        String[] words = new String[n];
        for (int i = 0; i < n; i++) {
            char[] letters = new char[8];
            for (int j = 0; j < 8; j++) {
                letters[j] = (char) ('a' + random.nextInt(26));
            }
            words[i] = new String(letters);
        }
        // Add shuffled copies of the first 1,000 words so groups of size 2 or more exist.
        for (int i = 0; i < 1000; i++) {
            char[] letters = words[i].toCharArray();
            for (int j = letters.length - 1; j > 0; j--) {
                int k = random.nextInt(j + 1);
                char swap = letters[j];
                letters[j] = letters[k];
                letters[k] = swap;
            }
            words[n - 1 - i] = new String(letters);
        }
        List<List<String>> groups = GroupAnagrams.group(words);
        int total = 0;
        for (List<String> group : groups) {
            total += group.size();
            char[] first = group.get(0).toCharArray();
            java.util.Arrays.sort(first);
            for (String word : group) {
                char[] letters = word.toCharArray();
                java.util.Arrays.sort(letters);
                assertEquals(new String(first), new String(letters));
            }
        }
        assertEquals(n, total);
        // Words of different classes must not share a group: the number of groups equals the number of
        // distinct sorted-letter strings.
        java.util.Set<String> classes = new java.util.HashSet<>();
        for (String word : words) {
            char[] letters = word.toCharArray();
            java.util.Arrays.sort(letters);
            classes.add(new String(letters));
        }
        assertEquals(classes.size(), groups.size());
    }
}
