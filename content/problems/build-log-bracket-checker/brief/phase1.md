## Phase 1: fix the bug

The CI dashboard missed a crashed test run: its log line ends with an **open bracket that never closes**, and the checker called it balanced. Lines with a stray closing bracket are caught.

Run **Phase 1 tests**. Some fail.

- Find the cause and fix it. Keep the change small.
- Be ready to explain why most tests still passed.
