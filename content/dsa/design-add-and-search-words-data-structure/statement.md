# Design Add and Search Words Data Structure

A crossword helper keeps a dictionary of words and answers questions such as "is there a stored word that fits `l.mp`?", where each dot is a square whose letter is not known yet.

Implement the class `WordDictionary` in `WordDictionary.java` with two methods:

- `void addWord(String word)` stores `word`. Storing a word that is already there changes nothing.
- `boolean search(String pattern)` returns `true` if some stored word has exactly as many letters as `pattern` and agrees with it at every position, where a `.` in the pattern agrees with any one letter. A dot never stands for zero letters or for two.

## Example 1

**Input:** `addWord("lamp")`, `addWord("limp")`, `addWord("lump")`, then `search("l.mp")`, `search("l..p")` and `search("l.m")`
**Output:** `true`, `true` and `false`
**Explanation:** All three words fit `l.mp` and `l..p`. No stored word has three letters, so `l.m` fits nothing even though it is the start of every word.

## Example 2

**Input:** `addWord("ox")`, then `search("o.")`, `search(".")` and `search("ox.")`
**Output:** `true`, `false` and `false`
**Explanation:** `o.` fits `ox`. A single dot needs a one-letter word, and `ox.` needs three letters; a dot cannot stand for nothing.

## Example 3

**Input:** `search("..")` on a dictionary with no words
**Output:** `false`
**Explanation:** With nothing stored, no pattern fits, not even one made only of dots.

## Constraints

- `0 <= number of addWord calls <= 5 * 10^4`
- `0 <= number of search calls <= 5 * 10^4`
- `1 <= word.length <= 20`
- `1 <= pattern.length <= 20`
- `word` holds only lowercase letters `a` to `z`.
- `pattern` holds only lowercase letters `a` to `z` and `.`.
- `0 <= number of dots in pattern <= 2`

## Follow-up

What does a pattern made only of dots cost, and how would you make it cheap?
