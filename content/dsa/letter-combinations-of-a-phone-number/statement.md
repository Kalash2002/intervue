# Letter Combinations of a Phone Number

On a phone keypad each digit from 2 to 9 stands for a few letters: 2 is `abc`, 3 is `def`, 4 is `ghi`, 5 is `jkl`, 6 is `mno`, 7 is `pqrs`, 8 is `tuv` and 9 is `wxyz`. Typing a sequence of digits could mean any string made by picking one letter for each digit, in the same order.

Implement `PhoneLetters.combinations(String digits)` in `PhoneLetters.java`. Return every string the digits could stand for, each exactly once, in any order. If `digits` is empty, return an empty list.

## Example 1

**Input:** `digits = "58"`
**Output:** `["jt","ju","jv","kt","ku","kv","lt","lu","lv"]` (in any order)
**Explanation:** 5 gives `j`, `k` or `l` and 8 gives `t`, `u` or `v`, so there are 3 × 3 = 9 strings.

## Example 2

**Input:** `digits = "9"`
**Output:** `["w","x","y","z"]` (in any order)
**Explanation:** One digit gives one string per letter on its key.

## Example 3

**Input:** `digits = "77"`
**Output:** `["pp","pq","pr","ps","qp","qq","qr","qs","rp","rq","rr","rs","sp","sq","sr","ss"]` (in any order)
**Explanation:** A digit may repeat, and each 7 independently gives one of its four letters: 4 × 4 = 16 strings, including `pp` and `ss`.

## Constraints

- `0 <= digits.length() <= 5`
- Every character of `digits` is a digit from `'2'` to `'9'`.

## Follow-up

How would you return only the strings that are real words from a dictionary?
