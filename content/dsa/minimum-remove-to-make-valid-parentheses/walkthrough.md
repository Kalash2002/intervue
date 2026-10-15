# How a strong round goes: Minimum Remove to Make Valid Parentheses

## Approach

A bracket is doomed in exactly two cases: a `)` that arrives when nothing is waiting to be closed, and a `(` that is still waiting when the text ends. Everything else can be kept, so removing exactly the doomed ones is the minimum. What I need while scanning is the set of openers still waiting, with the most recent on top, because a `)` always closes the latest one. That is a stack.

I keep the stack as positions, not characters. A `)` with an empty stack is marked for removal at once. A `)` with something on the stack pops it, and the pair stays. When the scan ends, whatever is left on the stack is never closed, so those positions are marked too. One more pass builds the answer from the positions that are not marked. Counting alone would tell me how many to remove but not which ones, and the answer depends on which ones, as `"(a(b)"` shows: the nearest opener is taken by the `)`, so the first `(` goes.

```java
static String tidy(String text) {
    int n = text.length();
    boolean[] drop = new boolean[n];
    int[] open = new int[n]; // positions of the openers still waiting, latest on top
    int top = 0;
    for (int i = 0; i < n; i++) {
        char c = text.charAt(i);
        if (c == '(') {
            open[top++] = i;
        } else if (c == ')') {
            if (top == 0) {
                drop[i] = true; // nothing to close
            } else {
                top--; // paired with the nearest waiting opener
            }
        }
    }
    while (top > 0) {
        drop[open[--top]] = true; // never closed
    }
    StringBuilder kept = new StringBuilder(n);
    for (int i = 0; i < n; i++) {
        if (!drop[i]) {
            kept.append(text.charAt(i));
        }
    }
    return kept.toString();
}
```

This question differs from its best-known version in one way: that version accepts any balanced result with the fewest removals, while here the pairing rule picks one answer, which is what the reference returns and what the tests expect.

## Complexity

| Approach | Time | Extra space |
| --- | --- | --- |
| Stack of opener positions, then one rebuild pass | O(n) | O(n) |
| For each `)` look back for the nearest unpaired `(` | O(n squared) | O(n) |

## Where it shows up

`build-log-bracket-checker` uses the same stack of positions in its phase 2: `firstError` pushes the column of each opener, so a bad closer is reported at its own column and a leftover opener at the earliest column still on the stack. There it reports the mistake instead of repairing the text, but the part that finds the doomed brackets is the same scan.

## Follow-up answers

**How would you return the number of removed brackets instead of the text?** Run the same scan and count the marks, or just count: the removed `)` are those met with an empty stack, and the removed `(` are what is left on the stack, so the answer is that count plus the stack size at the end. No rebuild pass is needed.

**What changes if square and curly brackets are in the text too?** The stack must also check kinds: a closer pops only if the top is its own kind, and a mismatch makes that closer doomed. With crossed kinds the rule for which bracket to drop needs a stated choice, since "(]" can lose either one.

**Can you do it without a stack, in two passes with counters?** Yes. A left-to-right pass with a counter of open brackets drops every `)` met at zero. A right-to-left pass over what is left drops every `(` that has no `)` after it. The kept brackets are the same as the stack version selects, because the nearest-opener pairing is the same pairing.
