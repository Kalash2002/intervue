## Level 1

Replay the requests in order and keep only what the limiter itself would remember: the accepted requests that are still inside the window. A rejected request is never remembered.

## Level 2

Keep the accepted times in a queue, oldest at the front. Before deciding on a request at time t, drop accepted times that are at or before t minus the window. If the queue already holds `limit` times, reject; otherwise accept and add t at the back.

## Level 3

1. Check the arguments first: a limit or a window below 1 is an error.
2. Use an `ArrayDeque` of accepted times and walk the client's times oldest first.
3. Remove from the front while the front time is less than or equal to `t - windowMs`; the window start is excluded.
4. Compare the queue size with the limit, then record the rejection or add the time.
5. For `rejectionCounts`, call `rejections` for each client, keep only positive counts, and store them in a `TreeMap` so they come out sorted by name.
