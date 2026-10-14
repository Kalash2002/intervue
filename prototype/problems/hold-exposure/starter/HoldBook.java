import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** Every hold the ledger knows about. Holds can be released early, which removes them from all calculations. */
public class HoldBook {
    private final List<Hold> holds = new ArrayList<>();
    private final Set<String> releasedIds = new HashSet<>();

    public void add(Hold hold) {
        holds.add(hold);
    }

    /** Releases a hold early. Returns false when no hold has that id. */
    public boolean release(String holdId) {
        for (Hold hold : holds) {
            if (hold.id().equals(holdId)) {
                releasedIds.add(holdId);
                return true;
            }
        }
        return false;
    }

    /** Holds of one account that have not been released, in the order they were added. */
    public List<Hold> holdsFor(String account) {
        List<Hold> result = new ArrayList<>();
        for (Hold hold : holds) {
            if (hold.account().equals(account) && !releasedIds.contains(hold.id())) {
                result.add(hold);
            }
        }
        return result;
    }

    /** Every account that has at least one hold, in the order first seen. */
    public Set<String> accounts() {
        Set<String> accounts = new LinkedHashSet<>();
        for (Hold hold : holds) {
            accounts.add(hold.account());
        }
        return accounts;
    }

    public int size() {
        return holds.size();
    }
}
