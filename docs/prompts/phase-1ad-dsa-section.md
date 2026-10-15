# Phase 1ad: DSA section with the NeetCode 150 set in Java

## Context to read first

`CLAUDE.md`; `docs/plan/PLAN.md` sections 2.6 (section interfaces, the DSA row), 5.11 (the section worked example), 7.2 (taxonomy), 10 (Phase 1ad row written in 1ac); `docs/contracts/section-contract.md`, `problem-format.md`, `run-protocol.md`; `sections/coding-round/` as the reference section; `content/dsa/INDEX.md` from Phase 1ac; the Phase 1ac shell (`packages/ui`, section registry).

## Goal

A DSA entry in the side menu opens a page listing the NeetCode 150 questions by topic group; each opens a solving page with the statement, a Java editor, Run tests and a walkthrough; and every `dsa://` chip in a coding-round walkthrough opens the question it names.

## In scope

- `sections/dsa-quick/`: `section.json` (id `dsa-quick`, title "DSA"), a single-phase problem schema under the problem format (one phase, `tests/` only, no `data/`, no file tree), the grader reusing the Java test kit through the SPI with the scoring rule correctness 80 and time 20, the solving page (statement left, editor right, output below, Run tests, Finish, hints in Practice, the assistant panel with the same rules as the coding round) and result parts (score, tests passed, time, link to the walkthrough), discovered by the registry from 1ac with no shell edit.
- `content/dsa/<question-id>/`: the NeetCode 150 questions, Java only, ids the NeetCode slugs. Each: `problem.json` (title, topic group as `techniques[0]`, difficulty, `tags[]`, the sibling coding-round problems that cover it under `coveredBy[]`), `statement.md` (original wording, own examples, no text copied from NeetCode or LeetCode), `variants/java/starter`, `reference` and `naive` where a slower correct solution exists, `tests/` with the kit, a `walkthrough.md` in the coding-round structure cut to one phase (approach, complexity table, the coding-round problems that use it, follow-ups). Write them in the order of `INDEX.md`: first every question a walkthrough references, then the rest by topic group. Verify each with `./gradlew :content:tools:verify`, extended to one-phase problems.
- DSA page: topic groups as sections, each question a row with difficulty, tags, status (solved, attempted, new) and the coding-round problems that cover it; filters by tag and difficulty; a "Random unsolved" button.
- `dsa://` chips in coding-round walkthroughs open the question's solving page; the question's walkthrough links back to the coding-round problems in `coveredBy[]`.
- Dashboard tiles count DSA solved separately; sessions and history carry the section id (additive column, migration V3).

## Out of scope

Changes under `desktop/`, `web/`, `backend/`; any language other than Java; new coding-round problems (Phase 1ae); packs, accounts, payments, sync.

## Constraints

- Section discovery through `ServiceLoader` and the registry only; CI's path check fails the PR if `engine/` is touched beyond the additive session column and a problem-format reader for one-phase problems (the latter needs a contract note, not a new format version).
- Content isolation rule: a question folder touches nothing outside itself. The `coveredBy[]` and `dsa://` links are validated by the verify tool (every id exists both ways).
- 150 questions do not fit one PR: land them in topic-group batches of 10 to 20 after the section and the first batch (the referenced questions) are green; the phase closes when all 150 verify.
- Timing limits for the time component come from calibration like the coding round; naive solutions must exceed the limit by 5x on CI, else drop the time component for that question with a note in `grading/NOTES.md`.

## Exit criteria

1. "DSA" appears in the side menu and its page lists 150 questions in the 18 topic groups with zero edits under `desktop/` (CI path check).
2. A full DSA session on `two-sum` in Interview and in Practice: run, finish, score, walkthrough, history row with section id. Evidence: the event log and screenshots in both themes.
3. Every `dsa://` chip in every coding-round walkthrough opens the right question, and every question's `coveredBy[]` points at an existing coding-round problem (verify tool proves both; zero `neetcode.io` or `leetcode.com` links anywhere under `content/`).
4. All 150 questions verify three ways (starter fails, reference passes, naive times out where one exists) on Windows CI; the macOS job builds.
5. Every Phase 1ac gate is green.

## Gates

Rows marked Phase 1a in `docs/process/definition-of-done.md`, plus the content rows for every question.

## Deliverables

Code and tests; `docs/contracts/section-contract.md` and `problem-format.md` (one-phase problems, `coveredBy[]`); ADR if the section SPI needed an additive field; `content/dsa/INDEX.md` kept current; `CHANGELOG.md`; `docs/status.md`; `docs/runbooks/commands.md`; per-batch cost in `docs/retro/phase-1ad.md`.

## Report

What shipped, batches landed and remaining, exit criteria met or not with evidence, the questions whose time component was dropped and why, what the owner should solve by hand, what moved to "Later".
