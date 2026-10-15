import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Suggestions for a search box. Filters and sorts the whole catalogue again for every letter. */
public class Autocomplete {

    static List<List<String>> suggest(String[] products, String typed, int limit) {
        List<List<String>> answer = new ArrayList<>();
        for (int length = 1; length <= typed.length(); length++) {
            String prefix = typed.substring(0, length);
            List<String> matches = new ArrayList<>();
            for (String product : products) {
                if (product.startsWith(prefix)) {
                    matches.add(product);
                }
            }
            Collections.sort(matches);
            answer.add(new ArrayList<>(matches.subList(0, Math.min(limit, matches.size()))));
        }
        return answer;
    }
}
