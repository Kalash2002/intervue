# Grader notes: Group Anagrams

Tests cover: the sample (three groups of anagrams), a single word, repeated words, empty words, words with no anagram partner, a word that shares a length and most letters with another but is not an anagram, one big group, and a large input of 60,000 seeded random eight-letter words. Group and word order are normalised by the tests before comparing.

The naive solution compares each word with a representative of every existing group, counting letters each time; with tens of thousands of groups that is quadratic and times out on the large test at the limit and at 3x. The reference keys a hash map by the 26 letter counts.
