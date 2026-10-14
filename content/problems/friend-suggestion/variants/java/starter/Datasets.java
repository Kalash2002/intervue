import java.util.Random;

/** Builds the large networks used by the phase 3 tests. Same seed, same network. */
public final class Datasets {
    private Datasets() {}

    /**
     * Users U0..U(n-1). Each user befriends a few others, mostly nearby in number, so the network is
     * one long, loosely knit community: far-apart numbers are many steps apart.
     */
    public static SocialGraph network(int users, int friendsEach, long seed) {
        Random random = new Random(seed);
        SocialGraph graph = new SocialGraph();
        for (int u = 0; u < users; u++) {
            graph.addUser("U" + u);
        }
        for (int u = 0; u < users; u++) {
            for (int f = 0; f < friendsEach / 2; f++) {
                int v = u + 1 + random.nextInt(random.nextInt(20) == 0 ? 2_000 : 50);
                if (v < users) {
                    graph.addFriendship("U" + u, "U" + v);
                }
            }
        }
        return graph;
    }
}
