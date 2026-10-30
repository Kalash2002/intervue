# Happy Number

Start from a positive integer and replace it by the sum of the squares of its decimal digits, then do the same to the result, and so on. For some starting numbers the chain reaches 1, and stays there, since 1 squared is 1: those numbers are called happy. For every other number the chain never reaches 1 and goes round a loop of the same numbers forever.

Implement `DigitChain.isHappy(int n)` in `DigitChain.java` and return `true` if the chain that starts at `n` reaches 1, and `false` if it never does.

## Example 1

**Input:** `n = 356`
**Output:** `true`
**Explanation:** 3² + 5² + 6² = 9 + 25 + 36 = 70. The chain goes on 70, 49, 97, 130, 10 and then 1² + 0² = 1.

## Example 2

**Input:** `n = 85`
**Output:** `false`
**Explanation:** The chain is 85, 89, 145, 42, 20, 4, 16, 37, 58, and 5² + 8² = 89 again, so from there it repeats the same eight numbers forever, none of them 1.

## Example 3

**Input:** `n = 1111111`
**Output:** `true`
**Explanation:** Seven 1s give 7 in one step, and 7 is happy: 49, 97, 130, 10, 1. A chain that reaches a single digit other than 1 is not finished yet.

## Constraints

- `1 <= n <= 2^31 - 1`

## Follow-up

Can you do it with only a constant amount of extra memory, without a set?
