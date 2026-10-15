## Phase 2: people you may know

Implement two methods in `FriendFinder`:

```java
List<Suggestion> suggestions(String user, int k)
int degreesApart(String a, String b)
```

`suggestions` rules:

- Suggest friends of friends: people exactly two steps away who are not the user and not already their friend.
- Rank them by the number of friends they share with the user, most first; break ties by user id, alphabetically.
- Return at most `k`. Throw `IllegalArgumentException` for a negative `k` or an unknown user.

`degreesApart` rules:

- Return the smallest number of friendship steps from `a` to `b`: 1 for friends, 2 for friends of friends, and so on.
- Return 0 when `a` and `b` are the same user, and -1 when there is no path.
- Throw `IllegalArgumentException` when either user is unknown.

Run **Phase 2 tests**.
