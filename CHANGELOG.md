# Changelog

All notable changes, grouped by deployable. Unreleased entries move under a version heading at release.

## Unreleased

- packages/ui, packages/editor, sections/coding-round/ui: fixes from the Phase 1a hand test: the room's splitters drag again (file tree, side panel, output pane, as in the prototype); the editor scrolls with the mouse wheel; pastel syntax colours from theme tokens and a softer dark palette; a "← Dashboard" button on the room, result and answer pages; an Answer page with the problem's walkthrough, on every dashboard card and on the result page (2026-10-15)
- engine, packages/local-engine-client: `GET /problems/{id}/walkthrough` serves a problem's walkthrough without a session, for the Answer page (2026-10-15)
- content: all ten walkthroughs follow one structure: read before you run, a codebase prompt, each phase with reproduce, cause, fix, prompts and what to check in the reply, the technique with its complexity table and practice links, a time budget, and answers to every follow-up; problem versions bumped (2026-10-15)
- ci: installers build from `app-v*` tags, matching the per-deployable tag names (2026-10-15)
- engine: developer-mode assistant on the Claude CLI (`--developer-mode`): Ask, Write code through the edit applier, Explain this hint and a written review at finish, streamed to the UI; prompts built from the section's prompt files; every call logs an `aiCall` event with route, model, tokens and latency, never text; Doctor check "Claude CLI found and signed in" in developer mode; with developer mode off no `claude` process is started (2026-10-15)
- engine: ADR 0006 scoring: every passing Interview session counts as solved, labels are Interview or Practice, the view carries AI edits applied and undone; timed Practice and `endedEarly` (V2 migration) (2026-10-15)
- sections/coding-round: point-and-explain rules for Interview mode and the Write code scope, with recorded-transcript tests per rule (2026-10-15)
- packages/ui, sections/coding-round/ui: live assistant panel (rules switch in Practice, outcomes with Undo, Write code counter, demo behind developer mode), Explain this hint, written review tab, set-up screen, session overview, phase intro card, Guide tab with passing pill, progress dots, end-session dialog (2026-10-15)
- packages/ui: visual refresh: stylesheet split into tokens, base, room, timeline, panel, pages and result; one calm accent, dark and light themes, problem cards and progress tiles; the saved theme applies at start (2026-10-15)
- packages/ui: the room's center column no longer reserves a row for a tabs bar that is not rendered; the editor fills its column instead of being squeezed to two lines above a blank splitter (2026-10-14)
- docs: ADR 0006 (AI use never blocks solved; point-and-explain rules in Interview mode), PLAN.md 2.8 and the section and edit-protocol contracts updated, Phase 1ab prompt added (2026-10-14)
- desktop: Tauri 2 shell that starts the engine from Rust with a per-launch token, hands port and token to the UI, stops it on exit; jlink runtime script; NSIS and DMG bundle config (2026-10-14)
- packages/ui, sections/coding-round/ui: start screen (Interview or Practice), dashboard counting only unassisted interviews as solved, room (editor, output, brief, Practice hints, timeline strip, phase chips, resets, assistant panel disabled until sign-in with a recorded-reply demo), result page with label, pauses, hints and AI edits, Doctor screen with copy report (2026-10-14)
- packages/editor, languages/java/editor: CodeMirror 6 with symbol, keyword and snippet completion, inline compiler diagnostics, locked ranges, AI-edit highlights with Undo; Java binding (2026-10-14)
- packages/local-engine-client: typed client for the engine's local API (2026-10-14)
- engine: local HTTP API on 127.0.0.1 with a bearer token, engine.jar distribution, Doctor with the Phase 1a health checks and stored calibration (2026-10-14)
- engine: SQLite store, content store, sessions with the pause rule, Practice hints, AI edit applier with undo and reset (2026-10-14)
- languages/java, sections/coding-round, shared-java: Java package and test kit on run protocol v1, coding-round section with the prototype score, problem format v1 (2026-10-14)
- content: seven new problems (Duplicate Order Detector, Rate Limiter Audit, Playlist Dedupe, Courier Load Balancer, Friend Suggestion, Build-Log Bracket Checker, Shift Roster Lookup); the three prototype problems migrated; verify tool on the language SPI (2026-10-14)
- docs: contracts for run protocol, problem format, language and section SPIs and the edit protocol; ADRs 0003 to 0005 (2026-10-14)

- web: early prototype download, `/downloads/intervue-prototype.zip` with a one-page RUN.md, linked from the landing page; built from the committed prototype after all three problems verified (2026-10-14)
- docs: `docs/runbooks/commands.md`, the per-phase command reference, updated at step 10 of every `/phase` run (2026-10-14)
- web: Phase 0 landing page (React, Vite, MUI) with three prototype screenshots, waitlist form posting to a Pages function that stores signups in KV, PostHog page and signup events, Cloudflare Pages config (2026-10-14)
- docs: product and engineering plan, process layer, phase prompts, agents and the phase skill (2026-10-14)
- prototype: data files read-only, finish records first-time passes, naive solutions and Verify tool, per-class child JVM test runner, heap cap, version fields (2026-10-14)
