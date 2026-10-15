# Permutation in String

You are given a `word` and a `text`. A stretch of the text is a run of consecutive characters. Decide whether some stretch of the text uses exactly the letters of `word`, each letter the same number of times, in any order.

Implement `Scramble.appearsIn(String word, String text)` in `Scramble.java`. Return `true` if such a stretch exists and `false` otherwise.

## Examples

- `word = "lap"`, `text = "xpalq"` returns `true`: the stretch `"pal"` is a rearrangement of `"lap"`.
- `word = "stop"`, `text = "xxpotsy"` returns `true`: the stretch `"pots"` uses the same four letters.
- `word = "zz"`, `text = "azbz"` returns `false`: both `z` letters are there, but no two consecutive characters are both `z`.

## Constraints

- 1 <= `word.length()` <= 100,000
- 0 <= `text.length()` <= 100,000
- Both strings contain only lowercase letters `a` to `z`
