# How a strong round goes: Group Anagrams

## Approach

I would turn "same letters" into a key. Sorting the letters of a word gives a string that every anagram of it shares, and a different string for anything else. So one pass with a hash map from that key to a list of words does the grouping, and the groups are just the map values. Comparing each word with every group would work, but the number of comparisons grows with words times groups.

```java
static List<List<String>> group(String[] words) {
    Map<String, List<String>> groups = new HashMap<>();
    for (String word : words) {
        char[] letters = word.toCharArray();
        Arrays.sort(letters); // anagrams sort to the same string
        String key = new String(letters);
        groups.computeIfAbsent(key, k -> new ArrayList<>()).add(word);
    }
    return new ArrayList<>(groups.values());
}
```

## Complexity

With n words of length at most L:

| Approach | Time | Extra space |
| --- | --- | --- |
| Compare each word with a member of every group | O(n times groups times L) | O(n times L) |
| Sorted letters as the key | O(n times L log L) | O(n times L) |
| Letter counts as the key (26 numbers) | O(n times L) | O(n times L) |

## Where it shows up

`playlist-dedupe` has the same shape: two track titles that are spelled differently are "the same song", so each title is reduced to a normalised key and a hash set or map does the comparing. Here the key is the sorted letters; there it is the cleaned-up title. The habit is the same: decide once what makes two things equal, write it as a key function, and let the hash collection do the rest.

## Follow-up answers

**What if the words can contain any Unicode characters?** Sort the code points instead of 26 counters, or use a map of character to count as the key. Take care with characters outside the basic plane, which take two `char` values in Java.

**Can you build the key in O(length) instead of O(length log length)?** Yes, count the letters into an array of 26 numbers and use that as the key, for example joined into a string or wrapped in a list. It beats sorting when words are long.

**How would you do this when the words do not fit in memory?** Compute the key as the words stream by and send each word to a partition chosen by the hash of its key, on disk or on other machines. Each partition holds whole groups, so it can be grouped on its own.
