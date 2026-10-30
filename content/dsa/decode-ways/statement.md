# Decode Ways

A message of capital letters is sent as digits: each letter is replaced by its position in the alphabet, `A` by `1`, `B` by `2` and so on up to `Z` by `26`, and the numbers are written one after another with no separators. A position is never written with a leading zero, so `0` on its own and pairs such as `05` stand for no letter.

Implement `Telegram.messages(String digits)` in `Telegram.java` and return how many different messages are written as `digits`. Return `0` when no message is.

## Example 1

**Input:** `digits = "1726"`
**Output:** `4`
**Explanation:** `AGBF` (1, 7, 2, 6), `QBF` (17, 2, 6), `AGZ` (1, 7, 26) and `QZ` (17, 26). `72` is above 26, so the `7` and the `2` always stand for separate letters.

## Example 2

**Input:** `digits = "2101"`
**Output:** `1`
**Explanation:** The `0` cannot stand alone, so it belongs with the `1` before it as `10`. That leaves `2`, `10`, `1`, which is `BJA`.

## Example 3

**Input:** `digits = "507"`
**Output:** `0`
**Explanation:** The `0` cannot stand alone and `50` is above 26, so no message is written this way.

## Constraints

- `1 <= digits.length() <= 100`
- `digits` holds only decimal digits.
- The answer is at most `2^31 - 1`.

## Follow-up

What if some digits were unreadable and could stand for any digit from 1 to 9?
