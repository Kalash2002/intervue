## Level 1

The report is empty, so the set never refuses an order as already seen. Look at how a `HashSet` decides that two keys are the same, and compare that with the methods `OrderKey` declares.

## Level 2

A hash set calls `hashCode` to find a bucket and then `equals(Object)` to compare. `OrderKey` declares an `equals` that takes an `OrderKey`, which is an overload, not an override, so the set still uses identity from `Object` and every new key object looks different.

## Level 3

1. Open `OrderKey` and find its `equals` method.
2. Change the parameter type to `Object`, check the type inside, and mark the method with `@Override` so the compiler would catch this next time.
3. Leave `hashCode` as it is: it already uses the same three fields.
4. Run Phase 1 tests; the sample file should report five repeats and five baskets.
