import java.util.List;

/** Questions the app asks about the friend graph. */
public class FriendFinder {
    private final SocialGraph graph;

    public FriendFinder(SocialGraph graph) {
        this.graph = graph;
    }

    public boolean areFriends(String a, String b) {
        return graph.friendsOf(a).contains(b);
    }

    /** How many friends two users have in common. */
    public int mutualFriends(String a, String b) {
        int shared = 0;
        for (String friend : graph.friendsOf(a)) {
            if (graph.friendsOf(b).contains(friend)) {
                shared++;
            }
        }
        return shared;
    }

    /** Friends of friends, best first. See the brief. */
    public List<Suggestion> suggestions(String user, int k) {
        throw new UnsupportedOperationException("suggestions is not implemented yet");
    }

    /** Fewest friendship steps between two users. See the brief. */
    public int degreesApart(String a, String b) {
        throw new UnsupportedOperationException("degreesApart is not implemented yet");
    }
}
