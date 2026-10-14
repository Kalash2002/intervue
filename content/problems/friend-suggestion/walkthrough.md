# How a strong round goes: Friend Suggestion

**Read this first.** This is one good way through the problem, not the only one. The prompts are written for interview mode, where the assistant does what you specify and nothing more.

## Minutes 0 to 5: read before you run

1. `SocialGraph`: a map from user to a set of friends. Read `addFriendship` one line at a time and say aloud whose set changes.
2. `FriendFinder`: `areFriends` and `mutualFriends` use the sets directly.
3. `data/friends.csv`: six friendships and gus, who has none. Sketch the graph on paper: a diamond of ana, ben, cai and dev, then a tail of eli and fay.

## Phase 1: one-way friendships (minutes 5 to 10)

Run phase 1. Tests about ana's own list and about gus pass; `friendshipGoesBothWays` fails. Say: "The second-named side never learns about the first, and it learns about itself instead: `.add(b)` should be `.add(a)`." Fix, rerun, and point out that the compiler cannot help because both are strings.

## Phase 2: people you may know (minutes 10 to 30)

Say the idea before coding: "Every path user, friend, candidate is one shared friend. Counting how often I reach each candidate gives the mutual count." Check by hand: from dev, ana is reached through ben and cai (2), fay through eli (1).

```text
Implement FriendFinder.suggestions(user, k): for each friend of the user, for each of that
friend's friends, skip the user and existing friends and count the candidate in a HashMap.
Sort by count descending, then id ascending; limit k. Throw IllegalArgumentException for an
unknown user or k < 0. Then degreesApart as a breadth-first search with an ArrayDeque and a
HashMap of step counts; 0 for the same user, -1 when unreachable, unknown users throw.
```

Check that the search records a user when it is queued, not when it is taken off the queue, or users are queued twice.

## Phase 3: the whole network (minutes 30 to 45)

If phase 2 followed the plan above, phase 3 passes. If suggestions loop over every user, or the search uses `List.contains`, rewrite those parts.

## Complexity

Suggestions cost O(d^2) for a user with d friends who have d friends each; degrees apart is O(V + E).

## Follow-up answers

- Breadth-first search explores in rings of equal distance, so the first time it reaches b is along a shortest path; with weighted edges it would need Dijkstra's algorithm.
- A celebrity with two million friends makes every friend's suggestions walk two million people: cap or sample the celebrity's list, or precompute suggestions offline.
