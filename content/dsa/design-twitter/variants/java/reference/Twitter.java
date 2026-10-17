import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

/** A small social feed: users post, follow each other and read their newest posts. */
public class Twitter {
    private static final int FEED_SIZE = 10;

    private final Map<Integer, List<int[]>> posts = new HashMap<>(); // per user, {time, tweetId}, oldest first
    private final Map<Integer, Set<Integer>> following = new HashMap<>(); // never holds the user itself
    private int clock = 0;

    /** Records a new post by userId. */
    void postTweet(int userId, int tweetId) {
        posts.computeIfAbsent(userId, u -> new ArrayList<>()).add(new int[] {clock++, tweetId});
    }

    /** Returns up to 10 newest post ids by userId and the users it follows, newest first. */
    List<Integer> getNewsFeed(int userId) {
        // one cursor per author, {time, author, index}: the newest unread post of each on top
        PriorityQueue<int[]> newest = new PriorityQueue<>((a, b) -> Integer.compare(b[0], a[0]));
        addLatest(newest, userId);
        for (int author : following.getOrDefault(userId, Set.of())) {
            addLatest(newest, author);
        }
        List<Integer> feed = new ArrayList<>();
        while (feed.size() < FEED_SIZE && !newest.isEmpty()) {
            int[] top = newest.poll();
            List<int[]> list = posts.get(top[1]);
            feed.add(list.get(top[2])[1]);
            if (top[2] > 0) { // that author's next older post
                newest.add(new int[] {list.get(top[2] - 1)[0], top[1], top[2] - 1});
            }
        }
        return feed;
    }

    /** Makes followerId follow followeeId; following yourself changes nothing. */
    void follow(int followerId, int followeeId) {
        if (followerId != followeeId) {
            following.computeIfAbsent(followerId, u -> new HashSet<>()).add(followeeId);
        }
    }

    /** Makes followerId stop following followeeId, if it did. */
    void unfollow(int followerId, int followeeId) {
        Set<Integer> set = following.get(followerId);
        if (set != null) {
            set.remove(followeeId);
        }
    }

    private void addLatest(PriorityQueue<int[]> newest, int author) {
        List<int[]> list = posts.get(author);
        if (list != null) {
            int last = list.size() - 1;
            newest.add(new int[] {list.get(last)[0], author, last});
        }
    }
}
