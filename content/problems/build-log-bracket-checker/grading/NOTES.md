# Grader notes: Build-Log Bracket Checker

**Phase 1 bug.** `isBalanced` returns `true` after the loop instead of `open.isEmpty()`, so a line whose only fault is an unclosed opener passes. Stray and mismatched closers are caught inside the loop, which is why most tests pass. Fix: `return open.isEmpty()`.

**Phase 2.** One left-to-right pass with a quote state and a stack of columns. Ordering of errors: the first closer error while scanning wins; at the end, an unterminated quote beats open brackets; open brackets are reported at the earliest (bottom of the stack), not the most recent. Backslash escapes only matter inside quotes. `isBalanced` must delegate to `firstError`, or `isBalancedAgreesWithFirstError` fails.

**Phase 3.** A 2,000,001-character line nested 1,000,000 deep. A backward search for the matching opener is O(n^2) and times out; a stack is O(n). A `Deque<Integer>` passes but boxes a million integers; an `int[]` stack is the tidy answer. The 200,000-line log is linear for any per-line approach.

**Common assistant mistakes to watch for.** Counters per bracket type (accepts `([)]`); reporting UNCLOSED at the most recent opener; treating a backslash outside quotes as an escape; checking the stack before the open quote at the end; leaving `isBalanced` with its own logic so the two disagree.
