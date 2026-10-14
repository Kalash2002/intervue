# How a strong round goes: Duplicate Order Detector

**Read this first.** This is one good way through the problem, not the only one. The prompts are written for interview mode, where the assistant does what you specify and nothing more.

## Minutes 0 to 5: read before you run

Say aloud: "I'll read the records, the key and the detector before I run anything."

1. `Order.java`: an order has an id, a customer, a SKU, an amount in paise and a minute. `key()` builds an `OrderKey` from customer, SKU and amount.
2. `OrderKey.java`: three fields, a `hashCode` over all three, and an `equals` method. Read its signature carefully.
3. `DuplicateDetector.java`: `exactRepeats` adds each key to a `HashSet` and reports the orders whose key was already there.
4. `Phase1Tests.java`: five tests; the first and third have no repeats at all.

## Prompt: a focused question

```text
Read-only question. In OrderKey, does equals override Object.equals or overload it?
Which method does HashSet.add call to compare two keys? Answer in three sentences and
cite the line numbers.
```

Check the answer against the code: the parameter type is `OrderKey`, so it is an overload.

## Phase 1: the empty report (minutes 5 to 10)

Run phase 1 tests. The tests with no repeats pass; every test that expects a repeat fails with an empty list. Say: "The set never finds a match, so equality is broken, not the loop." Change `equals` to take `Object`, add `@Override`, and run again. Explain why the compiler stayed quiet: an overload is a legal new method.

## Phase 2: suspected duplicates (minutes 10 to 30)

Say the rule before writing code: "For each order I need the minute of the most recent earlier copy of its basket. A map from key to last minute gives me that in one pass." Point out the two edge cases from the brief: exactly the window apart is not suspected, and the comparison is with the most recent copy.

```text
Implement DuplicateDetector.suspectedDuplicates(int windowMinutes) as one forward pass
over log.all() with a HashMap<OrderKey, Integer> of the latest minute per key. Throw
IllegalArgumentException for windowMinutes <= 0. Use a strict less-than for the gap.
Do not change any other method.
```

Read the reply line by line: check the strict comparison and that the map is updated on every order, not only on repeats. Then do `mostDuplicatedSkus` yourself or with a similar prompt; check the tie-break by SKU name.

## Phase 3: sale day (minutes 30 to 40)

Run phase 3. If your phase 2 code used one pass with a map, both tests already pass. If either times out, find the backward scan and replace it with a map lookup.

## Complexity

One pass, O(n) time and O(number of baskets) memory for each report; the final sort is over SKUs only.

## Follow-up answers

- The overload compiled because a method with a different parameter type is a new method; `@Override` would have turned it into a compile error.
- `equals` and `hashCode` must agree: equal objects need equal hash codes. Here the hash codes agreed and `equals(Object)` was never written.
- Clock skew between servers: compare with a tolerance, or order by a server-assigned sequence rather than wall-clock minutes.
