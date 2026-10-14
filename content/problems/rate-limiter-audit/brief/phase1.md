## Phase 1: fix the bug

When the team checks single requests by hand, a few land **one millisecond early**, and window counts near a boundary come out wrong. Most requests are fine.

Run **Phase 1 tests**. Some fail.

- Find the cause and fix it. Keep the change small.
- Be ready to explain why only some timestamps are affected.
