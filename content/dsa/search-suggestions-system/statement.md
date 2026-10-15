# Search Suggestions System

A shop has a catalogue of product names. As a customer types a search word one letter at a time, the search box shows suggestions after every letter: up to `limit` catalogue names that start with the text typed so far, smallest first in dictionary order (the order `String.compareTo` gives).

Implement `Autocomplete.suggest(String[] products, String typed, int limit)` in `Autocomplete.java`. It returns a list with one entry per letter of `typed`. Entry `k` (from 0) holds the suggestions for the first `k + 1` letters, in order. An entry is empty when nothing matches. A product equal to the typed text counts as a match and, being a prefix of longer names, comes before them.

The best-known form of this question always shows three suggestions. Here the number is the parameter `limit`.

## Examples

- `products = ["kiln", "kind", "kit", "kite", "lamp"]`, `typed = "kit"`, `limit = 3` returns `[["kiln", "kind", "kit"], ["kiln", "kind", "kit"], ["kit", "kite"]]`. All four `k` names match the first two prefixes and only three fit; `kiln` sorts before `kind` because `l` is before `n`.
- `products = ["pen", "pencil"]`, `typed = "pz"`, `limit = 3` returns `[["pen", "pencil"], []]`: nothing starts with `pz`.
- `products = ["ab", "abc", "b"]`, `typed = "abd"`, `limit = 1` returns `[["ab"], ["ab"], []]`.

## Constraints

- 1 <= `products.length` <= 50,000, all names different.
- 1 <= `typed.length()` <= 1,000
- 1 <= the length of every product <= 1,000
- 1 <= `limit` <= 10
- Products and `typed` contain only lowercase letters.
