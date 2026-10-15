import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/** Users and their friendships. Friendship is mutual and nobody is their own friend. */
public class SocialGraph {
    private final Map<String, Set<String>> friends = new LinkedHashMap<>();

    public void addUser(String user) {
        friends.computeIfAbsent(user, u -> new LinkedHashSet<>());
    }

    public void addFriendship(String a, String b) {
        if (a.equals(b)) {
            throw new IllegalArgumentException(a + " cannot be their own friend");
        }
        friends.computeIfAbsent(a, u -> new LinkedHashSet<>()).add(b);
        friends.computeIfAbsent(b, u -> new LinkedHashSet<>()).add(b);
    }

    public boolean hasUser(String user) {
        return friends.containsKey(user);
    }

    /** The user's friends; empty for an unknown user. */
    public Set<String> friendsOf(String user) {
        return Collections.unmodifiableSet(friends.getOrDefault(user, Set.of()));
    }

    public Set<String> users() {
        return Collections.unmodifiableSet(friends.keySet());
    }
}
