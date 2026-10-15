import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Questions the app asks about the friend graph. */
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

    /** Walks only the friends of friends: each path user -> friend -> candidate adds one mutual friend. */
    public List<Suggestion> suggestions(String user, int k) {
        requireUser(user);
        if (k < 0) {
            throw new IllegalArgumentException("k cannot be negative: " + k);
        }
        Set<String> friends = graph.friendsOf(user);
        Map<String, Integer> mutual = new HashMap<>();
        for (String friend : friends) {
            for (String candidate : graph.friendsOf(friend)) {
                if (!candidate.equals(user) && !friends.contains(candidate)) {
                    mutual.merge(candidate, 1, Integer::sum);
                }
            }
        }
        return mutual.entrySet().stream()
                .map(e -> new Suggestion(e.getKey(), e.getValue()))
                .sorted(Comparator.comparingInt(Suggestion::mutualFriends).reversed().thenComparing(Suggestion::user))
                .limit(k)
                .toList();
    }

    /** Breadth-first search: the first time b is reached, it is reached by a shortest path. */
    public int degreesApart(String a, String b) {
        requireUser(a);
        requireUser(b);
        Map<String, Integer> steps = new HashMap<>();
        Deque<String> queue = new ArrayDeque<>();
        steps.put(a, 0);
        queue.add(a);
        while (!queue.isEmpty()) {
            String current = queue.poll();
            int next = steps.get(current) + 1;
            if (current.equals(b)) {
                return next - 1;
            }
            for (String friend : graph.friendsOf(current)) {
                if (steps.putIfAbsent(friend, next) == null) {
                    queue.add(friend);
                }
            }
        }
        return -1;
    }

    private void requireUser(String user) {
        if (!graph.hasUser(user)) {
            throw new IllegalArgumentException("unknown user " + user);
        }
    }
}
