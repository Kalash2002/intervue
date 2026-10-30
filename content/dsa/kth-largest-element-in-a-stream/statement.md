# Kth Largest Element in a Stream

Values arrive one at a time, and after each one you must say which value is the k-th largest of everything seen so far. Repeats count separately: if the three largest values are 9, 9 and 4, the second largest is 9 and the third is 4.

Implement the class `KthLargest` in `KthLargest.java`:

- `KthLargest(int k, int[] nums)` starts the stream with the values in `nums`, in any order.
- `int add(int value)` adds `value` to the stream and returns the k-th largest value seen so far, the values from `nums` included.

`nums` holds at least `k - 1` values, so every `add` sees at least `k` values. The tests make up to 100,000 `add` calls on one object that already holds 100,000 values, so sorting everything on every call is too slow. In the examples, the input is the constructor call followed by the `add` calls, and the output lists what each `add` returned, in order.

## Example 1

**Input:** `["KthLargest(2, [5, -1, 8])", "add(3)", "add(9)", "add(10)", "add(-4)"]`
**Output:** `[5, 8, 9, 9]`
**Explanation:** After 3 arrives the values are 8, 5, 3, -1, so the second largest is 5. Then 9 and 10 each push it up, to 8 and then 9, and -4 is too small to change anything.

## Example 2

**Input:** `["KthLargest(3, [7, 7])", "add(7)", "add(12)", "add(12)", "add(15)"]`
**Output:** `[7, 7, 7, 12]`
**Explanation:** The three 7s are three separate values. After 15 arrives the three largest are 15, 12 and 12, so the third largest is 12.

## Example 3

**Input:** `["KthLargest(1, [])", "add(-6)", "add(-20)", "add(0)"]`
**Output:** `[-6, -6, 0]`
**Explanation:** With `k = 1` the answer is the largest value so far. The stream starts empty, which is allowed because `nums` must hold only `k - 1 = 0` values.

## Constraints

- `1 <= k <= 10^5`
- `0 <= nums.length <= 10^5`
- `nums.length >= k - 1`
- `-10^9 <= nums[i] <= 10^9`
- `-10^9 <= value <= 10^9`
- `1 <= number of add calls <= 10^5`

## Follow-up

What if values could also be removed from the stream?
