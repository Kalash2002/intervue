## Level 1

Suggestions only ever come from your friends' friend lists, and each time you reach a candidate through a different friend, that friend is one more you share. Degrees apart is a shortest number of steps in a graph where every step costs the same.

## Level 2

For suggestions, walk each friend's friends, skip yourself and people you already know, and count how often each candidate appears in a map; the count is the number of mutual friends. For degrees apart, use breadth-first search from `a`, recording each user's step count the first time it is reached.

## Level 3

1. Check the arguments first: unknown users and a negative `k` are errors.
2. For suggestions, build a map from candidate to count, then sort by count descending and id ascending, and keep the first `k`.
3. For degrees apart, return 0 at once when both users are the same.
4. Use a queue and a map of steps; add a user to the queue only when it is not yet in the map.
5. Return the step count when you take `b` off the queue, and -1 when the queue runs dry.
