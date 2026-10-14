# 0006: AI use never blocks "solved"; the reviewer grades control of the AI

Date: 2026-10-14. Status: accepted.

## Context

PLAN.md 2.8 as first decided marked an Interview session "assisted" when AI edits were applied at a run or at finish, and only unassisted sessions counted as solved. The first hands-on run of Phase 1a showed this fights the product's own promise: the real round expects candidates to use the assistant, and interviewers grade how they drove it, not whether they touched it. Hello Interview's format says the same ("using it well is exactly what we're looking for"). The written review already scores "Control of the AI" from the edit events.

## Decision

Using the assistant never changes the score label, the solved count or the best score. A passing Interview session is solved. `assisted` stays derived and stored as before (any run or finish with `aiEditsActive > 0`), the result page shows the number of AI edits applied and undone, and the reviewer prompt receives every edit event and scores control of the AI. What keeps the rehearsal honest is the assistant's Interview-mode rules: it points at the place and explains the logic, and writes code only for a piece the candidate names after stating the approach (PLAN.md 2.8, "Point-and-explain rules").

## Consequences

- `docs/contracts/section-contract.md` and `edit-protocol.md`: the Assisted row becomes informational; no version bump, the field and its derivation are unchanged.
- Engine: `SessionRuns` stops gating `counts` on `assisted`; the score label is "Interview" or "Practice"; tests change accordingly.
- UI: the "Interview, assisted" label and the dashboard sentence about it go; an edit count takes their place.
- `prompts/interview-mode.md` and `write-code.md` carry the point-and-explain rules; each rule gets a recorded-transcript test.
- PLAN.md risk 19 is mitigated by the rules and the reviewer score instead of the solved gate.
