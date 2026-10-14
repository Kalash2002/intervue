## Phase 2: cheapest route with a hop limit

Each extra corridor adds delay and settlement risk, so treasury caps how many corridors a payment may use. Implement:

```java
Optional<Route> cheapest(String from, String to, int maxHops)
```

Rules:

- Return the route with the lowest total fee among routes that use **at most** `maxHops` corridors.
- Use active corridors only.
- If several routes share the lowest fee, return one with the fewest corridors.
- Several corridors may join the same two nodes; fees can be zero.
- A route from a node to itself is `[from]` with fee 0, for any `maxHops` of 0 or more.
- Return `Optional.empty()` when no route fits the limit, or when either node is unknown.
- Throw `IllegalArgumentException` when `maxHops` is negative.

Run **Phase 2 tests**.
