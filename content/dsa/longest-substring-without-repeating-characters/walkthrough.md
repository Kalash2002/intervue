# How a strong round goes: Longest Substring Without Repeating Characters

## Approach

I treat the answer as a window: a start and an end with no repeated character between them. I move the end one character at a time. If the new character already sits inside the window, the window is invalid, and the smallest repair is to move the start to just after that earlier copy. For that I need to know where each character was last seen, so I keep a table indexed by the character, storing the position after its last occurrence. The start never moves backwards: in `"abba"`, when the final `a` arrives its last copy is at position 0, which is already behind the start, so the start must stay where it is. Taking the larger of the old start and the stored position handles that. After each step the window is valid, so I record its length if it is the longest so far. A table of 65,536 entries covers every possible `char`.

```java
static int longestDistinct(String s) {
    int[] after = new int[65_536]; // position just after the last copy of each character, 0 if unseen
    int start = 0;
    int best = 0;
    for (int end = 0; end < s.length(); end++) {
        char c = s.charAt(end);
        start = Math.max(start, after[c]); // never move the start back
        after[c] = end + 1;
        best = Math.max(best, end - start + 1);
    }
    return best;
}
```

## Complexity

| Approach | Time | Extra space |
| --- | --- | --- |
| From every start, extend until a repeat | O(n times k), k the longest stretch | O(k) |
| Window with a last-seen table | O(n) | O(1), a fixed table of 65,536 entries |

## Where it shows up

Looking for the longest run of unique items is how a stream processor finds the longest span without a duplicate event, for example the longest stretch of requests with no repeated request id when checking a deduplication window. Last-seen tables with a moving start are also used to measure how long a session lasted before a user repeated an action.

## Follow-up answers

**How would you return the stretch itself, not only its length?** Remember the start position whenever a new best length is found, then take the substring from that start with the best length at the end.

**What changes if up to two repeats of a character are allowed in the stretch?** Keep a count per character instead of a last position. Move the end forward, and when a count would pass the limit, move the start forward, lowering counts, until it is back within the limit.

**How would you handle a stream of characters that never ends?** The window and the table are all you need, and neither grows with the input, so you can process one character at a time. Report the best length so far whenever you are asked, and use a position counter that does not overflow, such as a long.
