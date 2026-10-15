# How a strong round goes: Duplicate Order Detector

**Read this first.** This is one good way through the problem, not the only one. The complexity figures describe the reference solution. The practice links go to other sites and to different questions that use the same technique; they are not this question. The prompts are written for interview mode, where the assistant does what you specify and nothing more.

## Minutes 0 to 5: read before you run

Say aloud: "I'll read the records, the key and the detector before I run anything."

1. `Order.java`: an id, a customer, a SKU, an amount in paise and a minute. `key()` builds an `OrderKey` from customer, SKU and amount; the minute is not part of the basket.
2. `OrderKey.java`: three fields, a `hashCode` over all three, and an `equals` method. Read its parameter type carefully, and look for `@Override` on each method.
3. `DuplicateDetector.java`: `exactRepeats` adds each key to a `HashSet` and reports the orders whose key was already there. `distinctBaskets` uses the same set.
4. `data/orders.csv`: ten orders. RIYA's kettle appears three times, at minutes 3, 4 and 300.
5. `Phase1Tests.java`: five tests; the first and third expect no repeats at all.

Only now run the phase 1 tests.

## Prompt: understand the codebase fast

```text
Read-only question. For this project give me:
1. One line per class: what it owns and which other classes it calls.
2. In OrderKey, does equals override Object.equals or overload it? Which method does
   HashSet.add call to compare two keys? Cite the line numbers.
3. Which fields of Order are part of the basket key and which are not.
Keep it under 150 words. Mark anything you are inferring.
```

Check item 2 against the code yourself: the parameter type is `OrderKey`, not `Object`. That is the whole of phase 1.

## Phase 1: the empty report (minutes 5 to 10)

### Reproduce

Run phase 1 tests. `differentBasketsAreNotRepeats` and `aDifferentAmountIsADifferentBasket` pass. Every test that expects a repeat fails with an empty list.

### Narrow it down, aloud

"The set never finds a match, even for two identical baskets. The hash codes agree, so the keys land in the same bucket; the set must then be calling an equality check that says no. That is `equals(Object)`, and this class never wrote one."

### The cause

`public boolean equals(OrderKey other)` is an overload, a new method. `HashSet` calls `equals(Object)`, which is still `Object`'s identity check, so two different key objects are never equal.

### The fix

```java
@Override
public boolean equals(Object o) {
    return o instanceof OrderKey other
            && customer.equals(other.customer)
            && sku.equals(other.sku)
            && amountMinor == other.amountMinor;
}
```

Delete the overload. Run phase 1 tests again; all five pass. Turning `OrderKey` into a `record` is an equally good fix: records generate both methods correctly.

### Say this

"It compiled because an overload is a legal method; `@Override` on it would have been a compile error. The tests without repeats passed because an always-empty report is right for them."

## Phase 2: suspected duplicates and the worst SKUs (minutes 10 to 30)

### Analysis before any prompt

- For each order I need the minute of the **most recent** earlier copy of its basket, not the first one.
- The gap is strict: exactly `windowMinutes` apart is not suspected.
- The SKU report counts repeats in the `exactRepeats` sense, any earlier copy however old, then sorts by count descending and SKU ascending.

Work the sample by hand with the 15-minute window `sampleFileSuspects` uses: O3 repeats O1 one minute later, suspected. O6 repeats O4 thirty minutes later, not suspected. O8 repeats O7 three minutes later, suspected. O9 is compared with O3 (the most recent copy), 296 minutes, not suspected. Result: O3 and O8. With a 31-minute window O6 joins them.

### Decide the approach yourself

"One forward pass with a map from basket to the minute of its latest copy. `put` returns the previous value, so reading and updating are one call."

### Prompt

```text
Implement DuplicateDetector.suspectedDuplicates(int windowMinutes) as one forward pass over
log.all() with a HashMap<OrderKey, Integer> of the latest minute per key. For each order,
previous = map.put(key, minute); add the order id when previous != null and
minute - previous < windowMinutes. Throw IllegalArgumentException for windowMinutes <= 0.
Do not change any other method.
```

Then:

