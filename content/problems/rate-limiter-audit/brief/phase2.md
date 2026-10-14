## Phase 2: replay the limiter

Implement two methods in `RateAudit`:

```java
List<Long> rejections(String client, int limit, long windowMs)
Map<String, Integer> rejectionCounts(int limit, long windowMs)
```

`rejections` replays the gateway for one client, request by request, oldest first:

- A request is **rejected** when the window ending at its time already holds `limit` **accepted** requests of that client. Otherwise it is accepted.
- Rejected requests do not count towards later windows.
- Return the times of the rejected requests, oldest first.
- Throw `IllegalArgumentException` when `limit` or `windowMs` is less than 1.

`rejectionCounts` returns, for every client with at least one rejection, how many of its requests were rejected, sorted by client name. Clients with none are left out.

Run **Phase 2 tests**.
