# Grader notes: Friend Suggestion

**Phase 1 bug.** `addFriendship` stores `b` in a's set and then `b` in b's own set (`.add(b)` where `.add(a)` was meant). The graph becomes directed and every second-named user lists themselves. Tests on the first-named side (`theFirstNamedSideSeesItsFriends`) and on unknown or lonely users pass, which is why only some tests fail. Fix: `.add(a)`.

**Phase 2.** Suggestions are the friends of friends minus the user and existing friends; counting how many times each candidate is reached gives the mutual-friend count without any set intersection. Ranking is count descending, id ascending. `degreesApart` is breadth-first search; depth-first search finds a path, not the shortest one, and fails `degreesApartCountsTheShortestPath`. Same user is 0, unreachable is -1, unknown users throw.

**Phase 3.** 100,000 users with about eight friends each. Scanning every user as a candidate costs about 10^5 per call and 4,000 calls time out; walking friends of friends costs about 64. A breadth-first search with a list for visited users is O(V^2); a hash map and an `ArrayDeque` make it O(V + E).

**Common assistant mistakes to watch for.** Fixing phase 1 by also adding a reverse lookup in `areFriends` instead of storing both directions; suggesting existing friends; counting a candidate once instead of once per shared friend; depth-first search for degrees apart; returning -1 for the same user.
