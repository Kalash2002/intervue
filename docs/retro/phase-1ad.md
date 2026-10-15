# Phase 1ad retro

Cost here is counted in dispatches and fix rounds, because the session did not record token counts.

## Code tasks (0 to 8)

Nine tasks, two needed a fix round: Task 2 (a one-phase `naiveRule` accepted an ERROR status) and Task 6 (the output pane showed "Run" and offered "Main (demo)" on the DSA page). Review caught both. Two rulings saved later work: engine bundling by discovery, and one jar per section named by section id (every section project is called `java`, so the second jar would have overwritten the first).

## Content batches

| Batch | Questions | Fix rounds | Notable review findings |
|---|---|---|---|
| 9a | 8 (the first referenced inside the 150) | 0 | Stock example `([)]` in valid-parentheses; top-k-frequent opening close to the well-known wording. Kept, noted. |
| 9b | 8 | 1 | word-ladder reference took 430 to 450 ms against a 2400 ms limit, so the 5x headroom rule was barely met; limit raised. |
| 10a | 7 (outside the 150) | 1 (three items) | Stock LeetCode examples in car-pooling, longest-valid-parentheses and contains-duplicate-ii replaced; meeting-rooms-iii naive only 1.26x past the 3x margin; contains-duplicate-ii naive was 2.4x past it and sensitive to SIMD. |
| 10b | 6 (outside the 150) | 1 | process-tasks-using-servers int-overflow test never left the int range; fixed, and the large-test limit raised from 2500 to 3500 ms for headroom. |

Total: 29 questions, 3 fix rounds, no question dropped.

## Lessons for the next batches

- Split a batch at 8 questions per dispatch. Sixteen full questions do not fit one agent's context.
- Tell the author up front: no stock examples, write own values. Three of the four fix rounds were this or a margin.
- Measure the naive solution, not only the reference. A naive that finishes in 1.26x of the limit is a flaky test on a fast runner. Aim for 5x or more past the limit on the reference machine, and avoid naives whose speed depends on SIMD.
- An overflow test must assert that the input really leaves the int range; check the arithmetic in the test, not only its name.
- Level 3 hints drifted toward full recipes in every batch. Say in the dispatch what Level 3 may and may not give away.
- Pin `techniques[0]` to the 18 NeetCode group ids in the dispatch, and list each question's group, or the section page groups it wrongly.
- Check `content/dsa/NEETCODE-150.md` against the published list before batch 2; it was written from memory.
- Evidence check from Task 11: the Interview session forced `timed: true` even when the request said false (by design, Interview is timed). Practice stayed untimed.
- The Task 11 screenshots found a real layout bug that unit tests could not: the DSA room collapsed to 50px at 1366x768 because it has no timeline row. Look at the pixels of every new page before calling a UI task done. The same run showed the room has no way to open `TwoSum.java` (see `docs/status.md`, Later).
