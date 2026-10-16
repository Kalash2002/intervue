## Level 1
Two words belong together exactly when they have the same letters. Think about a single value, computed from a word, that is equal for all words in a group and different between groups.

## Level 2
Words in a group differ only in the order of their letters, so anything that ignores order can be that value. Once you have it, what structure lets you collect the words that share it?

## Level 3
Compute a key from each word that all of its anagrams share and no other word does, and keep a map from key to the list of words that produced it.
