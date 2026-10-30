import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** A small social feed: users post, follow each other and read their newest posts. */
public class Twitter {
    private static final int FEED_SIZE = 10;

    private final List<int[]> timeline = new ArrayList<>(); // every post, {userId, tweetId}, oldest first
    private final Map<Integer, Set<Integer>> following = new HashMap<>();

    /** Records a new post by userId. */
    void postTweet(int userId, int tweetId) {
        timeline.add(new int[] {userId, tweetId});
    }

    /** Returns up to 10 newest post ids by userId and the users it follows, newest first. */
    List<Integer> getNewsFeed(int userId) {
        Set<Integer> authors = following.getOrDefault(userId, Set.of());
        List<Integer> feed = new ArrayList<>();
        for (int i = timeline.size() - 1; i >= 0 && feed.size() < FEED_SIZE; i--) {
            int[] post = timeline.get(i);
            if (post[0] == userId || authors.contains(post[0])) {
                feed.add(post[1]);
            }
        }
        return feed;
    }

    /** Makes followerId follow followeeId; following yourself changes nothing. */
    void follow(int followerId, int followeeId) {
        following.computeIfAbsent(followerId, u -> new HashSet<>()).add(followeeId);
    }

    /** Makes followerId stop following followeeId, if it did. */
    void unfollow(int followerId, int followeeId) {
        Set<Integer> set = following.get(followerId);
        if (set != null) {
            set.remove(followeeId);
        }
    }
}
