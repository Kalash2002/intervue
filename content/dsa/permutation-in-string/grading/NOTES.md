# Grader notes: Permutation in String

Tests cover: the sample (true), a one-letter word, a word longer than the text, an empty text, a match at the very start and one at the very end of the text, the same letters with the wrong counts (`"aab"` against `"xabbx"` is false; a set comparison says true), repeated letters that match, letters in reversed order, and two large inputs of 100,000 characters: a word of 50,000 letters that is almost all one letter against a text of one letter (false), and a word of two blocks against a text of two blocks, where the match straddles the middle (true).

The naive solution counts the letters of every stretch from scratch in a map; with a 50,000 letter word that is billions of map updates, which times out at the limit and at 3x. The reference slides one window.
