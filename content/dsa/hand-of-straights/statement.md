# Hand of Straights

You hold a hand of numbered cards: `cards[i]` is the number on card `i`, the cards come in no particular order, and numbers may repeat. A straight is a group of exactly `size` cards whose numbers are consecutive, such as 17, 18 and 19 when `size` is 3.

Implement `Hand.canSplit(int[] cards, int size)` in `Hand.java` and return `true` if the whole hand can be split into straights, with every card in exactly one of them, and `false` otherwise.

## Example 1

**Input:** `cards = [32,36,31,33,30,32,35,31,33,34,31,32]`, `size = 3`
**Output:** `true`
**Explanation:** The straights 30, 31, 32, then 31, 32, 33 twice, then 34, 35, 36 use every card once. A straight started at the first card, 32, would take the only 34 and leave 35 and 36 without one, and cutting the sorted hand into blocks of three gives 30, 31, 31, which is not a straight.

## Example 2

**Input:** `cards = [10,12,11,13,11,14]`, `size = 3`
**Output:** `false`
**Explanation:** The 10 can only be the lowest card of a straight, so 10, 11, 12 is forced. That leaves 11, 13 and 14, and the 11 would need the 12 that is already used.

## Example 3

**Input:** `cards = [4,4,4]`, `size = 3`
**Output:** `false`
**Explanation:** The numbers in a straight are consecutive, so no two of its cards share a number, and three 4s cannot make one.

## Constraints

- `1 <= cards.length <= 10^5`
- `0 <= cards[i] <= 10^9`
- `1 <= size <= cards.length`

## Follow-up

What if you also had to return the straights?
