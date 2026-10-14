# Grader notes: Duplicate Order Detector

**Phase 1 bug.** `OrderKey.equals(OrderKey)` overloads instead of overriding `equals(Object)`. `HashSet` calls `equals(Object)`, which is still identity from `Object`, so no two key objects are ever equal and `exactRepeats` is always empty. `hashCode` is correct, which makes the bug harder to see. Fix: `equals(Object)` with a type check and `@Override`. Tests with no repeats pass on the buggy code, which is why "some tests still passed".

**Phase 2.** The intended approach is one forward pass with `Map<OrderKey, Integer>` holding the latest minute per basket; `put` returns the previous value, so read and update are one call. The window is strict: exactly `windowMinutes` apart is not suspected. Comparing with the first copy instead of the most recent one fails `comparesWithTheMostRecentCopy`. The SKU report counts repeats (not orders), sorts by count descending then SKU ascending, and leaves out SKUs with no repeats.

**Phase 3.** Both tests run on 400,000 orders. Any backward scan is O(n^2) and times out; the map-based pass is O(n). A solution that is fast for `suspectedDuplicates` but builds the SKU counts by searching a list stays slow on the second test only if the list is long; with 60 SKUs it is fine, so the decisive cost is the "seen before" check.

**Common assistant mistakes to watch for.** Fixing phase 1 by changing `HashSet` to a `List` with `contains` (works, but quadratic and hides the real bug); adding `equals(Object)` without removing or delegating the overload; using `<=` for the window; comparing with the first copy; sorting SKUs only by count so ties come out in hash order.