```text
Implement mostDuplicatedSkus(int k): one pass with a HashSet<OrderKey> of seen keys; when add
returns false, count one repeat for order.sku() in a HashMap<String, Integer>. Return the
SkuCount list sorted by count descending, then sku ascending, limited to k. Throw
IllegalArgumentException for a negative k. SKUs with no repeats never enter the map.
```

### Check the reply for

- `<` and not `<=` for the window.
- The map updated on every order, not only on repeats; otherwise the next copy is compared with an old one and `comparesWithTheMostRecentCopy` fails.
- A sort with the SKU name as tie-break. Count alone leaves ties in hash order.
- Phase 1 "fixed" by replacing the set with a `List` and `contains`. That works and hides the real bug at quadratic cost.

Run phase 2 tests after each method.

## Phase 3: sale day (minutes 30 to 38)

### Read the tests first

Both tests run one report on 400,000 orders with a time limit. Say how many times your code looks at each order: "Once, with one hash lookup each."

### What to do

If phase 2 used the one-pass map, both tests already pass. If either times out, find the backward scan (for each order, walking back through earlier orders) and replace it with a map lookup. Run all three phases.

## The technique: hashing for "seen before"

**The idea.** A hash set answers "have I seen this before?" in O(1) on average, and a hash map answers "what did I record when I last saw it?". One forward pass with either turns an all-pairs comparison into linear work.

**How to recognise it.** Duplicates, first repeat, pairs that sum to a target, "most recent occurrence within k", counting frequencies, grouping by a key.

**The contract that makes it work.** A key used in a hash collection needs `equals(Object)` and `hashCode` that agree: equal objects must have equal hash codes. Get `hashCode` wrong and equal keys land in different buckets; get `equals` wrong, as here, and they never match even in the same bucket. Keys should also be immutable, or a key changed after insertion is lost in the wrong bucket.

**The two decisions that cause bugs.** Whether to compare with the first or the most recent occurrence: store the latest and update on every element. And whether the window is strict.

| Approach | Time for n orders | Extra space |
| --- | --- | --- |
| For each order, scan earlier orders | O(n squared) | O(1) |
| Hash map of latest minute per basket | O(n) on average | O(distinct baskets) |
| Sort by key then minute, scan neighbours | O(n log n) | O(n), and loses log order |

**Practise the technique**

These open inside the app under DSA.

- [Contains Duplicate](dsa://contains-duplicate): `exactRepeats` as a yes or no.
- [Contains Duplicate II](dsa://contains-duplicate-ii): a repeat within a window of positions, the closest match to `suspectedDuplicates`.
- [Top K Frequent Elements](dsa://top-k-frequent-elements): counting then ranking, as in `mostDuplicatedSkus`.

## Time budget

| Minutes | What |
| --- | --- |
| 0 to 5 | Read the key, the detector, the sample and the test names |
| 5 to 10 | Phase 1: reproduce, find the overload, fix with `@Override` |
| 10 to 14 | Phase 2 rules aloud and the sample worked by hand |
| 14 to 30 | Phase 2: both reports, a test run after each |
| 30 to 38 | Phase 3: read the tests, say the cost, run all three phases |
| 38 to 45 | Read your whole diff, answer the follow-ups aloud |

## Follow-up answers

**Why did it compile with no warning?** `equals(OrderKey)` is an overload, a new method with a different parameter type, which is legal Java. `@Override` would have turned the mistake into a compile error, which is why it belongs on every `equals`.

**The equals and hashCode contract.** Equal objects must have equal hash codes, and `equals` must take `Object`. With only `hashCode` right, equal keys share a bucket but never match. With only `equals` right, equal keys usually land in different buckets and are never compared. Either way the set holds duplicates.

**Cost.** One pass, O(n) average time, O(distinct baskets) memory for the map. The SKU report adds a sort over SKUs that have repeats, which is small.

**Two servers with skewed clocks.** Minutes from two clocks can arrive out of order or look closer than they were. Options: normalise to one clock at ingestion (a server-assigned sequence or the payment gateway's timestamp), widen the window by the known skew, or compare absolute differences so an out-of-order copy still counts. Say which error is worse for the business: missing a double charge or flagging a real second purchase.

**What the assistant got wrong.** Answer from your own session. Typical: switching to a `List` instead of fixing `equals`, adding `equals(Object)` but leaving the overload, `<=` for the window, comparing with the first copy, or sorting SKUs by count only.
