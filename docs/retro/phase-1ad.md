# Phase 1ad retro

Cost here is counted in dispatches and fix rounds, because the session did not record token counts.

## Code tasks (0 to 8)

Nine tasks, two needed a fix round: Task 2 (a one-phase `naiveRule` accepted an ERROR status) and Task 6 (the output pane showed "Run" and offered "Main (demo)" on the DSA page). Review caught both. The Task 11 evidence run then found two more bugs, fixed in the same task (see the lessons). Two rulings saved later work: engine bundling by discovery, and one jar per section named by section id (every section project is called `java`, so the second jar would have overwritten the first).

## Content batches

| Batch | Questions | Fix rounds | Notable review findings |
|---|---|---|---|
| 9a | 8 (the first referenced inside the 150) | 0 | Stock example `([)]` in valid-parentheses; top-k-frequent opening close to the well-known wording. Kept, noted. |
| 9b | 8 | 1 | word-ladder reference took 430 to 450 ms against a 2400 ms limit, so the 5x headroom rule was barely met; limit raised. |
| 10a | 7 (outside the 150) | 1 (three items) | Stock LeetCode examples in car-pooling, longest-valid-parentheses and contains-duplicate-ii replaced; meeting-rooms-iii naive only 1.26x past the 3x margin; contains-duplicate-ii naive was 2.4x past it and sensitive to SIMD. |
| 10b | 6 (outside the 150) | 1 | process-tasks-using-servers int-overflow test never left the int range; fixed, and the large-test limit raised from 2500 to 3500 ms for headroom. |

Total: 29 questions, 3 content fix rounds, no question dropped. Across the phase: 2 code fix rounds (Tasks 2 and 6), 3 content fix rounds, and 2 Task 11 fixes (the room grid in 5cbd8fc, the solution file in 5d9db1e), 7 in all.

## Content batches 4a to 16b (branch `phase-1ad-batch4`)

Each batch cost one author dispatch, one review dispatch and one fix round (the author resumed); none needed a second round. The controller re-read every fix diff, hand-checked every changed value and ran verify itself when the author could not.

| Batch | Questions | Notable review findings |
|---|---|---|
| 4a | 6 binary search | find-min Better claims overstated. |
| 4b | 5 linked list | The cycle test reused LeetCode's Example 2. |
| 4c | 6 linked list | merge-k's empty-list example was LeetCode's. |
| 5a | 6 trees | Tree height capped at 1,000 so the natural recursion passes; subtree naive dropped; LeetCode tree shapes. |
| 5b | 6 trees | kth-smallest had to state the scale that rules out a full in-order walk. |
| 5c | 3 trees | serialize Example 1 had LeetCode's shape. |
| 6 | 6 heap | median's addNum(1), addNum(2) was LeetCode's. |
| 7a | 5 backtracking | subsets-ii [0] untested; combination-sum-ii close to LC 40; reference near limit/8. |
| 7b | 5 backtracking | Lists compared through `toString` hid `[]` against `[""]`. |
| 8 | 2 tries, 4 graphs | A per-word search from the rarer end passed the timed test; clone-graph time ungraded (60 s limits). |
| 9 | 4 graphs | Course timed tests beaten by repeated in-place passes; walls Example 1 wrong. |
| 10 | 3 graphs, 2 advanced graphs | Minor wording and figures. |
| 11 | 2 advanced graphs, 4 1-D DP | A clipped figure legend; a Follow-up named the method. |
| 12a | 4 1-D DP | coin-change examples close to LeetCode's and to the textbook greedy counterexample. |
| 12b | 4 1-D DP | partition Example 2 was Graham's greedy counterexample; LIS "Prefer it when" false. |
| 13a | 5 2-D DP | An LCS figure legend contradicted its cells; a memo's "Prefer it when" false. |
| 13b | 3 2-D DP | Interleaving timed cases failed only at the last card, so a mirrored brute force passed in 7 ms. |
| 13c | 3 2-D DP | The same in regex; `colou*r` was a folk example. |
| 14a | 4 greedy | A jump-game follow-up answer was wrong. |
| 14b | 4 greedy | Seven minor prose and test-comment slips. |
| 15a | 3 intervals, 2 math | The overlap definition was true of every pair. |
| 15b | 6 math | plus-one's Follow-up gave away the method and its all-nines test was stock. |
| 16a | 4 bits | single-number's timed margin was 5 to 8 percent; 1 s limits too tight for correct string solutions. |
| 16b | 3 bits | The same 1 s limit in sum-of-two-integers; the carry loop had to be named as the expected answer. |

Total: 114 questions, 24 batches, 72 dispatches, 24 fix rounds, no question dropped. A final full verify found one more fix: surrounded-regions' depth test needed a 30 s limit, because the naive's time sat near the 5 s default and load turned it into a timing test.

Lessons from these batches:

- A timed false case must fail mid-input. When it fails only at the last element, a brute force mirrored to start from that end rejects it at once (found twice).
- Run each slower variant the walkthrough names against the timed test, not only `naive/`, and aim for 2x past 4.5 times the limit, measured on an idle machine.
- Tests that are not about speed need limits far above any correct solution's time, string-based ones included, or a busy machine turns them into timing tests.
- "Prefer it when" and complexity lines must be true of the code shown, not of the idea.
- Inputs the constraints force (an empty string, a 1x1 grid, `Integer.MIN_VALUE`) are tested even when they match a classic sample.
- One gradle verify at a time; a second one beside it is enough to miss the headroom rule.

## Lessons for the next batches

- Split a batch at 8 questions per dispatch. Sixteen full questions do not fit one agent's context.
- Tell the author up front: no stock examples, write own values. Most of the three content fix rounds were this or a margin.
- Measure the naive solution, not only the reference. A naive that finishes in 1.26x of the limit is a flaky test on a fast runner. Aim for 5x or more past the limit on the reference machine, and avoid naives whose speed depends on SIMD.
- An overflow test must assert that the input really leaves the int range; check the arithmetic in the test, not only its name.
- Level 3 hints drifted toward full recipes in every batch. Say in the dispatch what Level 3 may and may not give away.
- Pin `techniques[0]` to the 18 NeetCode group ids in the dispatch, and list each question's group, or the section page groups it wrongly.
- Check `content/dsa/NEETCODE-150.md` against the published list before batch 2; it was written from memory.
- Evidence check from Task 11: the Interview session forced `timed: true` even when the request said false (by design, Interview is timed). Practice stayed untimed.
- The Task 11 screenshots found a real layout bug that unit tests could not: the DSA room collapsed to 50px at 1366x768 because it has no timeline row. Look at the pixels of every new page before calling a UI task done. The same run found that the room opened `Main.java` with no way to reach `TwoSum.java`; Task 11 fixed it in 5d9db1e (the room opens the solution file, with tabs when several qualify).
