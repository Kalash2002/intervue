# 0012: DSA without the assistant, hints in Interview, section-keyed validation

Date: 2026-10-16. Status: accepted.

## Context

The owner's hand test of the DSA section (2026-10-16, `docs/status.md`) found that DSA questions carry the assistant where only hints belong, that statements are not laid out the way candidates know from NeetCode and LeetCode, and that answers give the optimal approach straight away. ADR 0008 gave `dsa-quick` the channels ASK and WRITE_CODE, and PLAN.md 2.8 and `CLAUDE.md` say hints exist only in Practice. The validator keys the one-phase rules on phase count (ADR 0008), so a one-phase coding-round problem would be held to DSA rules; `docs/status.md` lists that under Later.

## Decision

- **Channels are enforced by the engine, generically.** A session whose section lacks `ASK` gets 400 on `ask` and `hints/explain`; one that lacks `WRITE_CODE` gets 400 on `write`. A section with no channels renders no assistant panel. `dsa-quick` has no channels. The coding round keeps ASK and WRITE_CODE. The engine has no DSA special case.
- **Hints in Interview are a section choice.** `SectionPackage` gains an optional method `hintsInInterview()`, default false; `dsa-quick` returns true. Hints are available when the session is Practice or the section allows hints in Interview. This supersedes "hints only in Practice" for sections that set the method. `SPI_VERSION` stays 1 because the method has a default. "Explain this hint" needs ASK, so DSA does not offer it.
- **SessionView** gains two additive fields: `channels` (a list of channel names) and `hintsAllowed` (boolean). Rooms render from these.
- **Validator keyed on section.** The DSA rules apply when the section id is `dsa-quick`, replacing the phase-count keying of ADR 0008, and close the Later note. For `dsa-quick`: one phase of weight 80, no `brief/intro.md`, the statement shape, the answer shape, at least one figure, and the starter's editable files import `java.util.*`. A one-phase problem of any other section is refused by that section's schema. No `formatVersion` bump: the change is additive.
- **Shapes.** `content/templates/dsa-statement.md` and `content/templates/dsa-walkthrough.md` define the statement and the answer. Only the shape is borrowed from NeetCode and LeetCode; the words, examples and figures are original and no link to either site appears.

## Consequences

- `docs/contracts/section-contract.md` and `problem-format.md`, `CLAUDE.md` and PLAN.md 2.8 carry the new rules.
- The DSA room changes stay in `sections/dsa-quick/ui`; the engine change is the generic channel and hint check.
- Existing DSA statements and walkthroughs are rewritten to the templates, each with a figure and a `version` bump.
- A future section with no channels, or with hints in Interview, needs no engine change.
