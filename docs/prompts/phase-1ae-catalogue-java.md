# Phase 1ae: catalogue growth in Java, every problem covering two or three NeetCode questions

## Context to read first

`CLAUDE.md`; `docs/plan/PLAN.md` sections 7.1 (problem format), 7.3 (coverage map), 7.4 (authoring pipeline), 10 (Phase 1ae row); `docs/contracts/problem-format.md`; the ten existing problems in `content/problems/` as the reference shape; `content/dsa/INDEX.md` and the DSA questions from Phase 1ad; `content/free/free.json`.

## Goal

The launch catalogue grows from 10 to 24 Java coding-round problems, and every problem, old and new, names the two or three NeetCode 150 questions its implementation exercises and links to them in the app.

## In scope

- 14 new problems from the coverage map in `PLAN.md` 7.3 (take the Phase 2 pack B and Phase 3 rows first), each a three-phase scenario in a domain, Java only, with brief, hints at three levels per phase, starter, reference, naive, grading notes and a walkthrough in the Phase 1a structure. Each problem's phases are designed so that the solution uses two or three distinct NeetCode 150 techniques (for example a trie plus a heap plus a sliding window), named in `problem.json` under `covers[]` as DSA question ids, with `tags[]` to match.
- The ten existing problems get `covers[]` (two or three ids each, chosen from what their reference solutions actually do) and their walkthroughs' practice-links section lists those questions as `dsa://` chips.
- The verify tool checks `covers[]` both ways (every id exists in `content/dsa/`, and the question's `coveredBy[]` lists the problem) and that every problem has at least two ids.
- The section page and the problem card show "Covers: Two Sum, Top K Frequent, ..." as chips that open the DSA question; the DSA question page shows "Used in: Merchant Suggest, ...".
- Pack files: `content/packs/launch.json` listing the 22 paid problems and `free.json` unchanged (ADR 0001), both read by the dashboard's free-list marker; no encryption yet.

## Out of scope

Any language other than Java; the authoring workbench; packs beyond the two JSON files; backend, accounts, payments; engine or shell code beyond reading `covers[]`.

## Constraints

- Content PRs touch only `content/problems/<id>/`, `content/dsa/<id>/problem.json` (the `coveredBy[]` line) and the pack files. Land problems in batches of 3 to 5, each verified three ways on CI before the next.
- Every new problem is verified against the real toolchain on Windows CI; the macOS job builds.
- No wording copied from NeetCode or LeetCode; scenarios, data files and tests are original.
- Timing: the reference stays at least 5x under every phase-3 limit on CI (the Build-Log Bracket Checker note in `docs/status.md` is the cautionary tale).

## Exit criteria

1. 24 problems verify three ways on CI; each has `tags[]` and two or three `covers[]` ids; the verify tool proves both directions.
2. Every walkthrough's practice section links only to in-app DSA questions; a card and a question page show the cross-links in both themes (screenshots).
3. The coverage map in `PLAN.md` 7.3 marks the 24 as written with their `covers[]`, and the 18 topic groups each have at least one problem covering them.
4. Every Phase 1ad gate is green.

## Gates

Content rows of `docs/process/definition-of-done.md` for every problem; the Phase 1a rows for the small UI change.

## Deliverables

Content; `content/packs/launch.json`; `docs/contracts/problem-format.md` (`covers[]`); `PLAN.md` 7.3; `CHANGELOG.md`; `docs/status.md`; `docs/runbooks/commands.md`; authoring time per problem in `docs/retro/phase-1ae.md`.

## Report

What shipped, problems landed with their `covers[]`, exit criteria met or not with evidence, problems whose limits had to move, what the owner should play through by hand, what moved to "Later".
