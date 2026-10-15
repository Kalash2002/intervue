# Problem format, formatVersion 1

A problem on disk (PLAN.md 7.1). The reader and validator in `shared-java/problem-format` enforce this; the content verify tool refuses a problem that breaks it. Changing a field's meaning bumps `formatVersion` and needs an ADR.

## Layout

```
content/problems/<problem-id>/
├── problem.json
├── brief/intro.md, brief/phase1.md … phaseN.md
├── hints/phase1.md … phaseN.md
├── walkthrough.md
├── grading/NOTES.md
└── variants/<language>/
    ├── variant.json
    ├── starter/      the project the candidate gets, plus data/
    ├── reference/    files that overlay starter/ so every phase passes
    └── naive/        files that overlay starter/: every phase but the last passes, the last times out (one-phase problems: see "One-phase problems")
```

DSA questions live in `content/dsa/<question-id>/` with the same shape and one phase (below). The engine reads both roots, `content/dsa/` being optional; ids are unique across them (ADR 0008).

## problem.json

| Field | Type | Rule |
|---|---|---|
| `formatVersion` | integer | `1` |
| `id` | string | equals the folder name; lowercase letters, digits and `-` |
| `version` | integer | bumped on every change to the problem |
| `section` | string | `coding-round` or `dsa-quick` |
| `title`, `summary`, `difficulty`, `domain` | string | non-empty; `difficulty` is `Easy`, `Medium` or `Hard` |
| `techniques` | string array | track ids, at least one |
| `tags` | string array, optional (added 2026-10-15, additive) | free-text tags shown as chips and used by the section page's filters; each `[a-z0-9]+(-[a-z0-9]+)*`, no repeats; absent means empty |
| `minutes` | integer | Interview time limit |
| `phases` | array | ids `1..N` in order, N at least 1 |
| `phases[].title`, `.brief`, `.hints` | string | `brief` and `hints` are paths that exist |
| `phases[].weight` | integer | phase weights sum to 70 (the correctness share; 80 for a one-phase problem) |
| `phases[].allOrNothing` | boolean | |
| `phases[].timeoutRefMs` | integer | whole-run cap for that phase on the reference machine |
| `coveredBy` | string array, optional (added 2026-10-15, additive) | DSA questions only: ids of coding-round problems that exercise this question; each exists in `content/problems/` |
| `followUps` | string array | |
| `languages` | string array | each has a `variants/<language>/` folder |

## One-phase problems (DSA, added 2026-10-15, additive, no `formatVersion` bump)

A problem with exactly one phase differs from the multi-phase rules in this file:

| Item | Rule |
|---|---|
| Phase | one, weight `80` (not 70); its brief is `statement.md`; hints are `hints/phase1.md` |
| `brief/intro.md` | not present |
| `grading/NOTES.md` | still required |
| Walkthrough headings, in order | `## Approach`, `## Complexity`, `## Where it shows up`, `## Follow-up answers` |
| `naive/` | optional. Without it time is not graded separately and `grading/NOTES.md` says so. With it, the naive solution passes every test that did not time out, times out at least once, and still times out at 3x the limits (`NAIVE_MARGIN`) |
| Reference | keeps the 5x headroom rule |

Problems with several phases keep every other rule. The shape for a new question is `content/templates/dsa-question.md`.

## variant.json

| Field | Rule |
|---|---|
| `language` | equals the folder name |
| `minToolchain` | string version |
| `main` | the demo entry point |
| `readOnly` | paths inside `starter/` that exist; restored before every run, never editable |
| `tests` | `{"<phaseId>": "<test class>"}` for every phase |
| `verifiedAt`, `verifiedWith` | written by the verify tool |
| `referenceMs` | `{"<Suite.test>": ms}` written by the verify tool for the last phase |

## walkthrough.md

The answer the app shows after a session and from every problem card. A one-phase problem uses the four headings above instead of the closing sections below. Its shape is `content/templates/walkthrough.md`; the validator enforces the part that holds the structure (added 2026-10-15, additive):

| Rule | Detail |
|---|---|
| Closing sections, in this order | `## DSA reference` with `### Theory`, `### Code`, `### Complexity`, `### Practise the technique`; then `## Time budget`; then `## Follow-up answers` |
| Figures | `![caption](figures/<name>.svg)` (or `.png`) refers to a file under the problem's `figures/`; the engine inlines them for the Answer page |
| Practise links | `dsa://<question-id>` only; ids as in `content/dsa/INDEX.md`; each exists in `content/dsa/` |
| Other sites | no `neetcode.io` or `leetcode.com` link anywhere under `content/` |
| Prompts | ```text fences are what the candidate types; the voice rules are in `content/templates/README.md` (not validated) |

## Hints

`hints/phase<N>.md` exists for every phase and has three sections, `## Level 1` (where to look), `## Level 2` (the approach) and `## Level 3` (concrete steps), each non-empty. Hints contain no code fences.

## Time limits

Per-test `timeoutMs` in the test files and `timeoutRefMs` are milliseconds on the reference machine, scaled by the calibration factor at run time. The verify tool enforces: limit at least 5 times the reference solution's time, and the naive solution at least 3 times over the limit. A one-phase problem's naive solution is optional and follows its own rule (see "One-phase problems").
