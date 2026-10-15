# How a strong round goes: Minimum Window Substring

## Approach

Extending a covering stretch to the right keeps it covering, and cutting from the left may or may not. That suggests a window with two edges. I keep a table of how many copies of each needed character the window still lacks, and one number for how many characters in total are still missing. I move the right edge forward, taking in characters, until nothing is missing. Then I move the left edge forward as long as the window stays covering, because every character dropped without harm makes the stretch shorter. In the reference the loop records the window first and then drops the leftmost character, which is how it finds the last covering size for this right edge. It keeps the window only if it is strictly shorter than the best so far, which keeps the leftmost one on ties. Dropping a needed character leaves something missing, and the right edge starts moving again. A character that is not needed has a table value of zero; taking it in drives the value below zero, which means a surplus, so it is never counted as missing. Counts, not just presence, matter: for `need = "aab"` the stretch `"ab"` has the right set of letters but one `a` too few.

```java
static String smallest(String text, String need) {
    int[] missing = new int[128]; // how many copies of each character the window still lacks
    for (int i = 0; i < need.length(); i++) {
        missing[need.charAt(i)]++;
    }
    int totalMissing = need.length();
    int bestStart = 0;
    int bestLength = Integer.MAX_VALUE;
    int left = 0;
    for (int right = 0; right < text.length(); right++) {
        if (missing[text.charAt(right)]-- > 0) {
            totalMissing--; // this character was really needed
        }
        while (totalMissing == 0) {
            if (right - left + 1 < bestLength) { // strictly shorter keeps the leftmost
                bestLength = right - left + 1;
                bestStart = left;
            }
            if (++missing[text.charAt(left)] > 0) {
                totalMissing++; // dropping this character leaves a gap
            }
            left++;
        }
    }
    return bestLength == Integer.MAX_VALUE ? "" : text.substring(bestStart, bestStart + bestLength);
}
```

## Complexity

| Approach | Time | Extra space |
| --- | --- | --- |
| From every start, extend until the need is covered | O(n squared) | O(1) |
| Two edges moving forward with a missing-count table | O(n + m) | O(1), a table of 128 counters |

## Where it shows up

Finding the shortest span that contains a set of required terms is how a search engine picks the snippet to show: the tightest passage in a document that mentions every query word. The same idea is used in log tools to find the shortest time range in which every kind of event in a checklist occurred at least once.

## Follow-up answers

**How would you avoid checking all letters every time the window changes?** The version above already does. It keeps one number for how many characters are still missing and changes it by one only when a count crosses zero, so each step costs constant time.

**What changes if the answer must be the shortest stretch that contains the letters in the given order?** Counting no longer works, because order matters. The usual way is a pass that matches the letters one by one to find an end, then a pass backwards from that end to find the latest start, which is a subsequence problem rather than a counting one.

**How would you return every shortest stretch, not only the leftmost?** Keep a list instead of one start. When a strictly shorter stretch is found, clear the list and add it; when one of equal length is found, add it. The cost is the size of the output.
