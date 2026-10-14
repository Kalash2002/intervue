# Changelog

All notable changes, grouped by deployable. Unreleased entries move under a version heading at release.

## Unreleased

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
