# Implement Trie (Prefix Tree)

A trie stores words so that words sharing a beginning also share storage, and so that lookups cost time in the length of the text asked about, not in the number of words stored.

Implement the class `Trie` in `Trie.java` with three methods:

- `void insert(String word)` adds `word` to the trie. Adding a word that is already there changes nothing.
- `boolean search(String word)` returns `true` only if exactly this word was inserted earlier.
- `boolean startsWith(String prefix)` returns `true` if at least one inserted word begins with `prefix`. A word counts as beginning with itself.

## Examples

- After `insert("tram")`: `search("tram")` is `true`, `search("tra")` is `false` (only a beginning of a word, not a word), and `startsWith("tra")` is `true`.
- After `insert("tram")` and `insert("trap")`: `startsWith("tr")` is `true`, `startsWith("tre")` is `false`, `search("trap")` is `true`.
- On an empty trie, `search("a")` and `startsWith("a")` are both `false`.

## Constraints

- Words and prefixes contain only lowercase letters `a` to `z` and are non-empty.
- Up to 200,000 calls in one run, over up to 50,000 inserted words.
