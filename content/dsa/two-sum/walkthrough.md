# How a strong round goes: Two Sum

## Approach

I would not check every pair. For any number I am looking at, the partner I need is fixed: target minus that number. So the real question is "have I already seen that value?", and a hash map answers it in constant time on average. One pass, a map from value to position, and the lookup comes before the store so a number can never pair with itself. Equal values still work, because the second one finds the first already stored.

```java
static int[] indices(int[] nums, int target) {
    Map<Integer, Integer> seen = new HashMap<>(); // value -> position, for values before this one
    for (int i = 0; i < nums.length; i++) {
        Integer partner = seen.get(target - nums[i]);
        if (partner != null) {
            return new int[] {partner, i}; // the earlier position is the smaller one
        }
        seen.put(nums[i], i); // stored after the lookup, so a value never pairs with itself
    }
    return new int[0];
}
```

## Complexity

| Approach | Time | Extra space |
| --- | --- | --- |
| Check every pair | O(n squared) | O(1) |
| One pass with a hash map | O(n) on average | O(n) |

## Where it shows up

No coding-round problem is linked to this question. The pattern, "remember what I have seen and look up the partner I need", shows up wherever you match records by a computed key: finding a refund for a payment, pairing a debit with its credit in a ledger, or checking that two totals reconcile.

## Follow-up answers

**What changes if the array is sorted?** You no longer need the map. Put one pointer at each end; if the sum is too small move the left one right, if too big move the right one left. That is O(n) time and O(1) space.

**What if several pairs add up to the target and you must return all of them?** Keep walking after a match instead of returning. With repeated values, store a count or a list of positions per value so each pair is reported once. The output itself can be large, so the cost is O(n plus the number of pairs).

**What if the numbers do not fit in memory?** Sort them externally and use the two-pointer pass, or split by hash of the value so a number and its partner land in the same partition, then run the map version on each partition.
