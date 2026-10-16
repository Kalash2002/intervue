# Permutation in String

You are given two strings, `word` and `text`. A stretch of the text is a run of consecutive characters. Decide whether some stretch of `text` is a rearrangement of `word`: it holds the same characters as `word`, each the same number of times, in any order.

Implement `Scramble.appearsIn(String word, String text)` in `Scramble.java`. Return `true` if such a stretch exists and `false` otherwise.

## Example 1

**Input:** `word = "lap"`, `text = "xpalq"`
**Output:** `true`
**Explanation:** The stretch `"pal"` holds one each of `l`, `a` and `p`, which is a rearrangement of `"lap"`.

## Example 2

**Input:** `word = "tide"`, `text = "xxeditz"`
**Output:** `true`
**Explanation:** The stretch `"edit"` uses the same four letters as `"tide"`.

## Example 3

**Input:** `word = "zz"`, `text = "azbz"`
**Output:** `false`
**Explanation:** The text has two `z` characters, but no two consecutive characters are both `z`.

## Example 4

**Input:** `word = "rune"`, `text = "un"`
**Output:** `false`
**Explanation:** No stretch of `"un"` is long enough to hold four letters.

## Constraints

- `1 <= word.length <= 10^5`
- `0 <= text.length <= 10^5`
- `word` and `text` consist of lowercase English letters.

## Follow-up

Can you beat O(`word.length` * `text.length`) time?
