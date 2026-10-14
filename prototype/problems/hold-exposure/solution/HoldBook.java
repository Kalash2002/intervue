import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Reference solution: holds are indexed by account and by id, so no call scans the whole book. */
public class HoldBook {
    private final Map<String, List<Hold>> byAccount = new LinkedHashMap<>();
    private final Map<String, Hold> byId = new HashMap<>();
    private int size;

    public void add(Hold hold) {
        byAccount.computeIfAbsent(hold.account(), k -> new ArrayList<>()).add(hold);
        byId.put(hold.id(), hold);
        size++;
    }

    public boolean release(String holdId) {
        Hold hold = byId.get(holdId);
        if (hold == null) {
            return false;
        }
        // The account stays known even when its last hold is released, as in the original.
        byAccount.get(hold.account()).remove(hold);
        return true;
    }

    public List<Hold> holdsFor(String account) {
        return Collections.unmodifiableList(byAccount.getOrDefault(account, List.of()));
    }

    public Set<String> accounts() {
        return Collections.unmodifiableSet(byAccount.keySet());
    }

    public int size() {
        return size;
    }
}
