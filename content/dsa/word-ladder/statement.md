# Word Ladder

A word game starts from one word and changes a single letter at a time until it becomes another word. Every word you produce along the way, including the last one, must appear in a given list of allowed words. The starting word does not need to be in the list.

Implement `Ladder.fewestChanges(String begin, String end, List<String> allowed)` in `Ladder.java`. Return the smallest number of single-letter changes that turn `begin` into `end`, or `-1` when it cannot be done. If `begin` equals `end`, the answer is `0`.

## Examples

- `begin = "cold"`, `end = "warm"`, `allowed = [cord, card, ward, warm, worm]` returns `4`: cold, cord, card, ward, warm.
- `begin = "map"`, `end = "mat"`, `allowed = [mat, man, mad]` returns `1`: one letter changes and `mat` is allowed.
- `begin = "dog"`, `end = "dot"`, `allowed = [dig, dug]` returns `-1`: `dot` is not an allowed word.

## Constraints

- All words have the same length, between 1 and 10, and use lowercase letters `a` to `z`.
- 0 <= `allowed.size()` <= 50,000, and the allowed words are distinct.
