# How a strong round goes: Permutation in String

## Approach

A stretch is a rearrangement of the word exactly when it has the same length and the same count of every letter. So only stretches of the word's length matter, and the letter counts decide the match. Counting each stretch from scratch costs the length of the word for every start. Neighbouring stretches differ by one character leaving and one entering, so I count the first stretch once and then slide: add the entering letter, remove the leaving one, and compare the 26 counters with the word's counters. If the word is longer than the text there is no stretch to look at, and the answer is false. The comparison costs 26 steps per position, which is a constant, so the whole pass is linear. A set of letters is not enough: for `"aab"` the text `"abb"` has the same set but different counts.

```java
static boolean appearsIn(String word, String text) {
    int m = word.length();
    if (m > text.length()) {
        return false; // no stretch is long enough
    }
    int[] need = new int[26];
    int[] have = new int[26];
    for (int i = 0; i < m; i++) {
        need[word.charAt(i) - 'a']++;
        have[text.charAt(i) - 'a']++;
    }
    if (Arrays.equals(need, have)) {
        return true;
    }
    for (int end = m; end < text.length(); end++) {
        have[text.charAt(end) - 'a']++; // enters the stretch
        have[text.charAt(end - m) - 'a']--; // leaves the stretch
        if (Arrays.equals(need, have)) {
            return true;
        }
    }
    return false;
}
```

## Complexity

| Approach | Time | Extra space |
| --- | --- | --- |
| Count the letters of every stretch from scratch | O(n times m) | O(1) |
| Slide a window of counters and compare 26 of them | O(26 n) | O(1) |

## Where it shows up

Anagram search is used in plagiarism and duplicate detection to find a block of text that reuses the same characters, and in word-game tools that look for a rearrangement of a rack of letters inside a dictionary entry. A fixed-size window with counters also appears when checking that a short time window has the same mix of event types as a reference window.

## Follow-up answers

**How would you return the start positions of all such stretches?** Do not stop at the first match: when the counters are equal, record the start of the current stretch, which is `end - m + 1`, and keep sliding.

**How would you avoid comparing all 26 counters at every step?** Keep a number that says how many letters currently have the right count. When a counter moves to or away from the needed value, adjust that number by one, and report a match when it equals the number of different letters in the word.

**What changes if the text and the word can contain any Unicode character?** The counter arrays become maps from character to count. Compare through the matching-letters number from the previous answer, because comparing two maps in full at every step would cost the size of the alphabet.
