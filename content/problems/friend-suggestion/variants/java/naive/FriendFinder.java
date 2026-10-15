import java.util.ArrayList;
import java.util.List;

/**
 * Naive solution: correct, and slow on purpose. Suggestions test every user in the network, and the
 * search keeps its visited users in a list, so both are quadratic in the size of the network.
 * Phases 1 and 2 pass; phase 3 must time out.
 */
public class FriendFinder {
    private final SocialGraph graph;

    public FriendFinder(SocialGraph graph) {
        this.graph = graph;
    }

    public boolean areFriends(String a, String b) {
        return graph.friendsOf(a).contains(b);
    }

    public int mutualFriends(String a, String b) {
        int shared = 0;
        for (String friend : graph.friendsOf(a)) {
            if (graph.friendsOf(b).contains(friend)) {
                shared++;
            }
        }
        return shared;
    }

    public List<Suggestion> suggestions(String user, int k) {
        requireUser(user);
        if (k < 0) {
            throw new IllegalArgumentException("k cannot be negative: " + k);
        }
        List<Suggestion> all = new ArrayList<>();
        for (String other : graph.users()) {
            if (other.equals(user) || areFriends(user, other)) {
                continue;
            }
            int shared = mutualFriends(user, other);
            if (shared > 0) {
                all.add(new Suggestion(other, shared));
            }
        }
        all.sort((x, y) -> x.mutualFriends() != y.mutualFriends()
                ? y.mutualFriends() - x.mutualFriends() : x.user().compareTo(y.user()));
        return all.subList(0, Math.min(k, all.size()));
    }

    public int degreesApart(String a, String b) {
        requireUser(a);
        requireUser(b);
        List<String> visited = new ArrayList<>(List.of(a));
        List<String> level = new ArrayList<>(List.of(a));
        int steps = 0;
        while (!level.isEmpty()) {
            if (level.contains(b)) {
                return steps;
            }
            List<String> next = new ArrayList<>();
            for (String current : level) {
                for (String friend : graph.friendsOf(current)) {
                    if (!visited.contains(friend)) {
                        visited.add(friend);
                        next.add(friend);
                    }
                }
            }
            level = next;
            steps++;
        }
        return -1;
    }

    private void requireUser(String user) {
        if (!graph.hasUser(user)) {
            throw new IllegalArgumentException("unknown user " + user);
        }
    }
}
