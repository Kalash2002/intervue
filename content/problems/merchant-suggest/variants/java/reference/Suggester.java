import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Reference solution. Every merchant contributes one entry per word: the rest of its normalized name
 * from that word on. Entries are sorted, so all entries starting with the typed text are one
 * contiguous range found by binary search. Answers are remembered per typed text until the directory changes.
 */
public class Suggester {
    private static final int MAX_LIMIT = 10;

    private final MerchantDirectory directory;
    private long builtForVersion = -1;
    private List<Merchant> merchants;
    private String[] entryText;
    private int[] entryOwner;
    private int[] seenAt;
    private int stamp;
    private final Map<String, List<Merchant>> remembered = new HashMap<>();

    public Suggester(MerchantDirectory directory) {
        this.directory = directory;
    }

    public List<Merchant> suggest(String typed, int limit) {
        if (limit < 1 || limit > MAX_LIMIT) {
            throw new IllegalArgumentException("limit must be between 1 and " + MAX_LIMIT + ": " + limit);
        }
        String prefix = Normalizer.normalize(typed);
        if (prefix.isEmpty()) {
            return List.of();
        }
        rebuildIfDirectoryChanged();
        List<Merchant> top = remembered.computeIfAbsent(prefix, this::topMatches);
        return top.size() <= limit ? top : top.subList(0, limit);
    }

    private void rebuildIfDirectoryChanged() {
        if (builtForVersion == directory.version()) {
            return;
        }
        merchants = directory.all();
        record Entry(String text, int owner) {}
        List<Entry> entries = new ArrayList<>();
        for (int owner = 0; owner < merchants.size(); owner++) {
            String key = Normalizer.normalize(merchants.get(owner).name());
            entries.add(new Entry(key, owner));
            for (int i = key.indexOf(' '); i >= 0; i = key.indexOf(' ', i + 1)) {
                entries.add(new Entry(key.substring(i + 1), owner));
            }
        }
        entries.sort(Comparator.comparing(Entry::text));
        entryText = new String[entries.size()];
        entryOwner = new int[entries.size()];
        for (int i = 0; i < entryText.length; i++) {
            entryText[i] = entries.get(i).text();
            entryOwner[i] = entries.get(i).owner();
        }
        seenAt = new int[merchants.size()];
        stamp = 0;
        remembered.clear();
        builtForVersion = directory.version();
    }

    /** The best MAX_LIMIT merchants for a prefix, best first. */
    private List<Merchant> topMatches(String prefix) {
        int[] top = new int[MAX_LIMIT];
        int count = 0;
        stamp++;
        for (int i = firstEntryAtOrAfter(prefix); i < entryText.length && entryText[i].startsWith(prefix); i++) {
            int owner = entryOwner[i];
            if (seenAt[owner] == stamp) {
                continue; // already counted through another word of the same name
            }
            seenAt[owner] = stamp;
            if (count == MAX_LIMIT && !better(owner, top[count - 1])) {
                continue;
            }
            // Insertion into a tiny sorted array: at most MAX_LIMIT shifts.
            int position = count < MAX_LIMIT ? count++ : count - 1;
            while (position > 0 && better(owner, top[position - 1])) {
                top[position] = top[position - 1];
                position--;
            }
            top[position] = owner;
        }
        List<Merchant> result = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            result.add(merchants.get(top[i]));
        }
        return List.copyOf(result);
    }

    /** Busier merchant first; for equal counts the earlier name. merchants is sorted by normalized name. */
    private boolean better(int a, int b) {
        long ta = merchants.get(a).txnCount();
        long tb = merchants.get(b).txnCount();
        return ta != tb ? ta > tb : a < b;
    }

    private int firstEntryAtOrAfter(String prefix) {
        int low = 0;
        int high = entryText.length;
        while (low < high) {
            int middle = (low + high) >>> 1;
            if (entryText[middle].compareTo(prefix) < 0) {
                low = middle + 1;
            } else {
                high = middle;
            }
        }
        return low;
    }
}
