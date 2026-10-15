# How a strong round goes: Valid Palindrome

## Approach

I could build a cleaned copy of the text and compare it with its reverse, but that allocates a second string for no reason. Instead I walk in from both ends with two positions. At each step I move the left position past characters that do not count, and the right position past the same going backwards, then compare the two letters or digits that are left, ignoring case. A mismatch ends it with no. If the positions meet or cross, every pair matched and the answer is yes. The ignore rule must apply to digits too: they are compared, not dropped, so `a7` is not a palindrome. The skip loops need their own bounds check so they do not run off the string on input like `" .,; "`, where nothing counts.

```java
static boolean isPalindrome(String s) {
    int left = 0;
    int right = s.length() - 1;
    while (left < right) {
        if (!Character.isLetterOrDigit(s.charAt(left))) {
            left++; // ignore this character on the left
        } else if (!Character.isLetterOrDigit(s.charAt(right))) {
            right--; // ignore this one on the right
        } else if (Character.toLowerCase(s.charAt(left)) != Character.toLowerCase(s.charAt(right))) {
            return false; // a pair that counts and differs
        } else {
            left++;
            right--;
        }
    }
    return true; // the positions met: every pair matched
}
```

## Complexity

| Approach | Time | Extra space |
| --- | --- | --- |
| Build a cleaned copy, reverse it and compare | O(n) | O(n) |
| Two positions moving inwards | O(n) | O(1) |

## Where it shows up

Walking in from both ends is how symmetric structures are checked in place: matching the opening and closing markers of a record, or checking that a sequence reads the same in both directions. Skipping characters that do not count before comparing is also the usual way to normalise user input, such as ignoring punctuation and case when two usernames or search terms are compared.

## Follow-up answers

**How would you allow deleting at most one character to make it a palindrome?** On the first mismatch, try skipping the left character and try skipping the right one, and check whether either of the remaining middle parts is a palindrome with the same two-position walk. If neither is, the answer is no.

**What changes if the text can contain accented letters and other Unicode?** Normalise the text first so that an accented letter has one form, and compare code points, not `char` values, because some characters take two `char` values in Java. The two-position walk stays the same.

**How would you find the longest palindrome inside a string?** Treat every position, and every gap between two positions, as a centre and expand outwards while the two sides match. Keep the longest. That is O(n squared) in the worst case.
