# How a strong round goes: Longest Valid Parentheses

## Approach

Checking one string for balance is the easy stack problem. Here I want the longest balanced piece inside a messy string, and a balanced piece is cut off by characters that can never match: a `)` with nothing open, or a `(` that is never closed. If I know where the last unmatched character sat, then a balanced stretch that ends at the current position starts right after it, and its length is just a difference of positions.

So I keep a stack of positions rather than characters. I start it with a sentinel, the position `-1` just before the string, standing for "the last cut". For each `(` I push its position. For each `)` I pop. If the stack is empty after the pop, this `)` had nothing to match: it becomes the new cut, so I push its own position. Otherwise it matched a `(`, and the position now on top of the stack is the last character that is not part of the matched run ending here, so the balanced length is `i - stack.peek()`. I keep the maximum.

```java
static int longestBalanced(String s) {
    Deque<Integer> stack = new ArrayDeque<>();
    stack.push(-1); // the position of the last unmatched character
    int best = 0;
    for (int i = 0; i < s.length(); i++) {
        if (s.charAt(i) == '(') {
            stack.push(i);
            continue;
        }
        stack.pop();
        if (stack.isEmpty()) {
            stack.push(i); // an unmatched ')' becomes the new cut
        } else {
            best = Math.max(best, i - stack.peek());
        }
    }
    return best;
}
```

Two left-to-right and right-to-left counting passes reach the same answer in constant space: count opens and closes, reset when closes exceed opens, record `2 * closes` whenever the two are equal, then do the same from the right with the roles swapped.

## Complexity

| Approach | Time | Extra space |
| --- | --- | --- |
| Stack of positions with a sentinel | O(n) | O(n) |
| Two counting passes, one in each direction | O(n) | O(1) |
| From every start, extend while the depth stays non-negative | O(n squared) | O(1) |

## Where it shows up

`build-log-bracket-checker` reads a build log and checks that its brackets match. A yes or no on the whole log is `valid-parentheses`; the harder variant is to say where the log is damaged and how much of it is intact, which needs positions on the stack and not only the bracket characters. That is the step this question adds: the stack holds indexes, and the unmatched ones mark the cuts.

## Follow-up answers

**Can you do it in constant extra space?** Yes, with the two counting passes. Left to right, count `(` and `)`; when the counts are equal the stretch so far is balanced, so record twice the count; when `)` outnumbers `(`, reset both to zero. That pass misses strings like `(()` where `(` is always ahead, so repeat from the right with the roles swapped.

**How would you return where the longest stretch starts, as well as its length?** The start is `stack.peek() + 1` at the moment the maximum is set. Store it alongside the best length.

**How would you handle several kinds of brackets, like round and square together?** The stack must also remember which kind each opener was, and a closer matches only when its kind agrees with the opener on top. An unmatched or mismatched closer is a cut, as before. The index trick for lengths stays the same.
