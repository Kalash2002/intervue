# 0008: DSA section, one-phase problems, a second content root, discovered section lists

Date: 2026-10-15. Status: accepted; superseded in part by 0012 (the DSA statement and answer shapes and the section-id keying).

## Context

ADR 0007 puts a DSA section (Phase 1ad) before the catalogue growth. Three things in the code stop a second section from being only a new package:

- `desktop/src/main.tsx` hardcodes `sections={[codingRoundUi]}`, and `engine/build.gradle.kts` names the coding-round project. A new section would edit `desktop/` and `engine/`, against the rule that section packages never do.
- The engine resolves problems from `content/problems` only (`EngineMain.java:64`).
- Problem format 1 assumes several phases: weights sum to 70, `brief/intro.md` is required, the walkthrough ends in the DSA reference, and a naive solution passes every phase but the last. A DSA question is one statement, one set of tests and one walkthrough.

## Decision

- **Section.** `dsa-quick`, `name` "DSA", `runnerKind` `code-tests`, `graderKind` `tests`, `uiMode` `editor`; channels ASK and WRITE_CODE; `supportsHints()` true; result parts `score`, `tests`, `time`, `walkthrough`. Score: correctness 80 x passed / total (timeouts and failures count as not passed); time 20 only when every test passed within its limit. Interview and Practice are labelled as in ADR 0006.
- **One-phase problems, no `formatVersion` bump.** The change is additive. A problem with exactly one phase has weight 80, `statement.md` as its brief, `hints/phase1.md`, and no `brief/intro.md`; `grading/NOTES.md` is still required. Its walkthrough headings, in order, are `## Approach`, `## Complexity`, `## Where it shows up`, `## Follow-up answers` (superseded by ADR 0012: the DSA statement and answer shapes, and keying the rules on section id `dsa-quick` instead of phase count, are in `docs/contracts/problem-format.md`). `naive/` is optional. Without one, time is not separately graded and `grading/NOTES.md` says so. With one, the naive solution must pass every test that did not time out, time out at least once, and still time out at 3x the limits (`NAIVE_MARGIN`); the reference keeps the 5x headroom rule. Problems with several phases keep today's rules (weights sum to 70, intro required, the DSA reference headings).
- **`coveredBy[]`.** An optional list of coding-round problem ids in a DSA question's `problem.json`. The verify tool proves that every `coveredBy` id exists in `content/problems/`, every `dsa://<id>` link in `content/**/*.md` exists in `content/dsa/`, no id exists in both roots, and no `neetcode.io` or `leetcode.com` link appears under `content/`.
- **Two content roots.** The engine reads `content/problems/` and `content/dsa/` (the latter optional). Ids are unique across both. Question ids are LeetCode slugs (Phase 1ac, `content/dsa/INDEX.md`).
- **Local API.** `ProblemSummary` gains `coveredBy: string[]`. No contract file exists for the local API, so this ADR is its record.
- **Section lists discovered.** The UI's list of `SectionUi` moves to a new package `@intervue/sections` (`packages/sections`). Gradle includes and bundles every `sections/*/java` project by discovery.

## Consequences

- This phase edits `desktop/src/main.tsx`, `desktop/package.json`, `desktop/src-tauri/tauri.bundle.json` (it bundles `content/dsa`) and `engine/build.gradle.kts` once. From the next section on, exit criterion 1's "zero desktop and engine edits" holds. A new section is not one folder plus one line, though: it adds its folder under `sections/`, one line in `packages/sections/src/index.ts`, a dependency in `packages/sections/package.json` and the matching entry in the root `pnpm-lock.yaml`. `.github/workflows/parts.yml` has no part label that allows both `sections/` and `packages/`, and none that allows the lockfile, so such a PR needs a label rule change or an exception.
- The owner chose this one-time fix on 2026-10-15 over hardcoding a second entry.
- `docs/contracts/problem-format.md` and `docs/contracts/section-contract.md` carry the new rules. The validator branches on phase count, which is the cost of not bumping `formatVersion`.
- A problem id can no longer be reused between the two roots; the verify tool is the guard.
