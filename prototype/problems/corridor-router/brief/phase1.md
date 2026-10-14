## Phase 1: fix the bug

Operations report that `fewestHops` sometimes returns a route with more corridors than necessary. On the sample network it sends INR to USD through a third node although a direct corridor exists.

Run **Phase 1 tests**. Some fail.

- Find the cause and fix it. Keep the change small.
- Be ready to explain what `fewestHops` does, why it was wrong, and its time and space cost.
