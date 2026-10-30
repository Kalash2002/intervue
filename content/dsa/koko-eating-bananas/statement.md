# Koko Eating Bananas

Koko faces a row of banana piles, where `piles[i]` is the number of bananas in pile `i`, and has `hours` hours before the keepers come back. She picks one eating speed, a whole number of bananas per hour, and keeps it. Each hour she works on a single pile: she eats up to `speed` bananas from it, and if the pile has fewer left, she finishes it and rests for the remainder of that hour. She never starts a second pile in the same hour.

Return the smallest speed at which every pile is finished within `hours` hours. Implement `EatingSpeed.minSpeed(int[] piles, int hours)` in `EatingSpeed.java`.

The limit on `hours` here is higher than in the best-known version of this question: up to `2 * 10^9` rather than `10^9`, so the hours needed at a slow speed can pass what an `int` holds.

## Example 1

**Input:** `piles = [7, 15, 4, 11]`, `hours = 8`
**Output:** `6`
**Explanation:** At 6 bananas an hour the piles take 2, 3, 1 and 2 hours, 8 in all. At 5 they take 2, 3, 1 and 3, which is 9 and too many.

## Example 2

**Input:** `piles = [7, 15, 4, 11]`, `hours = 4`
**Output:** `15`
**Explanation:** Four piles in four hours leaves one hour per pile, so the speed must cover the largest pile, 15 bananas.

## Example 3

**Input:** `piles = [1000000000, 1000000000, 1000000000]`, `hours = 2000000000`
**Output:** `2`
**Explanation:** At 1 banana an hour the piles need 3,000,000,000 hours, more than allowed and more than an `int` can count. At 2 they need 1,500,000,000.

## Constraints

- `1 <= piles.length <= 10^4`
- `1 <= piles[i] <= 10^9`
- `piles.length <= hours <= 2 * 10^9`

## Follow-up

Can you find the speed in O(n log m) time, where m is the largest pile?
