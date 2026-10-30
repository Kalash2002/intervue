# Grader notes: Implement Trie (Prefix Tree)

Tests cover: the sample, a word versus its own prefix, two words sharing a prefix, a word that is a prefix of another, an unknown prefix, an empty trie, inserting the same word twice, single-letter words, and a large run of 50,000 seeded random eight-letter words followed by 200,000 mixed queries whose answers the test computes from a hash set of the words.

The naive solution keeps a list of words and scans it for every query, so with many words it is quadratic overall and times out on the large test at the limit and at 3x. The reference is an array-based trie.
