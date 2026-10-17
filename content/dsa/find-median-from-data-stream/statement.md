# Find Median from Data Stream

Numbers arrive one at a time, and at any moment you may be asked for the median of all the numbers so far. The median is the middle number once they are sorted; with an even count it is the average of the two middle numbers, which can end in `.5`.

Implement the class `MedianFinder` in `MedianFinder.java` with a no-argument constructor and these methods:

- `void addNum(int num)` adds `num` to the data.
- `double findMedian()` returns the median of all numbers added so far.

The numbers can be any `int`, so adding the two middle numbers can overflow an `int`. The tests add 100,000 numbers and ask for the median after each one, so sorting all the numbers on every request is too slow. In the examples, the input is the sequence of calls on a new object, and the output lists what each `findMedian` call returned, in order.

## Example 1

**Input:** `["addNum(6)", "findMedian()", "addNum(-2)", "findMedian()", "addNum(11)", "findMedian()", "addNum(3)", "findMedian()"]`
**Output:** `[6.0, 2.0, 6.0, 4.5]`
**Explanation:** The sorted data grows as 6, then -2 6 (average 2), then -2 6 11 (middle 6), then -2 3 6 11, whose two middle numbers 3 and 6 average 4.5.

## Example 2

**Input:** `["addNum(-5)", "addNum(-8)", "findMedian()", "addNum(-8)", "findMedian()"]`
**Output:** `[-6.5, -8.0]`
**Explanation:** The average of -8 and -5 is -6.5, not -6. After a second -8 the sorted data is -8 -8 -5, and the middle one is -8.

## Example 3

**Input:** `["addNum(2147483647)", "addNum(2147483646)", "findMedian()"]`
**Output:** `[2147483646.5]`
**Explanation:** The two numbers are the largest `int` values. Their sum does not fit in an `int`, but their average does fit in a `double` exactly.

## Constraints

- `-2^31 <= num <= 2^31 - 1`
- `1 <= total number of calls <= 2 * 10^5`
- `findMedian` is only called after at least one `addNum`.

## Follow-up

What if every number were between 0 and 100?
