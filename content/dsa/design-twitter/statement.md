# Design Twitter

Design the core of a small social app. Users post short messages, follow other users, and read a feed of the newest posts they can see. A user sees their own posts and the posts of everyone they follow at the moment the feed is read: following someone shows their older posts too, and unfollowing hides them again.

Implement the class `Twitter` in `Twitter.java` with a no-argument constructor and these methods:

- `void postTweet(int userId, int tweetId)` records a new post with id `tweetId` by `userId`. Posts are ordered by when they were made, not by their ids.
- `List<Integer> getNewsFeed(int userId)` returns the ids of the 10 newest posts that `userId` can see, newest first, or all of them if there are fewer than 10.
- `void follow(int followerId, int followeeId)` makes `followerId` follow `followeeId`. Following someone you already follow, or yourself, changes nothing.
- `void unfollow(int followerId, int followeeId)` makes `followerId` stop following `followeeId`. If it was not following, nothing changes; you always see your own posts.

A user exists as soon as any call names them. The tests make tens of thousands of feed requests while the app holds more than 100,000 posts, most of them by people the readers do not follow, so a feed request should cost time that depends on how many people the user follows, not on how many posts exist. In the examples, the input is the sequence of calls on a new object, and the output lists what each `getNewsFeed` call returned, in order.

## Example 1

**Input:** `["postTweet(4, 101)", "postTweet(9, 205)", "getNewsFeed(4)", "follow(4, 9)", "getNewsFeed(4)", "postTweet(4, 333)", "getNewsFeed(4)", "unfollow(4, 9)", "getNewsFeed(4)"]`
**Output:** `[[101], [205, 101], [333, 205, 101], [333, 101]]`
**Explanation:** User 4 first sees only post 101. After following user 9 it also sees 205, which is newer. After unfollowing, 205 disappears again.

## Example 2

**Input:** `["follow(1, 5)", "follow(1, 6)", "postTweet(5, 70)", "postTweet(6, 71)", "postTweet(5, 72)", "postTweet(7, 74)", "postTweet(1, 73)", "getNewsFeed(1)"]`
**Output:** `[[73, 72, 71, 70]]`
**Explanation:** User 1 sees its own post and those of users 5 and 6, newest first. Post 74 is by user 7, whom user 1 does not follow. Post 73 is the newest even though 74 has a larger id.

## Example 3

**Input:** `["postTweet(3, 900)", "follow(3, 3)", "getNewsFeed(3)", "unfollow(3, 3)", "getNewsFeed(3)", "getNewsFeed(8)"]`
**Output:** `[[900], [900], []]`
**Explanation:** Following yourself does not list your posts twice, and unfollowing yourself does not hide them. User 8 has posted nothing and follows no one, so its feed is empty.

## Constraints

- `0 <= userId, followerId, followeeId <= 10^4`
- `0 <= tweetId <= 10^9`
- Every `tweetId` is posted only once.
- `1 <= total number of calls <= 2 * 10^5`

## Follow-up

What if some users had millions of followers and feeds had to load instantly?
