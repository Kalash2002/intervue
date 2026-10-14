# How a strong round goes: Rate Limiter Audit

**Read this first.** This is one good way through the problem, not the only one. The prompts are written for interview mode, where the assistant does what you specify and nothing more.

## Minutes 0 to 5: read before you run

1. `Request` and `RequestLog`: one list of every request; `forClient` filters the whole list each call. Note that it is linear and may matter later.
2. `LogParser`: times arrive as seconds with three decimals and become milliseconds in `parseMillis`. Read that one line slowly.
3. `RateAudit.countInWindow`: start excluded, end included, exactly as the brief says.
4. `data/requests.csv`: eleven requests, two clients.

## Phase 1: one millisecond early (minutes 5 to 12)

Run phase 1. The window tests pass and the parsing tests fail on `1.003` and `2.006` but not on `0.120`. Say: "The window code is right; the input is wrong for some values. Multiplying a double by 1000 and casting truncates, and these decimals are not exact in binary."

```text
Read-only question. What does (long) (Double.parseDouble("2.006") * 1000) evaluate to in Java,
and why? Give the exact double nearest to 2.006 and the product.
```

Check the claim yourself by printing the product. Fix with `BigDecimal.movePointRight(3).longValueExact()`, and explain why exactness matters for an audit.

## Phase 2: replay the limiter (minutes 12 to 30)

State the rule before coding: "Only accepted requests occupy the window; a rejected request is invisible to later decisions. The window is (t - w, t]." Walk the sample by hand for alpha with a limit of 2: 1003 is rejected, 2006 and 2010 are rejected, 2900 is accepted because 1900 is exactly one window earlier.

```text
Implement RateAudit.rejections(client, limit, windowMs) with an ArrayDeque of accepted
times: before each request, remove from the front while front <= t - windowMs; reject when
the deque holds limit times, else add t. Throw IllegalArgumentException when limit or
windowMs is below 1. Then rejectionCounts with a TreeMap, leaving out zero counts.
```

Check the `<=` in the expiry loop and that rejected times are never added to the deque.

## Phase 3: a full day (minutes 30 to 40)

Run phase 3. With the deque, the noisy client passes. The many-clients test may still time out: read `RequestLog.forClient` and say "40,000 clients times 320,000 requests is 13 billion comparisons." Index times by client in `RequestLog.add`.

## Complexity

`rejections` is O(n) per client with at most `limit` times held; `rejectionCounts` is O(total requests) once the log is indexed.

## Follow-up answers

- 2.006 is stored as 2.00599999…, so the product is 2005.99999… and the cast drops it to 2005.
- Counting rejected requests would lock a client out for as long as it keeps retrying, which is not what a sliding-window limiter promises.
- Four machines with separate windows let a client make up to four times the limit; share the counter (for example in Redis) or route each client to one machine.
