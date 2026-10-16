# Implement Trie (Prefix Tree)

A trie stores words so that words sharing a beginning also share storage, and so that lookups cost time in the length of the text asked about, not in the number of words stored.

Implement the class `Trie` in `Trie.java` with three methods:

- `void insert(String word)` adds `word` to the trie. Adding a word that is already there changes nothing.
- `boolean search(String word)` returns `true` only if exactly this word was inserted earlier.
- `boolean startsWith(String prefix)` returns `true` if at least one inserted word begins with `prefix`. A word counts as beginning with itself.

## Example 1

**Input:** `insert("tram")`, `insert("trap")`, `insert("tab")`, then `search("tra")`, `startsWith("tra")` and `search("trap")`
**Output:** `false`, `true` and `true`
**Explanation:** `"tra"` is only the beginning of `"tram"` and `"trap"`, not a word that was inserted, so `search` says no while `startsWith` says yes. `"trap"` was inserted as a whole word.

## Example 2

**Input:** `insert("car")`, `insert("cart")`, then `search("car")`, `startsWith("cart")` and `search("carts")`
**Output:** `true`, `true` and `false`
**Explanation:** A word can also be the beginning of a longer word. Both `"car"` and `"cart"` were inserted, and nothing as long as `"carts"` was.

## Example 3

**Input:** `search("a")` and `startsWith("a")` on an empty trie
**Output:** `false` and `false`
**Explanation:** Nothing has been inserted, so there is no word and no beginning of a word.

## Constraints

- `0 <= number of insert calls <= 5 * 10^4`
- `0 <= number of search and startsWith calls <= 2 * 10^5`
- `1 <= word.length <= 20`
- `1 <= prefix.length <= 20`
- `word` and `prefix` use only lowercase letters `a` to `z`.

## Follow-up

How would you return every word that starts with a prefix?
