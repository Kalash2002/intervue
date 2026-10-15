# How a strong round goes: Longest Repeating Character Replacement

## Approach

For any stretch of the string, the cheapest way to make it one letter is to keep its most common letter and change everything else. So a stretch is acceptable when its length minus the highest count in it is at most `k`. I search for the longest acceptable stretch with a window: letter counts for the window, the right edge moving forward one step at a time. After adding a character I update the highest count seen. If the window now needs more than `k` changes, I move the left edge one step, which slides the window along at the same size instead of shrinking it. The highest count is allowed to go stale when the left edge moves. The reason it is safe: the window can only grow past its current size when a letter reaches a count higher than any seen before, and that is exactly when the highest count is updated. A stale value can never let the window grow wrongly, and the final window size is the answer.

```java
static int longestRun(String s, int k) {
    int[] count = new int[26];
    int left = 0;
    int highest = 0; // highest count of one letter seen in any window so far
    int best = 0;
    for (int right = 0; right < s.length(); right++) {
        highest = Math.max(highest, ++count[s.charAt(right) - 'A']);
        if (right - left + 1 - highest > k) { // too many changes needed: slide the window
            count[s.charAt(left) - 'A']--;
            left++;
        }
        best = Math.max(best, right - left + 1);
    }
    return best;
}
```

## Complexity

| Approach | Time | Extra space |
| --- | --- | --- |
| From every start, extend until more than k changes are needed | O(n squared) | O(1) |
| One window with letter counts | O(n) | O(1), 26 counters |

## Where it shows up

Choosing the longest span that can be made uniform with a few edits is how a data-cleaning tool finds the longest run of a value in a noisy sensor log if up to k readings may be treated as glitches. The same count-in-a-window idea appears in log analysis, for example the longest period in which at most k requests were not from the dominant client.

## Follow-up answers

**Why can the maximum count be left stale when the window slides, and does recomputing it each time change the answer?** A stale value can make the current window look better than it is, but the window only gets larger when a letter count passes every earlier maximum, and then the maximum is updated, so the answer stays correct. Recomputing the maximum by scanning 26 counters each step is also correct and still linear, because 26 is a constant; it is just a little more work.

**How would you also report which letter the run is made of?** Remember the letter that has the highest count when the best size is updated. With the stale-maximum version, that letter is the one whose increment set the new highest count.

**What changes if the alphabet is any Unicode character?** The 26 counters become a map from character to count. Recomputing the maximum then costs a scan of the map, so keep the stale-maximum version, which needs no scan.
