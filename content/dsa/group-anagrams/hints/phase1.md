## Level 1
Two words belong together exactly when they have the same letters. Think about a single value, computed from a word, that is equal for all words in a group and different between groups.

## Level 2
If you sort the letters of a word, all its anagrams produce the same sorted string. What can you do with such a string as a key?

## Level 3
Keep a map from the sorted-letters key to a list of words. For each word, build the key, then append the word to the list stored under it. At the end, return the map values. Counting the 26 letters instead of sorting gives a key in linear time per word.
