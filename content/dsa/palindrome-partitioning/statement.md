# Palindrome Partitioning

You are given a string of lowercase letters. Cut it into one or more consecutive pieces so that every piece is a palindrome, a piece that reads the same forwards and backwards. A single letter is always a palindrome.

Implement `PalindromePartition.all(String s)` in `PalindromePartition.java`. Return every such way of cutting `s` exactly once, each as the list of its pieces from left to right. The pieces inside one way keep their left-to-right order; the ways themselves may be returned in any order.

## Example 1

**Input:** `s = "noon"`
**Output:** `[["n","o","o","n"], ["n","oo","n"], ["noon"]]` (in any order)
**Explanation:** Besides four single letters, `oo` and the whole word are palindromes. `no`, `noo` and `oon` are not, so no other cut works.

## Example 2

**Input:** `s = "ttt"`
**Output:** `[["t","t","t"], ["t","tt"], ["tt","t"], ["ttt"]]` (in any order)
**Explanation:** Every piece of a string of one repeated letter is a palindrome, so each of the two gaps can be cut or not: four ways.

## Example 3

**Input:** `s = "xyz"`
**Output:** `[["x","y","z"]]`
**Explanation:** No two or three neighbouring letters form a palindrome, so the only way is to cut between every pair of letters.

## Constraints

- `1 <= s.length() <= 12`
- `s` holds only lowercase English letters.

## Follow-up

How would you count the ways without listing them?
