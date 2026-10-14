# Phase 1ab: live assistant on the developer route, scoring rule, room refresh

## Context to read first

`CLAUDE.md`; `docs/plan/PLAN.md` sections 2.5 (AI gateway, developer-mode routing), 2.8 (session types, point-and-explain rules, edit protocol, hints), 6.4 (health checks, the developer-only CLI check); `docs/adr/0006-ai-use-and-solved.md`; `docs/contracts/edit-protocol.md`, `section-contract.md`; `docs/process/*`; `sections/coding-round/prompts/*`; `prototype/server/Server.java` (`Claude.ask`, `Chat.chat`, `Chat.feedback`: the `claude -p` call to reuse); the Phase 1a code in `engine/` (`FakeEditSource`, `EditApplier`, `SessionRuns`), `packages/ui` and `sections/coding-round/ui`.

## Goal

On the developer's own machine the assistant answers, writes code, explains hints and reviews a finished session through the Claude CLI; a passing Interview session counts as solved whether or not the assistant edited code; and the room guides the candidate the way the real round does: set up the session, read the phase card, run the tests, finish with a review.

## In scope

- Engine, `AiClient` (PLAN.md 2.5): one interface with four calls, `chat`, `edit`, `hintExplain`, `feedback`, and in this phase one implementation, `CliModelClient`, reachable only when the engine setting `developer.mode` is on. It runs `claude -p` with the Sonnet 5.5 model id, streams the reply, and for `edit` feeds the edit protocol lines into the Phase 1a `EditApplier` in place of `FakeEditSource` (keep the fake for tests and the demo). `PromptBuilder` assembles prompts from the section's prompt files and the problem in the order of PLAN.md 2.5 (rules, brief, read-only files, editable files with hashes, history with past edits as one line each, message); prompts are data, never strings in code. Every call logs the route, tokens and latency the CLI reports. The Doctor gets the developer-only check "Claude CLI found and signed in" (warning level). With `developer.mode` off the assistant controls show "Assistant unavailable: sign-in arrives in Phase 1b"; with it on and the CLI missing, signed out or failing, they show the reason. The backend route, the remaining-allowance rule and the budget stay in Phase 1b.
- Engine, scoring (ADR 0006): `SessionRuns` counts every passing Interview session as solved; the score label is "Interview" or "Practice"; `assisted` is still derived and stored; the session view carries the number of AI edits applied and undone. Dashboard progress and best scores follow.
- Section prompts: rewrite `prompts/interview-mode.md` and the scope paragraph of `prompts/write-code.md` to the point-and-explain rules of PLAN.md 2.8: asked to fix, solve or make tests pass, the assistant names the file, method and line and describes the logic to change in words, with no solution code; asked to explain, it explains fully using the candidate's code; asked a specific question, it answers honestly; it never volunteers other phases, other bugs or optimisations; Write code edits only the piece the candidate named after stating the approach, and a bare "fix it" gets the point-and-explain reply and no edit. `helpful-mode.md` stays for Practice. One recorded-transcript test per rule in the section's Java tests: a fixed prompt plus a recorded model reply, asserting what the engine applies and shows (no live model calls in tests).
- Assistant panel: Ask and Write code enabled; the model's reply streams into the chat; Write code shows the note, each edit's outcome and an Undo per edit as in the demo; a rules switch in Practice (Helpful or Interview rules); "Explain this hint" in Practice on a revealed hint; a short counter of Write code calls this session. The recorded-reply demo moves behind `developer.mode`.
- Result page: the written review from `prompts/feedback.md`, requested at finish and shown when it lands, with the edit and hint events in its input; "Review unavailable" with the reason when the route is off.
- Room flow (hand-checked; follow the structure of the Hello Interview screens the owner supplied, not their look): a "Set up your session" start screen with the language, the two session-type cards and, for Practice, a timed-mode switch; a "N phases, M minutes" overview listing the phases from the brief, then one note on how to use the assistant; each phase opens with an intro card (phase name, one sentence, Start, Skip phase) on top of the brief; the Guide tab shows the current phase's text, which tests must pass, a "k of n passing" pill and a Run tests button whose output appears in the same tab as well as in the output pane; progress dots per phase in the header; Finish opens an end-session dialog with Cancel, End early and I'm done.
- Visual refresh of `packages/ui/src/styles/app.css` and the pages that use it: a new token set (one accent used for primary actions and progress, a calmer palette than the current amber, a readable sans for UI text with mono only in code and badges, cards with radius and a subtle border for choices and problems, pills for difficulty and status, generous spacing), dark and light themes both checked; the dashboard shows problems as cards with difficulty, minutes and status, and the progress row as tiles. No new UI library; keep the dependency list as it is.

## Out of scope

Backend, accounts, entitlements, caps and the usage log, the remaining-allowance rule, packs, payments, sync, language servers, per-phase clocks, a code-comprehension phase with spoken or typed answers, voice, signing, updater, Python, any other section.

## Constraints

- `CLAUDE.md` boundaries. The CLI route exists only behind `developer.mode`; an end-user build has no code path to it (ArchUnit or a settings test proves it). No API key exists in this phase.
- `EditApplier` stays the only writer of model edits; every Phase 1a edit-rule test still passes.
- Prompt files are data in `sections/coding-round/prompts/`; the engine reads them at call time.
- Use Context7 through the `researcher` subagent for the Claude Code CLI (`claude -p` flags, streamed JSON output, model selection, whether an effort setting exists non-interactively) and record the answers in `docs/research/phase-1ab.md`; the two open CLI questions of PLAN.md 2.5 stay with Phase 1b if the CLI docs do not settle them.
- No design library; CSS tokens only. Keep the editor, output pane and timeline strip working as in Phase 1a; the center column grid has exactly as many rows as children.

## Exit criteria

1. With `developer.mode` on and the CLI signed in: an Interview session on a free problem where the candidate asks "fix it" gets a reply naming the file, method and line and the logic in words, with no code; asks "explain `holdsFor`" and gets an explanation; states an approach, asks Write code for the named method, and the edit applies with highlights and Undo; a run follows and the session still counts as solved on the dashboard after finishing with all phases green. Evidence: the session's event log and a screenshot of the dashboard.
2. With `developer.mode` off, every assistant control shows the Phase 1b message and no process named `claude` is started (test with a fake PATH).
3. Practice: Helpful rules by default, Interview rules selectable; "Explain this hint" returns an explanation that goes no further than the revealed level (recorded-transcript test).
4. Finish produces a written review with the six sections of `feedback.md`; its input contains the edit and hint events.
5. One recorded-transcript test per point-and-explain rule is green; every Phase 1a engine, UI and content gate is green.
6. The room flow and the visual refresh are hand-checked on Windows in both themes: set-up screen, overview, phase intro card, Guide tab with the passing pill, end-session dialog, dashboard cards; the editor fills its column with no blank area.

## Gates

Rows marked Phase 1a in `docs/process/definition-of-done.md`, with the Session types row as updated for ADR 0006.

## Deliverables

Code and tests; `docs/contracts/section-contract.md` and `edit-protocol.md` already updated for ADR 0006; `docs/research/phase-1ab.md`; `CHANGELOG.md`; `docs/status.md`; `docs/runbooks/commands.md`.

## Report

What shipped, each exit criterion met or not with evidence, the measured CLI latency per call, what the human must try by hand (the flow in criterion 6), what moved to "Later".
