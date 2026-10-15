## Level 1

You need two pieces of state while reading left to right: whether you are inside quotes, and where each still-open bracket started. The earliest still-open bracket is the one at the bottom of your stack.

## Level 2

Keep the column of the opening quote while inside quotes; skip the character after a backslash there; a quote ends the quoted text. Outside quotes, push the column of each opener; on a closer, report an unexpected closer if nothing is open, or a mismatch if the top does not match. After the loop, an open quote wins, then any opener left, reported at the bottom of the stack.

## Level 3

1. Walk the line with an index so you can skip the character after a backslash inside quotes.
2. Store columns, not characters, on the stack; you can read the character back from the line.
3. Return as soon as you meet an unexpected closer or a mismatch; that is the first error.
4. After the loop, check the open quote first, then the stack, using its bottom element for the column.
5. Make `isBalanced` return whether `firstError` is empty, then run Phase 1 and Phase 2 tests.
