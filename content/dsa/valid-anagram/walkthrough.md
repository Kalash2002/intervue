# How a strong round goes: Valid Anagram

## Approach

Anagrams share one thing: the same count of every letter. Order does not matter, so I do not need to compare positions at all. With only 26 possible letters, a small table of counts is enough. First, if the lengths differ the answer is no and there is nothing to count. Otherwise I add one to the table for every letter of `a`, then take one away for every letter of `b`. If a count would go below zero, `b` has a letter `a` cannot supply. Because the lengths are equal, no negative count means every count ends at zero, so I need no final pass. Comparing only which letters appear is not enough: `aab` and `abb` use the same letters but are not anagrams.

```java
static boolean areAnagrams(String a, String b) {
    if (a.length() != b.length()) {
        return false; // different lengths can never use the same letters
    }
    int[] counts = new int[26]; // how many of each letter a has not yet been matched by b
    for (int i = 0; i < a.length(); i++) {
        counts[a.charAt(i) - 'a']++;
    }
    for (int i = 0; i < b.length(); i++) {
        if (--counts[b.charAt(i) - 'a'] < 0) {
            return false; // b uses a letter more often than a does
        }
    }
    return true; // equal lengths and no overdraw: every count is back to zero
}
```

## Complexity

| Approach | Time | Extra space |
| --- | --- | --- |
| Match each letter of `a` to an unused equal letter of `b` | O(n squared) | O(n) |
| Sort both and compare | O(n log n) | O(n) |
| Count the letters in a table | O(n) | O(1), the table has 26 slots |

## Where it shows up

Counting how often each key occurs, then comparing two tallies, is the everyday use of a hash map. A search index compares word counts to spot near-duplicate documents, and a test helper checks that two lists hold the same items in any order by comparing their tallies.

## Follow-up answers

**What changes if the text can contain any Unicode character, not just lowercase letters?** The fixed table of 26 no longer fits, so I would use a hash map from character to count, with the same add-then-subtract logic. Characters outside the basic plane are two `char` values in Java, so I would count code points instead.

**How would you group a long list of words into sets of anagrams?** Give every word a key that is identical for all its anagrams, such as its letters sorted or its 26 counts joined into a string, and put words with the same key in one list in a hash map.

**Can you do it with one pass over each string?** Yes, that is what the counting does: one pass over `a` to add, one over `b` to subtract. It cannot be a single combined pass in general, because the letter of `b` at a given position may only be supplied by a later letter of `a`.
