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
    └── naive/        files that overlay starter/: every phase but the last passes, the last times out
```

## problem.json

| Field | Type | Rule |
|---|---|---|
| `formatVersion` | integer | `1` |
| `id` | string | equals the folder name; lowercase letters, digits and `-` |
| `version` | integer | bumped on every change to the problem |
| `section` | string | `coding-round` in Phase 1a |
| `title`, `summary`, `difficulty`, `domain` | string | non-empty; `difficulty` is `Easy`, `Medium` or `Hard` |
| `techniques` | string array | track ids, at least one |
| `minutes` | integer | Interview time limit |
| `phases` | array | ids `1..N` in order, N at least 1 |
| `phases[].title`, `.brief`, `.hints` | string | `brief` and `hints` are paths that exist |
| `phases[].weight` | integer | phase weights sum to 70 (the correctness share) |
| `phases[].allOrNothing` | boolean | |
| `phases[].timeoutRefMs` | integer | whole-run cap for that phase on the reference machine |
| `followUps` | string array | |
| `languages` | string array | each has a `variants/<language>/` folder |

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

## Hints

`hints/phase<N>.md` exists for every phase and has three sections, `## Level 1` (where to look), `## Level 2` (the approach) and `## Level 3` (concrete steps), each non-empty. Hints contain no code fences.

## Time limits

Per-test `timeoutMs` in the test files and `timeoutRefMs` are milliseconds on the reference machine, scaled by the calibration factor at run time. The verify tool enforces: limit at least 5 times the reference solution's time, and the naive solution at least 3 times over the limit.
