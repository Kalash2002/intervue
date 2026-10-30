## Level 1
Start with what to store: who posted what, in what order, and who follows whom. Then ask what a feed request really needs to look at. Does it need posts by people the user does not follow?

## Level 2
Keep each user's own posts in order, newest last. A feed is then the newest ten among a handful of already ordered lists, one per author the user can see. You have seen this shape before when merging sorted lists.

## Level 3
To take the newest posts from several ordered lists one at a time, you only ever need to compare the newest unread post of each list.
