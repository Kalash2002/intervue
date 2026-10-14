# How a strong round goes: Build-Log Bracket Checker

**Read this first.** This is one good way through the problem, not the only one. The prompts are written for interview mode, where the assistant does what you specify and nothing more.

## Minutes 0 to 4: read before you run

1. `BracketChecker.isBalanced`: a stack of characters; openers push, closers pop and compare. Read the line after the loop.
2. `unbalancedLines` reports 1-based line numbers through `isBalanced`.
3. `data/build.log`: six lines; line 3 never closes its first bracket.

## Phase 1: unclosed lines pass (minutes 4 to 8)

Run phase 1. Stray and mismatched closer tests pass; `anOpenerLeftOpenFails` fails. Say: "Errors inside the loop are caught, but nothing checks what is left on the stack at the end." Return `open.isEmpty()`.

## Phase 2: point at the error (minutes 8 to 28)

List the rules aloud in the order they apply: quote state first, then closers during the scan, then unterminated quote, then the earliest open bracket. Work two examples by hand: `"(](("` gives a mismatch at column 1; `"()(x"` gives unclosed at column 2.

```text
Implement BracketChecker.firstError(line) as one pass with an index i. Track the column of an
open double quote (or -1); inside quotes skip the character after a backslash. Outside quotes,
push opener columns on an int[] stack; on a closer return UNEXPECTED_CLOSER when the stack is
empty or MISMATCH when the top does not match. After the loop return UNCLOSED_STRING at the
quote column if a quote is open, else UNCLOSED at the bottom of the stack. Then make isBalanced
return firstError(line).isEmpty().
```

Check the reply against the order of rules and the bottom-of-stack column.

## Phase 3: huge lines (minutes 28 to 35)

With a stack, phase 3 passes. If the matcher looks backwards, the deep line times out: "a million closers each walking back up to a million characters."

## Complexity

O(n) time and O(depth) memory per line.

## Follow-up answers

- A stack is right because brackets close in the reverse order they open; counters lose the order and accept `([)]`.
- To add single quotes and `//` comments, turn the quote flag into a small state machine (code, double-quoted, single-quoted, comment) with one switch per state.
