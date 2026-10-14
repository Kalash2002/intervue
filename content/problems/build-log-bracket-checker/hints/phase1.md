## Level 1

Stray and mismatched closers are caught inside the loop. Think about what is still on the stack when the loop ends, and what the method returns then.

## Level 2

After the last character, every bracket that was opened and never closed is still on the stack. The method returns true without looking, so any line whose only problem is an unclosed bracket counts as balanced.

## Level 3

1. Open `BracketChecker.isBalanced` and find the return after the loop.
2. Return whether the stack is empty instead of returning true.
3. Leave the checks inside the loop alone; they already handle stray and mismatched closers.
4. Run Phase 1 tests: line 3 of the sample log should now be reported.
