# How a strong round goes: Rate Limiter Audit

**Read this first.** This is one good way through the problem, not the only one. The complexity figures describe the reference solution. The DSA reference at the end names the data structure behind the solution, shows it in plain Java, gives its cost and lists the questions to practise, which open inside the app. The prompts are written for interview mode, where the assistant does what you specify and nothing more.

## Minutes 0 to 5: read before you run

Say aloud: "I'll read how the log is parsed and stored, and the window check, before I run anything."

1. `Request` and `RequestLog`: one list of every request, oldest first. `forClient` filters the **whole** list on every call. Note it: it is linear per call and may matter later.
2. `LogParser.parseMillis`: seconds with up to three decimals become milliseconds. Read that one line slowly: a `double`, times 1000, cast to `long`.
3. `RateAudit.countInWindow`: start excluded, end included, `(end - w, end]`, exactly as the brief says.
4. `data/requests.csv`: eleven requests from two clients; alpha has eight.
5. `Phase1Tests.java`: six tests, three about windows and three about parsing.

Only now run the phase 1 tests.

## Prompt: understand the codebase fast

```text
Read-only question. For this project give me:
1. One line per class: what it owns and which other classes it calls.
2. The path a line of requests.csv takes from text to a Request, method by method, with the
   type of the time value at each step.
3. How the cost of RequestLog.forClient grows with the number of requests and clients.
Keep it under 150 words. Mark anything you are inferring.
```

Item 2 points at phase 1 (the time is a `double` for one step). Item 3 is half of phase 3.

## Phase 1: one millisecond early (minutes 5 to 12)

### Reproduce

Run phase 1 tests. The window tests pass, and so does `parsesWholeSecondsAndShortDecimals`. `parsesEveryMillisecondExactly` fails on 1.003, 2.006 and 4.012, and both sample tests fail because alpha's 2.006 becomes 2005.

### Narrow it down, aloud

"The window code is right; the input is wrong for some values only. 0.120 parses fine, 2.006 does not. Most decimal fractions have no exact binary form, so 2.006 is stored as a double just below it, times 1000 is just below 2006, and the cast truncates to 2005."

If you want confirmation, ask a specific question and then check it yourself by printing the value:

```text
In Java, what does (long) (Double.parseDouble("2.006") * 1000) evaluate to, and why? Give the
product before the cast.
```

### The fix

```java
return new BigDecimal(seconds).movePointRight(3).longValueExact();
```

`BigDecimal` keeps the decimal text exactly, and `longValueExact` refuses a fourth decimal instead of silently dropping it. `Math.round(Double.parseDouble(seconds) * 1000)` also passes; if you choose it, say why rounding is safe for three decimals. `Math.ceil` is wrong for values that are exact.

### Say this

"Only some timestamps are affected: the ones whose double sits just below the decimal value. An audit needs exact times, so I parse decimal text as a decimal, not as binary floating point."

## Phase 2: replay the limiter (minutes 12 to 30)

### Analysis before any prompt

- Only **accepted** requests occupy the window. A rejected request is invisible to later decisions.
- The window ending at t is `(t - w, t]`: a request exactly one window earlier has expired.
- Requests at the same millisecond each count.

Walk alpha by hand with a limit of 2 and a 1,000 ms window: 120 and 480 are accepted; 1003 is rejected (two accepted in the window); 1250 is accepted (only 480 is still in the window); 1900 is accepted; 2006 and 2010 are rejected (1250 and 1900); 2900 is accepted because 1900 is exactly one window earlier. Rejected: 1003, 2006, 2010.

### Decide the approach yourself

"A queue of accepted times. Before each request I drop expired times from the front; if the queue still holds `limit` times I reject, otherwise I add the time. Each time enters and leaves the queue once."

### Prompt

```text
I'll model the limiter as a sliding window: a deque of the accepted times of one client. For each
request, drop the times at the front that have left the window (the window start is excluded, so
expire with less-or-equal), then reject if the deque is full, else accept and push. Implement
rejections(client, limit, windowMs) that way and return the rejected times; a limit or window
below 1 is an IllegalArgumentException. Only this method.
```

Then:

```text
rejectionCounts(limit, windowMs): run rejections for every client the log knows and report the
ones with a non-zero count in a sorted map. Validate the arguments the same way even when the log
is empty.
```

### Check the reply for

- `<=` when expiring. `<` keeps a request that is exactly one window old and fails `theWindowStartIsExcluded`.
- Rejected times never added to the deque. Counting them fails `rejectedRequestsDoNotCount`.
- A sliding window, not a fixed counter that resets every second.
- Validation in `rejectionCounts` before the loop.

Run phase 2 tests after each method.

## Phase 3: a full day (minutes 30 to 40)

### Read the two tests first

The brief warns that the two workloads are slow for different reasons. Say each one aloud:

