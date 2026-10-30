# Longest Common Subsequence

A subsequence of a string keeps some of its letters, any number from none to all, in their original order, and drops the rest. The letters kept do not have to sit next to each other: `cat` is a subsequence of `cobalt`, but `tac` is not.

Implement `Drafts.longestCommon(String first, String second)` in `Drafts.java` and return the length of the longest string that is a subsequence of both `first` and `second`. Return `0` if the two strings share no letter.

## Example 1

**Input:** `first = "cordon"`, `second = "condor"`
**Output:** `4`
**Explanation:** `codo` is a subsequence of both. The two words use the same six letters, but any two of `r`, `d` and `n` come in different orders in the two words, so a common subsequence keeps at most one of those three letters.

## Example 2

**Input:** `first = "pistachio"`, `second = "captain"`
**Output:** `4`
**Explanation:** `ptai` appears in this order in both, spread out. The only other letter they share, `c`, comes first in `captain` but after the `p` in `pistachio`, so it cannot join them.

## Example 3

**Input:** `first = "golf"`, `second = "flog"`
**Output:** `1`
**Explanation:** Every letter of one word is in the other, but in the opposite order, so any two letters come in different orders in the two words and only one letter can be kept.

## Constraints

- `1 <= first.length() <= 1000`
- `1 <= second.length() <= 1000`
- `first` and `second` contain only lowercase English letters.

## Follow-up

What if you also had to return one longest common subsequence?
