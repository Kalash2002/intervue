# Search Suggestions System

A shop has a catalogue of product names. As a customer types a search word one letter at a time, the search box shows suggestions after every letter: up to `limit` catalogue names that start with the text typed so far, smallest first in dictionary order (the order `String.compareTo` gives).

Implement `Autocomplete.suggest(String[] products, String typed, int limit)` in `Autocomplete.java`. It returns a list with one entry per letter of `typed`. Entry `k` (from 0) holds the suggestions for the first `k + 1` letters, in order. An entry is empty when nothing matches. A product equal to the typed text counts as a match and, being a prefix of longer names, comes before them.

The number of suggestions is not fixed: the parameter `limit` sets it.

## Example 1

**Input:** `products = ["kiln", "kind", "kit", "kite", "lamp"]`, `typed = "kit"`, `limit = 3`
**Output:** `[["kiln", "kind", "kit"], ["kiln", "kind", "kit"], ["kit", "kite"]]`
**Explanation:** All four `k` names match the first two prefixes and only three fit; `kiln` sorts before `kind` because `l` is before `n`. For `kit` only `kit` and `kite` match, and `kit` comes first.

## Example 2

**Input:** `products = ["pen", "pencil"]`, `typed = "pz"`, `limit = 3`
**Output:** `[["pen", "pencil"], []]`
**Explanation:** Both names start with `p`, and nothing starts with `pz`.

## Example 3

**Input:** `products = ["ab", "abc", "b"]`, `typed = "abd"`, `limit = 1`
**Output:** `[["ab"], ["ab"], []]`
**Explanation:** With a limit of 1 only the smallest match shows, and no name starts with `abd`.

## Constraints

- `1 <= products.length <= 5 * 10^4`
- All names in `products` are different.
- `1 <= typed.length() <= 1000`
- `1 <= products[i].length() <= 1000`
- `1 <= limit <= 10`
- Products and `typed` contain only lowercase letters.

## Follow-up

What if the best suggestions are ranked by popularity instead of dictionary order?
