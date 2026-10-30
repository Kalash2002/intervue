# Last Stone Weight

You have a pile of stones, each with a positive whole-number weight. The game is played in turns. Each turn, take the two heaviest stones and smash them together:

- If they weigh the same, both are destroyed.
- Otherwise the lighter one is destroyed and the heavier one loses the lighter one's weight, then goes back on the pile.

The game ends when at most one stone is left. If two stones tie for second heaviest, it does not matter which one you take, since they weigh the same.

Implement `LastStone.lastWeight(int[] stones)` in `LastStone.java`. Return the weight of the stone that is left, or `0` if none is left. The pile can hold 200,000 stones, so looking through every stone on every turn is too slow.

## Example 1

**Input:** `stones = [6, 3, 9, 4]`
**Output:** `2`
**Explanation:** 9 and 6 leave a stone of 3, so the pile is 4, 3, 3. Then 4 and 3 leave 1, so the pile is 3, 1. Then 3 and 1 leave 2, the last stone.

## Example 2

**Input:** `stones = [5, 5, 8, 2]`
**Output:** `0`
**Explanation:** 8 and 5 leave 3, then 5 and 3 leave 2, and the two stones of 2 destroy each other, so nothing is left.

## Example 3

**Input:** `stones = [40]`
**Output:** `40`
**Explanation:** A single stone never takes part in a turn, so it is the stone left.

## Constraints

- `1 <= stones.length <= 2 * 10^5`
- `1 <= stones[i] <= 10^9`

## Follow-up

Can you do better than a heap when every weight is at most 1,000?
