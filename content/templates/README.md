# Templates for new problems

`walkthrough.md` is the shape every `content/problems/<id>/walkthrough.md` has. The verify tool refuses a problem whose walkthrough lacks the DSA reference and closing headings in that order, or shows a figure that does not exist (`docs/contracts/problem-format.md`, "walkthrough.md").

## Rules that are not in the template text

- **Prompt voice.** Every ```text fence after "Decide the approach yourself" is what the candidate types to the assistant in interview mode. It reads as an experienced developer who has decided: the approach, the one or two decisions a wrong choice would fail a test on, the method and the scope. It never dictates the code line by line (no "set quote = -1; on a backslash i++"). The read-only "understand the codebase" prompt asks for checkable facts and may stay as a list.
- **Figures.** One SVG per problem under `figures/`, drawn by hand, showing the data structure or the approach with the concrete values of the problem's own example. Nothing decorative, no stock diagrams, no screenshots. Style: `viewBox` about 640 by 220, text and strokes in `#83748f` (readable on both themes), the one highlighted element in `#7c3aed` with white text, mono font for values. The caption in the Markdown says what the figure shows in one sentence. The engine inlines figures into the Answer page; the result page's walkthrough tab shows them too.
- **Code block.** `### Code` is the general routine in plain Java, outside this codebase, 10 to 20 lines, one comment per decision. The problem's reference solution is that shape with the problem's names.
- **Practise links.** Only `dsa://<question-id>` links, ids as in `content/dsa/INDEX.md` (LeetCode slugs). No links to other sites anywhere in a walkthrough.
- **Versions.** Any change to a problem folder bumps `version` in `problem.json`.

## DSA questions

`dsa-statement.md` and `dsa-walkthrough.md` are the shapes for `content/dsa/<question-id>/statement.md` and `walkthrough.md`; the validator enforces them for section `dsa-quick` (ADR 0012, `docs/contracts/problem-format.md`). The coding-round `walkthrough.md` above does not apply to DSA, and DSA has no prompts because it has no assistant.

- **Statement.** Only the layout is borrowed from NeetCode and LeetCode. Wording and examples are yours: no copied text, no stock examples, no links.
- **Answer.** Teach from the brute force up. State why each approach is correct, give its cost and say when to prefer it. Every approach has a ```java block and a complexity line. Same voice as above: an experienced developer, plain and short.
- **Figure.** At least one, under `figures/`, showing the optimal structure on the question's own example, in the style above.
- **Starter.** Editable files import `java.util.*`, so `Map` and `List` compile when typed.
