# Grader notes: Rate Limiter Audit

**Phase 1 bug.** `LogParser.parseMillis` computes `(long) (Double.parseDouble(s) * 1000)`. Values such as 1.003, 2.006 and 4.012 have no exact binary representation; the product lands just below the integer and the cast truncates, so those requests are one millisecond early. Only 48 of the first 5,000 millisecond values are affected, which is why most of the log looks right. Fix: `new BigDecimal(s).movePointRight(3).longValueExact()`. `Math.round` also passes the tests and is acceptable if the candidate explains why rounding is safe for three decimals.

**Phase 2.** The limiter counts accepted requests only. The intended structure is a deque of accepted times with expired times dropped from the front (`<= t - windowMs`, because the window start is excluded). Counting rejected requests fails `rejectedRequestsDoNotCount`; using `<` when expiring fails `theWindowStartIsExcluded`. `rejectionCounts` must leave out clients with zero and sort by name (`TreeMap`).

**Phase 3.**
- Workload 1 (one client, 500,000 requests): counting the accepted list for each request is O(n^2); the deque is O(n).
- Workload 2 (about 40,000 clients): slow because `RequestLog.forClient` scans every request for every client. The fix is in `RequestLog`: index times by client on `add`. Strong candidates read `RequestLog` before tuning `RateAudit`.

**Common assistant mistakes to watch for.** Fixing phase 1 with `Math.ceil` (wrong for exact values); counting rejected requests in the window; expiring with `<` instead of `<=`; a fixed-window counter (resets every second) instead of a sliding window; optimising only `RateAudit` for workload 2.
