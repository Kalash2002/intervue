# Phase 1ae: the catalogue in NeetCode order, every question covered by a scenario problem

## Context to read first

`CLAUDE.md`; `docs/plan/PLAN.md` sections 7.1 (problem format), 7.3 (coverage map), 7.4 (authoring pipeline), 10 (Phase 1ae row); `docs/contracts/problem-format.md` including the walkthrough section; `content/templates/walkthrough.md` and its README (the shape, the prompt voice, the figure rules); `content/dsa/NEETCODE-150.md` (the authoring queue, in roadmap order); `content/dsa/INDEX.md`; the ten existing problems as the reference shape; the DSA questions from Phase 1ad; `content/free/free.json`.

## Goal

Every one of the 150 NeetCode questions is covered by a scenario problem in the AI-assisted coding section, the problems are written in the roadmap's order so that working through them meets the same patterns in the same sequence as the DSA list, and the launch catalogue is the first stretch of that order.

## In scope

- **Order of authoring.** Walk `content/dsa/NEETCODE-150.md` top to bottom: Arrays and hashing first, then Two pointers, Sliding window, Stack, Binary search, Linked list, Trees, Tries, Heap, Backtracking, Graphs, Advanced graphs, 1-D DP, 2-D DP, Greedy, Intervals, Math and geometry, Bit manipulation. Each new problem takes the next two or three uncovered questions of the current group and is designed so that its three phases make the candidate use those techniques in that order (phase 1 the simplest, phase 3 the hardest); `covers[]` names them. A question that fits no scenario with its neighbours gets a smaller problem of its own; no question is skipped. The ten existing problems keep their slots (the "Covered by" column) and get `covers[]` from what their reference solutions do.
- **Catalogue size.** The queue yields about 50 problems in all (150 questions, two or three per problem). The launch catalogue is the first 24 in order (the existing ten plus 14 new ones, which cover Arrays and hashing through the start of Trees); the rest are written in later batches in the same order until the table has no empty "Covered by" cell. `PLAN.md` 7.3's domain ideas are used where they fit the next questions, otherwise a new scenario is invented; every scenario is original work, no wording from NeetCode or LeetCode.
- **Each problem** is a three-phase Java scenario in a domain with brief, hints at three levels per phase, starter, reference, naive, grading notes, tags, and a walkthrough in the template shape: interview-voice prompts, a DSA reference whose figure shows the structure with the problem's own values, and practise links to exactly the questions in `covers[]`.
- **Cross-links.** `covers[]` on every problem; `coveredBy[]` on every DSA question; the verify tool checks both directions, that every problem has two or three ids, and that the ids are consecutive entries of one group in the queue (so the order holds). The section page's cards show "Covers: ..." chips that open the question; the DSA question page shows "Used in: ...". `NEETCODE-150.md` is updated in the same PR as each problem.
- **Section order.** The AI-assisted coding page lists problems in queue order within each group (the grouped view already follows the group order); `nextUp` picks the next unsolved in that order, so Home's "Next:" walks the roadmap.
- **Pack files.** `content/packs/launch.json` listing the 22 paid problems of the first 24; `free.json` unchanged (ADR 0001).

## Out of scope

Any language other than Java; the authoring workbench; packs beyond the two JSON files; backend, accounts, payments; engine or shell code beyond reading `covers[]` and the queue order.

## Constraints

- Content PRs touch only `content/problems/<id>/`, `content/dsa/<id>/problem.json` (the `coveredBy[]` line), `content/dsa/NEETCODE-150.md` and the pack files. Land problems in batches of 3 to 5 in queue order, each verified three ways on CI before the next.
- Verify on a quiet machine or on CI: the 5x headroom rule fails under load. The reference stays at least 5x under every phase-3 limit.
- No wording copied from NeetCode or LeetCode; scenarios, data files and tests are original.

## Exit criteria

1. Every row of `content/dsa/NEETCODE-150.md` has a "Covered by" problem, and every `covers[]` is a run of consecutive questions of one group; the verify tool proves both directions and the order.
2. All problems verify three ways on CI; each has `tags[]`, a figure, interview-voice prompts and practise links equal to its `covers[]`.
3. The section page lists problems in queue order and Home's "Next:" follows it (component test).
4. The first 24 problems are the launch catalogue; `launch.json` names 22 of them; the coverage map in `PLAN.md` 7.3 is replaced by a pointer to the queue.
5. Every Phase 1ad gate is green.

## Gates

Content rows of `docs/process/definition-of-done.md` for every problem; the Phase 1a rows for the small UI change.

## Deliverables

Content; `content/packs/launch.json`; `docs/contracts/problem-format.md` (`covers[]`, consecutive-run rule); `PLAN.md` 7.3; `CHANGELOG.md`; `docs/status.md`; `docs/runbooks/commands.md`; authoring time per problem in `docs/retro/phase-1ae.md`.

## Report

What shipped, the queue's coverage count, problems landed with their `covers[]`, exit criteria met or not with evidence, problems whose limits had to move, what the owner should play through by hand, what moved to "Later".
