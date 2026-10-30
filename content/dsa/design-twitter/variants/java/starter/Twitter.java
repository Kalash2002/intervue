import java.util.*;

/** A small social feed: users post, follow each other and read their newest posts. */
public class Twitter {

    /** Records a new post by userId. */
    void postTweet(int userId, int tweetId) {
    }

    /** Returns up to 10 newest post ids by userId and the users it follows, newest first. */
    List<Integer> getNewsFeed(int userId) {
        return new ArrayList<>();
    }

    /** Makes followerId follow followeeId; following yourself changes nothing. */
    void follow(int followerId, int followeeId) {
    }

    /** Makes followerId stop following followeeId, if it did. */
    void unfollow(int followerId, int followeeId) {
    }
}
