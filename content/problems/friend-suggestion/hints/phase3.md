## Level 1

A user has about eight friends, and each of them has about eight. Ask whether your suggestions touch those few dozen people or every user in the network, and whether your search checks "already visited" in constant time.

## Level 2

Testing every user in the network as a candidate costs the whole network per call; walking friends of friends costs only a few dozen. A search that keeps visited users in a list pays a full list scan for each new user, which is quadratic in the size of the network.

## Level 3

1. Generate candidates only from the friends of the user's friends.
2. Count mutual friends while you walk, instead of recomputing an intersection for each candidate.
3. Keep visited users and their step counts in a hash map.
4. Use an `ArrayDeque` as the queue rather than removing from the front of a list.
5. Run Phase 3 tests and compare each test's time with its limit.
