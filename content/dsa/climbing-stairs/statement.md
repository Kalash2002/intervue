# Climbing Stairs

A staircase has `n` steps above the floor. Each move climbs either one step or two steps. Count the different sequences of moves that start on the floor and end exactly on step `n`; two sequences are different when they differ in at least one move.

Implement `Staircase.countWays(int n)` in `Staircase.java` and return the count as a `long`. The count is returned exactly, not modulo anything: for the largest `n` allowed it still fits in a `long`, but it does not fit in an `int`.

## Example 1

**Input:** `n = 5`
**Output:** `8`
**Explanation:** Five single steps is one way; one double step among three singles can sit in four places; two doubles and one single can be ordered in three ways. 1 + 4 + 3 = 8.

## Example 2

**Input:** `n = 7`
**Output:** `21`
**Explanation:** The last move lands on step 7 from step 6 (13 ways to get there) or from step 5 (8 ways), so 13 + 8 = 21.

## Example 3

**Input:** `n = 91`
**Output:** `7540113804746346429`
**Explanation:** The largest staircase allowed; the count is close to the top of the `long` range and far past that of an `int`.

## Example 4

**Input:** `n = 1`
**Output:** `1`
**Explanation:** A single one-step move is the only way; a two-step move would overshoot.

## Constraints

- `1 <= n <= 91`

## Follow-up

What if each move could climb one, two or three steps?
