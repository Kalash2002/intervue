# How a strong round goes: Friend Suggestion

**Read this first.** This is one good way through the problem, not the only one. The complexity figures describe the reference solution. The practice links go to other sites and to different questions that use the same technique; they are not this question. The prompts are written for interview mode, where the assistant does what you specify and nothing more.

## Minutes 0 to 5: read before you run

Say aloud: "I'll read the graph, the finder and the sample, and sketch the sample, before I run anything."

1. `SocialGraph`: a map from user to a set of friends. Read `addFriendship` one line at a time and say aloud whose set changes on each line.
2. `FriendFinder`: `areFriends` and `mutualFriends` read the sets directly, so they are only as right as the graph.
3. `data/friends.csv`: seven friendships and gus, who has none. Sketch it on paper: a diamond of ana, ben, cai and dev (ana-ben, ana-cai, ben-cai, ben-dev, cai-dev), then a tail dev-eli-fay.
4. `Phase1Tests.java`: five tests; read the names.

Only now run the phase 1 tests.

## Prompt: understand the codebase fast

```text
Read-only question. For this project give me:
1. One line per class: what it owns and which other classes it calls.
2. For SocialGraph.addFriendship(a, b): which user's set each line changes and what it adds.
3. For FriendFinder.mutualFriends: how many set lookups it makes for users with d friends.
Keep it under 150 words. Mark anything you are inferring.
```

Check item 2 against the code yourself, line by line. If the answer says "both directions are stored", it is wrong, and you have caught the assistant reading what the method should do instead of what it does.

## Phase 1: one-way friendships (minutes 5 to 10)

### Reproduce

Run phase 1 tests. `lonelyAndUnknownUsersHaveNoFriends` and `theFirstNamedSideSeesItsFriends` pass. `friendshipGoesBothWays`, `nobodyIsTheirOwnFriend` and `mutualFriendsOnTheSample` fail.

### Narrow it down, aloud

"ana sees her friends because she is always named first. ben is named second in ana-ben, and he does not see ana; he sees himself. So the second line of `addFriendship` adds the wrong name to the second user's set."

### The cause

```java
friends.computeIfAbsent(b, u -> new LinkedHashSet<>()).add(b);
```

### The fix

```java
friends.computeIfAbsent(b, u -> new LinkedHashSet<>()).add(a);
```

Run phase 1 tests again; all five pass.

### Say this

"One character made the graph directed and gave every second-named user a self-loop. The compiler cannot help because `a` and `b` are both strings. The tests that passed only looked at the first-named side or at users with no friends."

## Phase 2: people you may know (minutes 10 to 32)

### Analysis before any prompt

- A candidate is exactly two steps away: a friend of a friend who is not the user and not already a friend.
- Every path user, friend, candidate is one shared friend. Counting how many times I reach a candidate **is** the mutual-friend count; no set intersection needed.
- `degreesApart` asks for the fewest steps, which is breadth-first search. Depth-first search finds a path, not the shortest.

Check by hand from the sketch: from dev, ana is reached through ben and through cai (2), fay through eli (1). From ana to fay is 4 steps: ana, ben, dev, eli, fay.

### Decide the approach yourself

"Suggestions: a map from candidate to count, filled by a double loop over friends and their friends, then a sort. Degrees apart: a queue and a map of step counts, marking a user when it is queued."

### Prompt

```text
Implement FriendFinder.suggestions(user, k): throw IllegalArgumentException for k < 0 or an
unknown user. For each friend of the user, for each of that friend's friends, skip the user and
the user's existing friends, and add one to the candidate's count in a HashMap<String, Integer>.
Return Suggestion(candidate, count) sorted by count descending, then id ascending, limited to k.
Only this method.
```

Then:

```text
Implement degreesApart(a, b) as breadth-first search: throw IllegalArgumentException when either
user is unknown; put a in a HashMap<String, Integer> steps with 0 and in an ArrayDeque. Poll a
user; if it is b return its steps; for each friend, if steps.putIfAbsent(friend, steps + 1)
returns null, add the friend to the queue. Return -1 when the queue empties.
```

### Check the reply for

