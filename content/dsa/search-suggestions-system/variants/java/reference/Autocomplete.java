import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Suggestions for a search box. Sort once; the names with a prefix are one block of the sorted array. */
public class Autocomplete {

    static List<List<String>> suggest(String[] products, String typed, int limit) {
        String[] sorted = products.clone();
        Arrays.sort(sorted);
        List<List<String>> answer = new ArrayList<>(typed.length());
        int start = 0; // a longer prefix never begins before the shorter one's block
        for (int length = 1; length <= typed.length(); length++) {
            String prefix = typed.substring(0, length);
            start = firstAtLeast(sorted, prefix, start);
            List<String> found = new ArrayList<>(limit);
            for (int i = start; i < sorted.length && found.size() < limit && sorted[i].startsWith(prefix); i++) {
                found.add(sorted[i]);
            }
            answer.add(found);
        }
        return answer;
    }

    /** First index at or after from whose name is not smaller than the prefix. */
    private static int firstAtLeast(String[] sorted, String prefix, int from) {
        int low = from;
        int high = sorted.length;
        while (low < high) {
            int middle = (low + high) >>> 1;
            if (sorted[middle].compareTo(prefix) < 0) {
                low = middle + 1;
            } else {
                high = middle;
            }
        }
        return low;
    }
}
