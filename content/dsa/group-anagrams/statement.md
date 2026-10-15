# Group Anagrams

Two words are anagrams when one can be rearranged into the other, using every letter exactly once. Given an array of lowercase words, put the words into groups so that every group holds words that are anagrams of one another and no two groups could be merged.

Implement `GroupAnagrams.group(String[] words)` in `GroupAnagrams.java`. Return a list of groups; each group is a list of the words, repeated words included. The order of the groups and the order inside a group do not matter.

## Examples

- `words = ["tide", "diet", "edit", "rain", "iran"]` returns the groups `["tide", "diet", "edit"]` and `["rain", "iran"]`.
- `words = ["solo"]` returns one group, `["solo"]`.
- `words = ["", "", "a"]` returns `["", ""]` and `["a"]`: the empty word is its own anagram class.

## Constraints

- 0 <= `words.length` <= 60,000
- 0 <= word length <= 100
- Words contain only the letters `a` to `z`.
