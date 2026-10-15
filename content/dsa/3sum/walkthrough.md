# How a strong round goes: 3Sum

## Approach

Checking all triples is cubic. Fixing the first value turns the rest into a known problem: find two values that add up to the negative of the first. If the array is sorted that is the two-position walk from the sorted pair question, so the whole thing is O(n squared). Sorting also solves the other difficulty, duplicates. Equal values sit next to each other, so I can skip them. For the first value I skip any that equals the one before it, because it would find the same pairs again. Inside the pair search, after a match I move both positions in, and then past any equal values, so the same pair is not reported twice. I also stop as soon as the first value is positive: the values after it are at least as large, so the sum cannot reach zero. An array of all zeros shows why the skipping matters: without it the result would be full of copies of `[0, 0, 0]`.

```java
static List<List<Integer>> zeroSum(int[] nums) {
    int[] a = nums.clone();
    Arrays.sort(a);
    List<List<Integer>> result = new ArrayList<>();
    for (int i = 0; i < a.length - 2 && a[i] <= 0; i++) {
        if (i > 0 && a[i] == a[i - 1]) {
            continue; // the same first value would repeat triples already found
        }
        int left = i + 1;
        int right = a.length - 1;
        while (left < right) {
            int sum = a[i] + a[left] + a[right];
            if (sum < 0) {
                left++;
            } else if (sum > 0) {
                right--;
            } else {
                result.add(List.of(a[i], a[left], a[right]));
                left++;
                right--;
                while (left < right && a[left] == a[left - 1]) {
                    left++; // skip repeats of the pair's left value
                }
            }
        }
    }
    return result;
}
```

## Complexity

| Approach | Time | Extra space |
| --- | --- | --- |
| Check every group of three, remove repeats with a set | O(n cubed) | O(k) for k triples |
| Sort, fix one value, two positions for the rest | O(n squared) | O(1) besides the sort and the output |

## Where it shows up

Finding groups of values that cancel out is a reconciliation task: three ledger entries that net to zero, or three adjustments that offset one another. The idea of sorting once and then fixing one choice while two cursors handle the rest also underlies "k-sum" searches and many pair-matching jobs over sorted data.

## Follow-up answers

**How would you change it to find triples that add up to any given target?** Search for a pair that sums to `target - a[i]` instead of the negative of `a[i]`. The early stop on a positive first value only works for a target of zero, so drop it, or replace it with a check suited to the target.

**How would you solve the closest-to-target version, where no exact triple need exist?** Keep the same loops and track the sum closest to the target as you go. Move the left position when the sum is below the target and the right one when it is above, and return early on an exact match.

**How would you generalise to groups of four values?** Fix two values with two nested loops and run the two-position walk on the rest. That is O(n cubed), and the same skipping of equal values applies at every level.
