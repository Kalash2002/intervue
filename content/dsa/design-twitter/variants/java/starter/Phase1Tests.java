import static kit.Check.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import kit.Test;

public class Phase1Tests {

    @Test(order = 1)
    void sample() {
        Twitter app = new Twitter();
        app.postTweet(4, 101);
        app.postTweet(9, 205);
        assertEquals(List.of(101), app.getNewsFeed(4));
        app.follow(4, 9);
        assertEquals(List.of(205, 101), app.getNewsFeed(4));
        app.postTweet(4, 333);
        assertEquals(List.of(333, 205, 101), app.getNewsFeed(4));
        app.unfollow(4, 9);
        assertEquals(List.of(333, 101), app.getNewsFeed(4));
    }

    @Test(order = 2)
    void newestFirstAcrossAuthors() {
        Twitter app = new Twitter();
        app.follow(1, 5);
        app.follow(1, 6);
        app.postTweet(5, 70);
        app.postTweet(6, 71);
        app.postTweet(5, 72);
        app.postTweet(7, 74); // user 1 does not follow 7
        app.postTweet(1, 73);
        assertEquals(List.of(73, 72, 71, 70), app.getNewsFeed(1));
    }

    @Test(order = 3)
    void followingYourselfChangesNothing() {
        Twitter app = new Twitter();
        app.postTweet(3, 900);
        app.follow(3, 3);
        assertEquals(List.of(900), app.getNewsFeed(3)); // not listed twice
        app.unfollow(3, 3);
        assertEquals(List.of(900), app.getNewsFeed(3)); // your own posts stay
        assertEquals(List.of(), app.getNewsFeed(8));
    }

    @Test(order = 4)
    void feedHoldsAtMostTen() {
        Twitter app = new Twitter();
        for (int id = 500; id <= 511; id++) {
            app.postTweet(2, id);
        }
        List<Integer> newestTen = List.of(511, 510, 509, 508, 507, 506, 505, 504, 503, 502);
        assertEquals(newestTen, app.getNewsFeed(2));
        app.follow(6, 2);
        assertEquals(newestTen, app.getNewsFeed(6));
    }

    @Test(order = 5)
    void unfollowIsSafeAndFollowIsASet() {
        Twitter app = new Twitter();
        app.postTweet(2, 40);
        app.unfollow(1, 2); // not following: nothing happens
        assertEquals(List.of(), app.getNewsFeed(1));
        app.follow(1, 2);
        app.follow(1, 2);
        assertEquals(List.of(40), app.getNewsFeed(1)); // not listed twice
        app.unfollow(1, 2);
        assertEquals(List.of(), app.getNewsFeed(1)); // one unfollow undoes both follows
    }

    @Test(order = 6)
    void followGoesOneWay() {
        Twitter app = new Twitter();
        app.follow(10, 20);
        app.postTweet(10, 1000);
        app.postTweet(20, 2000);
        assertEquals(List.of(2000), app.getNewsFeed(20));
        assertEquals(List.of(2000, 1000), app.getNewsFeed(10));
    }

    @Test(order = 7)
    void idsAtTheLimits() {
        Twitter app = new Twitter();
        app.postTweet(0, 0);
        app.postTweet(10_000, 1_000_000_000);
        app.follow(0, 10_000);
        assertEquals(List.of(1_000_000_000, 0), app.getNewsFeed(0));
    }

    @Test(order = 8)
    void interleavedAuthorsWithGaps() {
        Twitter app = new Twitter();
        app.follow(30, 31);
        app.follow(30, 32);
        int[] authors = {31, 33, 32, 32, 31, 33, 30, 31, 32, 33, 31, 31, 32, 30, 33};
        for (int i = 0; i < authors.length; i++) {
            app.postTweet(authors[i], 8000 + i); // user 33 is not followed
        }
        // newest first, skipping 33's posts 8014, 8009, 8005, 8001; ten of the eleven others
        assertEquals(List.of(8013, 8012, 8011, 8010, 8008, 8007, 8006, 8004, 8003, 8002), app.getNewsFeed(30));
    }

    @Test(order = 9)
    void twoAppsAreIndependent() {
        Twitter first = new Twitter();
        Twitter second = new Twitter();
        first.postTweet(1, 11);
        second.postTweet(1, 22);
        assertEquals(List.of(11), first.getNewsFeed(1));
        assertEquals(List.of(22), second.getNewsFeed(1));
    }

    private static int tweetId(int sequence) {
        return 3 * sequence + 1;
    }

    @Test(order = 10, timeoutMs = 3000)
    void busyTimelineWithManyFeeds() {
        Twitter app = new Twitter();
        // readers 1000..1199 each follow 20 of the authors 0..499
        int[][] followed = new int[200][20];
        for (int j = 0; j < 200; j++) {
            for (int t = 0; t < 20; t++) {
                followed[j][t] = (j * 37 + t * 25) % 500;
                app.follow(1000 + j, followed[j][t]);
            }
        }
        // 20 rounds in which each author 0..499 posts once, in order of user id
        for (int round = 0; round < 20; round++) {
            for (int u = 0; u < 500; u++) {
                app.postTweet(u, tweetId(round * 500 + u));
            }
        }
        // then 100,000 newer posts from users 500..999, whom no reader follows
        for (int round = 0; round < 200; round++) {
            for (int u = 500; u < 1000; u++) {
                app.postTweet(u, tweetId(10_000 + round * 500 + (u - 500)));
            }
        }
        // a reader's newest ten are the last-round posts of its ten highest-numbered authors
        List<List<Integer>> expected = new ArrayList<>();
        for (int j = 0; j < 200; j++) {
            int[] authors = followed[j].clone();
            Arrays.sort(authors);
            List<Integer> feed = new ArrayList<>();
            for (int t = 19; t >= 10; t--) {
                feed.add(tweetId(19 * 500 + authors[t]));
            }
            expected.add(feed);
        }
        for (int call = 0; call < 70_000; call++) {
            int j = call % 200;
            List<Integer> got = app.getNewsFeed(1000 + j);
            if (!expected.get(j).equals(got)) {
                assertEquals(expected.get(j), got, "feed of user " + (1000 + j));
            }
        }
    }
}
