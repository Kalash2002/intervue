# Group Anagrams

Two words are anagrams when one can be rearranged into the other, using every letter exactly as many times as it appears. You are given an array of lowercase words. Put the words into groups so that every group holds words that are anagrams of one another, and no two groups could be merged.

Implement `GroupAnagrams.group(String[] words)` in `GroupAnagrams.java`. Return a list of groups; each group is a list of words, repeated words included. The order of the groups and the order of the words inside a group do not matter.

## Example 1

**Input:** `words = ["tide", "diet", "edit", "rain", "iran"]`
**Output:** `[["tide", "diet", "edit"], ["rain", "iran"]]`
**Explanation:** `tide`, `diet` and `edit` all use one each of `d`, `e`, `i` and `t`. `rain` and `iran` use one each of `a`, `i`, `n` and `r`. The two sets of letters differ, so there are two groups.

## Example 2

**Input:** `words = ["abb", "bab", "baa", "aba"]`
**Output:** `[["abb", "bab"], ["baa", "aba"]]`
**Explanation:** All four words have length 3 and use only `a` and `b`, but `abb` and `bab` have two `b` and one `a`, while `baa` and `aba` have two `a` and one `b`. Same letters is not enough; the counts must match.

## Example 3

**Input:** `words = ["", "", "a"]`
**Output:** `[["", ""], ["a"]]`
**Explanation:** An empty word uses no letters, so it is an anagram of another empty word and of nothing else. The two empty words form one group and `a` forms its own.

## Constraints

- `0 <= words.length <= 6 * 10^4`
- `0 <= words[i].length <= 100`
- `words[i]` consists of lowercase English letters (`a` to `z`).

## Follow-up

Can the cost per word be linear in the word's length?
