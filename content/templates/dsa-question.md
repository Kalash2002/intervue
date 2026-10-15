# Template for a DSA question

Questions live in `content/dsa/<question-id>/`, one folder each. The id is the LeetCode slug (`content/dsa/INDEX.md`). The rules are in `docs/contracts/problem-format.md`, "One-phase problems". Any change to the folder bumps `version` in `problem.json`.

## Folder

```
content/dsa/<question-id>/
├── problem.json          one phase, weight 80; optional coveredBy[]
├── statement.md          the brief
├── hints/phase1.md       ## Level 1, ## Level 2, ## Level 3
├── walkthrough.md
├── grading/NOTES.md      required; says so when there is no naive/
└── variants/java/
    ├── variant.json
    ├── starter/
    ├── reference/
    ├── naive/            optional
    └── tests/
```

## Rules

- **Statement.** Original wording and your own examples. Do not copy text from NeetCode or LeetCode, and do not link to either site.
- **Techniques.** `techniques[0]` is the topic group's track id from `packages/ui/src/lib/tracks.ts`.
- **Naive.** Add one only when a slower approach is worth showing. It must pass every test that did not time out, time out at least once, and still time out at 3x the limits.
- **Links.** Practise links are `dsa://<question-id>` only. `coveredBy` lists coding-round problem ids that exist in `content/problems/`.
- **Voice.** As in `content/templates/README.md`: plain and short, and the walkthrough says why.

## walkthrough.md skeleton

```markdown
## Approach

<The idea in a few sentences, then the routine in plain Java, one comment per decision.>

## Complexity

| Approach | Time | Extra space |
| --- | --- | --- |
| <naive> | O(...) | O(...) |
| <this one> | O(...) | O(...) |

## Where it shows up

<Where this pattern appears in real code, and the coding-round problems that use it.>

## Follow-up answers

**<Follow-up question from problem.json>** <The answer in two to four sentences.>
```
