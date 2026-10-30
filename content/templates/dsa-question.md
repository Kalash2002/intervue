# Template for a DSA question

Questions live in `content/dsa/<question-id>/`, one folder each. The id is the LeetCode slug (`content/dsa/INDEX.md`). The rules are in `docs/contracts/problem-format.md`, "One-phase problems". Any change to the folder bumps `version` in `problem.json`.

## Folder

```
content/dsa/<question-id>/
├── problem.json          one phase, weight 80; optional coveredBy[]
├── statement.md          the brief (dsa-statement.md)
├── hints/phase1.md       ## Level 1, ## Level 2, ## Level 3
├── walkthrough.md        (dsa-walkthrough.md)
├── figures/              at least one SVG
├── grading/NOTES.md      required; says so when there is no naive/
└── variants/java/
    ├── variant.json
    ├── starter/
    ├── reference/
    ├── naive/            optional
    └── tests/
```

## Rules

- **Hints.** Three levels, available in Interview and Practice; DSA has no assistant (ADR 0012).
- **Statement.** Original wording and your own examples. Do not copy text from NeetCode or LeetCode, and do not link to either site.
- **Techniques.** `techniques[0]` is the topic group's track id from `packages/ui/src/lib/tracks.ts`.
- **Naive.** Add one only when a slower approach is worth showing. It must pass every test that did not time out, time out at least once, and still time out at 3x the limits.
- **Links.** Practise links are `dsa://<question-id>` only. `coveredBy` lists coding-round problem ids that exist in `content/problems/`.
- **Voice.** As in `content/templates/README.md`: plain and short, and the walkthrough says why.

## Templates

- `statement.md` follows `content/templates/dsa-statement.md`: title, description, `## Example 1` to `## Example N`, `## Constraints`, optional `## Follow-up`.
- `walkthrough.md` follows `content/templates/dsa-walkthrough.md`: the question, the thinking, brute force, better, optimal, one complexity table, where it shows up, follow-up answers, and a figure under `figures/`.
- The starter's editable files import `java.util.*`.
