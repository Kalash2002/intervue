# Valid Anagram

Two words are anagrams when the letters of one can be rearranged to spell the other, using every letter exactly as many times as it appears.

Implement `Anagrams.areAnagrams(String a, String b)` in `Anagrams.java`. Return `true` when `a` and `b` are anagrams of each other and `false` otherwise.

## Example 1

**Input:** `a = "carton"`, `b = "contra"`
**Output:** `true`
**Explanation:** Both words use one each of `c`, `a`, `r`, `t`, `o` and `n`, only in a different order.

## Example 2

**Input:** `a = "noon"`, `b = "nnno"`
**Output:** `false`
**Explanation:** The lengths match and the letters are the same, but `a` has two `n` and two `o` while `b` has three `n` and one `o`.

## Example 3

**Input:** `a = ""`, `b = ""`
**Output:** `true`
**Explanation:** Two empty words use no letters at all, so each is trivially a rearrangement of the other.

## Constraints

- `0 <= a.length, b.length <= 2 * 10^5`
- `a` and `b` consist of lowercase English letters (`a` to `z`).

## Follow-up

Can you do it in a single linear pass over each word, with memory that does not grow with the length?
