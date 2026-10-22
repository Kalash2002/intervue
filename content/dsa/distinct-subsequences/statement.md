# Distinct Subsequences

Striking letters out of a text and keeping the rest in their order can leave a given word behind, often in more than one way. Two ways are different when they keep different positions of the text, even though both spell the same word.

Implement `Strikeout.ways(String text, String word)` in `Strikeout.java` and return the number of different sets of positions of `text` whose letters, read from left to right, spell `word`.

The count for the whole of `word` always fits an `int` (see Constraints), but the counts for its shorter beginnings need not: with some texts they run far past it, even past what a `long` holds. Only the count for the whole word is checked.

## Example 1

**Input:** `text = "tomatoes"`, `word = "toes"`
**Output:** `3`
**Explanation:** The e and the s are the last two letters of the text, and the t and the o before them can be the first t with the first o, the first t with the second o, or the second t with the second o.

## Example 2

**Input:** `text = "committee"`, `word = "mite"`
**Output:** `8`
**Explanation:** Either of the two m's, the only i, either t and either e: every copy of each letter comes after every copy of the letter before it, so the choices are independent and there are 2 × 1 × 2 × 2 ways.

## Example 3

**Input:** `text = "night"`, `word = "thing"`
**Output:** `0`
**Explanation:** The two words have the same letters, but the word needs all five of them, and keeping all five letters of `night` leaves `night`.

## Constraints

- `1 <= text.length() <= 1000`
- `1 <= word.length() <= 1000`
- `text` and `word` contain only lowercase English letters.
- The answer is less than `2^31`.

## Follow-up

What if the count could be far larger than 2^31 and had to be reported modulo 10^9 + 7?
