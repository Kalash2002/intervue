# Alien Dictionary

A language writes its words with lowercase English letters, but its alphabet puts them in an order you do not know. You are given `words`, a list that is claimed to be sorted the way that language's dictionary sorts: two words are compared at the first position where they differ, by the alphabet order of the letters there, and a word that is a proper prefix of another comes before it. Equal words may stand next to each other.

Implement `Lexicon.letterOrder(String[] words)` in `Lexicon.java`. Return a string that contains every letter appearing anywhere in `words` exactly once, and no other letter, in an order under which the list really is sorted. If several orders work, return any of them. If no order works, because the list contradicts itself, return the empty string `""`. A longer word listed before its own proper prefix, such as `"lamp"` before `"lam"`, is such a contradiction.

## Example 1

**Input:** `words = ["mop","mpq","om","oq","q"]`
**Output:** `"mopq"`
**Explanation:** The pairs of neighbours give o before p, m before o, m before q and o before q. Both `"mopq"` and `"moqp"` satisfy them, and either is accepted.

## Example 2

**Input:** `words = ["zoo"]`
**Output:** `"zo"`
**Explanation:** One word gives no rule, so both letters appear in any order; `"oz"` is accepted too.

## Example 3

**Input:** `words = ["kn","nk","k"]`
**Output:** `""`
**Explanation:** The first pair needs k before n and the second needs n before k, so no order works.

## Example 4

**Input:** `words = ["lamp","lam"]`
**Output:** `""`
**Explanation:** `"lam"` is a proper prefix of `"lamp"`, so it would have to come first in any alphabet.

## Constraints

- `1 <= words.length <= 10^5`
- `1 <= words[i].length <= 20`
- `'a' <= words[i][j] <= 'z'`

## Follow-up

How would you tell whether the letter order is the only one possible?
