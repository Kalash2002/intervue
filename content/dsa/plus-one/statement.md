# Plus One

A counter keeps a non-negative integer as an array of its decimal digits, most significant digit first, such as `[5, 8, 0, 6]` for 5806. The number can be up to 100 digits long, far more than a `long` holds. It has no leading zeros, except for the number 0 itself, which is `[0]`.

Implement `Odometer.plusOne(int[] digits)` in `Odometer.java` and return the digits of the number plus one, in the same form. You may change `digits` and return it, or return a new array.

## Example 1

**Input:** `digits = [5, 8, 0, 6]`
**Output:** `[5, 8, 0, 7]`
**Explanation:** 5806 + 1 = 5807. The last digit is below 9, so only it changes.

## Example 2

**Input:** `digits = [3, 9, 9, 9]`
**Output:** `[4, 0, 0, 0]`
**Explanation:** 3999 + 1 = 4000. Each 9 turns into 0 and carries one to the left, until the 3 takes the carry and becomes 4.

## Example 3

**Input:** `digits = [9, 9, 9, 9, 9, 9]`
**Output:** `[1, 0, 0, 0, 0, 0, 0]`
**Explanation:** 999999 + 1 = 1000000. Every digit is 9, so the carry runs off the front and the answer is one digit longer than the input.

## Constraints

- `1 <= digits.length <= 100`
- `0 <= digits[i] <= 9`
- `digits[0] != 0 || digits.length == 1`

## Follow-up

Can you stop as soon as the carry is used up, and when do you need a new array?
