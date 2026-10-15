# How a strong round goes: Two Sum II: Sorted Input

## Approach

With unsorted input I would use a hash map, but a sorted array lets me do without memory. I put one position at each end. If the two values add to more than the target, the right one is too large to pair with anything at or after the left position, because the left value is already the smallest still in play, so I move the right position in. If the sum is too small, the left value is too small to pair with anything at or before the right position, so I move the left one in. Each step discards one position for good and the two positions never cross, so the loop runs at most n times. The answer is guaranteed to exist, so the loop ends when the sum matches. Two equal values are fine: they sit at different positions, and the positions stay different because `left < right`.

```java
static int[] find(int[] numbers, int target) {
    int left = 0;
    int right = numbers.length - 1;
    while (left < right) {
        int sum = numbers[left] + numbers[right];
        if (sum == target) {
            return new int[] {left, right};
        } else if (sum > target) {
            right--; // the right value is too big to pair with any remaining left value
        } else {
            left++; // the left value is too small to pair with any remaining right value
        }
    }
    throw new IllegalStateException("the input always has a pair");
}
```

## Complexity

| Approach | Time | Extra space |
| --- | --- | --- |
| Try every pair | O(n squared) | O(1) |
| Binary search for the partner of each value | O(n log n) | O(1) |
| Hash map of values seen (ignores the sorting) | O(n) | O(n) |
| Two positions moving inwards | O(n) | O(1) |

## Where it shows up

Combining two cursors over sorted data is a standard tool: a merge join in a database walks two sorted inputs in step, and a price lookup such as "which two items in this sorted list fit the budget exactly" is this question. It beats a hash map when memory is tight or the data is already sorted on disk.

## Follow-up answers

**What if there could be several valid pairs and you had to return all of them?** Do not stop on a match: record the pair, then move both positions inwards. With repeated values, skip over equal values after a match so the same pair of values is not recorded twice, or count them if you need the number of position pairs.

**How would you do it with a binary search for each value, and how does that compare?** For each position `i`, binary search the rest of the array for `target - numbers[i]`. That is correct and uses no extra memory, but it costs O(n log n) against O(n) for the two positions.

**How would the answer change if the array were not sorted?** The shrinking argument no longer holds, so I would use a hash map from value to position and look up the partner of each value as I go, which is O(n) time and O(n) space. Or I would sort a copy together with the original positions, at O(n log n).