- Existing friends filtered out. Suggesting a current friend fails `suggestsFriendsOfFriendsWithTheirSharedCount`.
- One count per shared friend, not one per candidate.
- A user marked when queued, not when polled; otherwise users are queued many times.
- 0 for the same user (the start is `b` immediately) and -1 when unreachable.

Run phase 2 tests after each method.

## Phase 3: the whole network (minutes 32 to 48)

### Read the tests first

100,000 users with about eight friends each. Say how much of the network each call touches: "Suggestions walk friends of friends, about 8 x 8 = 64 users, so 4,000 calls is about a quarter of a million steps. Degrees apart visits each user and friendship at most once."

### What to do

If phase 2 followed the plan above, phase 3 already passes. Run it. If suggestions time out, look for a loop over every user as a candidate (100,000 per call, 400 million in total). If degrees apart times out, look for `List.contains` as the visited check, which makes the search quadratic; use the map.

Run all three phases.

## The technique: graph traversal by neighbours and breadth-first search

**The idea.** A social network is an undirected graph stored as an adjacency list: each user maps to the set of their friends. "Friends of friends" is a walk of two steps from one node, so its cost depends on the user's neighbourhood, not on the size of the network. "Fewest steps" is breadth-first search: it explores in rings of equal distance, so the first time it reaches a node is along a shortest path.

**How to recognise it.** "Degrees of separation", "minimum moves", "shortest path" with unweighted steps, "people you may know", word ladders, grid mazes.

**The two decisions that cause bugs.** An undirected edge must be stored in both directions. And the visited mark goes on when a node is queued; marking on removal lets the same node enter the queue many times.

**When BFS is not enough.** With weighted edges (say, stronger and weaker friendships) the fewest steps is not the cheapest path; use Dijkstra's algorithm.

| Approach | Time | Extra space |
| --- | --- | --- |
| Suggestions by scanning every user and intersecting friend sets | O(V x d) per call | O(V) |
| Suggestions by walking friends of friends | O(d squared) per call | O(d squared) |
| Degrees apart by BFS with a hash map | O(V + E) | O(V) |
| BFS with a list as the visited check | O(V squared) | O(V) |

**Practise the technique**

These open inside the app under DSA.

- [Number of Islands](dsa://number-of-islands): traversal with a visited mark.
- [Word Ladder](dsa://word-ladder): fewest steps in an unweighted graph, the same shape as `degreesApart`.
- [Find if Path Exists in Graph](dsa://find-if-path-exists-in-graph): building an undirected adjacency list in both directions.

## Time budget

| Minutes | What |
| --- | --- |
| 0 to 5 | Read the graph and finder, sketch the sample |
| 5 to 10 | Phase 1: reproduce, read `addFriendship` line by line, one-character fix |
| 10 to 14 | Phase 2 idea aloud, dev's suggestions and ana to fay worked by hand |
| 14 to 32 | Phase 2: `suggestions`, then `degreesApart`, a run after each |
| 32 to 48 | Phase 3: read the tests, say how much each call touches, run, fix any full scan |
| 48 to 60 | Read your whole diff, answer the follow-ups aloud |

## Follow-up answers

**How did one character make the graph directed?** `.add(b)` on b's own set means the second-named user gets themselves instead of the first-named user. Every edge exists only from a to b, plus a self-loop on b. `friendshipGoesBothWays` caught it first: ben is not friends with ana.

**Why does BFS give the fewest steps?** It visits every node at distance 1 before any at distance 2, and so on, so the first visit to b is at its smallest distance. It stops being right when steps have different costs; then use Dijkstra's algorithm.

**Cost of suggestions.** For a user with d friends who each have d friends: O(d squared) to count, plus sorting the candidates, at most d squared of them, so O(d squared log d). It does not depend on the number of users.

**A celebrity with two million friends.** Any of their friends walks two million people per suggestion call, and the celebrity is a "mutual friend" of almost everyone. Options: skip or cap very high-degree nodes when counting, sample their friend list, or compute suggestions offline in a batch job and serve them from a cache. Say which trade-off the product wants: freshness or cost.

**What the assistant got wrong.** Answer from your own session. Typical: fixing `areFriends` with a reverse lookup instead of storing both directions, suggesting existing friends, counting a candidate once, depth-first search for degrees apart, or -1 for the same user.
