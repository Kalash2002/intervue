## Level 1

The person listed first in a friendship sees the right friend list; the person listed second does not. Read `SocialGraph.addFriendship` and say, for each of its two lines, whose set changes and what goes into it.

## Level 2

A mutual friendship has to be stored twice: b in a's set and a in b's set. The second line adds b to b's own set, a one-letter slip that compiles because both are strings. That makes the graph one-way and puts people in their own friend lists.

## Level 3

1. Open `SocialGraph.addFriendship`.
2. In the second `computeIfAbsent` line, add `a` to b's set instead of `b`.
3. Keep the check that refuses a friendship with oneself.
4. Run Phase 1 tests: ben's friends should be ana, cai and dev.
