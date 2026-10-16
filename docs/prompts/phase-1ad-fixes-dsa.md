# Phase 1ad fixes: the DSA room without the assistant, hints, LeetCode-shaped statements, answers from brute force to optimal, imports

## Context to read first

`CLAUDE.md`; `docs/status.md` (the "Phase 1ad hand test" paragraph); `docs/adr/0008-dsa-section-one-phase-problems.md`, `0009-java-system-before-accounts.md`; `docs/contracts/problem-format.md`, `section-contract.md`; `sections/dsa-quick/` (`SolveRoom.tsx`, `section.json`, the Java package); `content/dsa/two-sum/` as the reference question; `content/templates/`; `packages/editor/src/completion.ts` and `languages/java/editor/`; a NeetCode or LeetCode problem page, read through the browser, for the statement shape only (never the text).

## Goal

A DSA question reads like the pages candidates already know, is solved without an assistant but with hints, its answer teaches from the brute force up to the optimal approaches, and a `Map` typed in the editor compiles.

## In scope

- **No assistant in DSA.** `dsa-quick`'s `channels()` is empty; the solving room drops the Assistant tab and shows Hints in both session types (a DSA question is practice by nature, so the three authored levels are available in Interview sessions too; the hint events still count on the result page). The coding round is unchanged. Section contract: a section with no channels renders no assistant panel and the engine refuses `ask`, `write` and `hints/explain` for its sessions.
- **Statement shape.** `statement.md` follows a fixed structure, written into `content/templates/dsa-statement.md` and enforced by the validator for `dsa-quick`: a title, the description, `## Example 1` ... `## Example N` each with `**Input:**`, `**Output:**` and `**Explanation:**` lines, `## Constraints` as a list, and an optional `## Follow-up`. The room renders examples as boxed blocks with the three labels aligned and inputs in mono, and constraints as a list, the way NeetCode and LeetCode lay them out. The 29 existing questions are rewritten to it.
- **Answer shape.** `walkthrough.md` for `dsa-quick` follows `content/templates/dsa-walkthrough.md`, enforced by the validator (keyed by section id, which also closes the Later note about one-phase coding-round problems): `## The question` (restated in two sentences, with what makes it harder than it looks), `## How to think about it` (the rationale that leads from the naive idea to the structure), `## Brute force` (code, why it is correct, complexity), one or more `## Better: <name>` sections (each with code, complexity and when to prefer it), `## Optimal: <name>` (code, complexity), `## Complexity` (one table over every approach), `## Where it shows up` (the coding-round problems in `coveredBy[]` plus real-world uses), `## Follow-up answers`. One figure per question under `figures/`, showing the optimal structure with the question's own example. The 29 existing answers are rewritten to it.
- **Imports.** Every DSA starter imports `java.util.*` (the starter's one job is to let the candidate type `Map`); the coding-round starters already import what they use. The editor's completion, when it accepts a JDK type from `java.util` that the file does not import, inserts the import after the package line (packages/editor, with the Java editor binding supplying the type list). Full IntelliSense is Phase 1af.
- **Template and docs.** The two templates, `content/templates/README.md` extended with the DSA rules, the contract rows, the Later note closed.

## Out of scope

The language server (1af), new questions beyond rewriting the 29, the coding-round room, backend, accounts.

## Constraints

- `CLAUDE.md` boundaries: the DSA room changes stay in `sections/dsa-quick/ui`; the "no channels" behaviour is a section-contract rule the engine enforces generically, not a DSA special case in the engine.
- Statement and answer text are original; the shape is borrowed, the words are not.
- Verify all 29 on a quiet machine; the 5x headroom rule fails under load.

## Exit criteria

1. A DSA Interview session shows Hints and no Assistant; `POST /sessions/<id>/ask` on a DSA session returns 400 with a reason (engine test); the coding round still shows both.
2. Every DSA statement validates against the template (validator test with one missing `## Constraints`), and the room renders Example blocks and Constraints as described (component test; screenshots in both themes).
3. Every DSA walkthrough validates against the answer template, in order, with a figure; two-sum's answer shows the brute force, at least one better approach and the optimal one, each with code and complexity.
4. Typing `Map<Integer, Integer> m = new HashMap<>();` in a DSA starter compiles without the candidate adding an import; accepting `ArrayList` from the completion list inserts `import java.util.ArrayList;` (editor test).
5. All 29 questions verify three ways; every Phase 1ad gate is green.

## Gates

Rows marked Phase 1a in `docs/process/definition-of-done.md`, plus the content rows for every rewritten question.

## Deliverables

Code and tests; the two templates; `docs/contracts/problem-format.md` and `section-contract.md`; `CHANGELOG.md`; `docs/status.md`; `docs/runbooks/commands.md`.

## Report

What shipped, each exit criterion met or not with evidence, the screens the owner should look at (a DSA question in Interview and Practice, its Answer page), what moved to "Later".