- One noisy client with 500,000 requests: this stresses `rejections`. Counting the accepted list for each request is quadratic; the deque is linear and already passes.
- 40,000 clients with eight requests each: this stresses `RequestLog.forClient`, which scans all 320,000 requests for every client. That is about 13 billion comparisons, and none of them are in `RateAudit`.

### Prompt

```text
forClient scans the whole log for every client. Keep a per-client list of times filled in add,
with clients in first-seen order, so forClient and clients() are direct lookups. Keep the
out-of-order check. Only this class.
```

### Check the reply for

- `forClient` on an unknown client returns an empty list, not `null`.
- `clients()` still in first-seen order.
- The out-of-order check still in `add`.

Run all three phases.

## DSA reference

### Theory

![A sliding window with a deque: accepted times enter at the back; before each request, times older than the window leave from the front. With a limit of 3, the request at 2,150 is rejected and the one at 2,900 is accepted.](figures/window.svg)

**The idea.** When every question is about "the last w milliseconds", keep only the events still inside the window in a queue. New events join at the back, expired ones leave from the front. Each event is added and removed once, so a whole replay is linear, and the queue never holds more than the limit.

**How to recognise it.** Rate limiting, "requests in the last minute", hit counters, moving averages, "longest substring with at most k ...", maximum in every window of size k.

**The two decisions that cause bugs.** Which end of the window is open: here the start is excluded, so expire with `<=`. And what counts: only accepted requests occupy a limiter's window.

**Floating point is not decimal.** Values such as 2.006 have no exact binary form. For money and timestamps written as decimal text, parse with `BigDecimal` or as integers.

### Code

The structure on its own, outside this codebase; the reference solution is this shape with the problem's names.

```java
/** Times rejected by a limiter that allows `limit` accepted requests per sliding window. */
static List<Long> rejected(List<Long> times, int limit, long windowMs) {
    Deque<Long> accepted = new ArrayDeque<>();
    List<Long> rejected = new ArrayList<>();
    for (long t : times) {
        while (!accepted.isEmpty() && accepted.peekFirst() <= t - windowMs) {
            accepted.pollFirst();                      // left the window; the start is excluded
        }
        if (accepted.size() == limit) rejected.add(t); // only accepted requests occupy the window
        else accepted.addLast(t);
    }
    return rejected;
}
```

### Complexity

| Approach | Time for one client with n requests | Memory at once |
| --- | --- | --- |
| Count accepted requests in the window for each request | O(n squared) | O(n) |
| Deque of accepted times | O(n) | O(limit) |
| Fixed one-second buckets | O(n) | O(1), but a burst at a boundary gets twice the limit |

### Practise the technique

These open inside the app under DSA.

- [Number of Recent Calls](dsa://number-of-recent-calls): a queue of times with expiry from the front, the closest match.
- [Design Hit Counter](dsa://design-hit-counter): the same idea as a class.
- [Sliding Window Maximum](dsa://sliding-window-maximum): a harder deque window.

## Time budget

| Minutes | What |
| --- | --- |
| 0 to 5 | Read the log, the parser and the window check |
| 5 to 12 | Phase 1: reproduce, reason to floating point, exact parse |
| 12 to 16 | Phase 2 rules aloud and alpha replayed by hand |
| 16 to 30 | Phase 2: `rejections`, then `rejectionCounts`, a run after each |
| 30 to 34 | Phase 3: read both tests, name what each stresses |
| 34 to 40 | Phase 3: index `RequestLog` by client, run all three phases |
| 40 to 45 | Read your whole diff, answer the follow-ups aloud |

## Follow-up answers

**Why is 2.006 times 1000 not 2006?** The double nearest to 2.006 is slightly below it, so the product is slightly below 2006 and the cast to `long` truncates to 2005. Any value whose nearest double is below the decimal is affected; 1.003 and 4.012 are others. Values with exact binary forms, such as 7.5, are not.

**Why must rejected requests not count?** A sliding-window limiter promises at most `limit` accepted requests per window. If rejections counted, a client that keeps retrying would keep its own window full and stay locked out for as long as it retries, far longer than one window.

**Cost.** `rejections` is O(n) for n requests: each time enters and leaves the deque once. It holds at most `limit` times at once. `rejectionCounts` is O(total requests) once `RequestLog` is indexed by client.

**Four machines with their own windows.** A client spread across four machines can make up to four times the limit. Options: a shared counter (for example in Redis, with an atomic increment and expiry), routing each client to one machine by a hash of its id, or giving each machine a quarter of the limit and accepting some unfairness.

**What the assistant got wrong.** Answer from your own session. Typical: `Math.ceil` in phase 1, counting rejected requests, `<` when expiring, a fixed-window counter, or tuning only `RateAudit` for the many-clients workload.
