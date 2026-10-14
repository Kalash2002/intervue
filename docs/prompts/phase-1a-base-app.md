# Phase 1a: base working app with 10 problems

## Context to read first

`CLAUDE.md`; `docs/plan/PLAN.md` sections 2.2 (local store, clock rule), 2.6 (entities, interfaces), 3.1, 3.2 and 3.3 (stack, Tauri packaging, the Tauri decision), 4 (runtime, run protocol, calibration), 5.2 and 5.3 (layout, layers), 6.4 (health checks), 7.1 (problem format); `docs/process/*`; `docs/learning/phase-1a.md` (the human does this sprint before the run); `docs/adr/0001-payments-mock-then-gateway.md` (two free problems) and `docs/discovery/phase-0-review.md` (checkpoint before the engine rewrite); `prototype/` for the behaviour to preserve (`server/Server.java`, `web/app.js`, `kit/`).

## Goal

A desktop app a tester can install from a zip and use offline: dashboard, session room, result page, exactly as the prototype behaves, for 10 Java problems read from a local plain content folder, with no account, no backend and no payments. New since the prototype: Interview and Practice session types, three-level authored hints in Practice, the AI edit applier with undo and reset (driven by a fake edit source until Phase 1b connects the backend), and editor autocomplete with inline compiler diagnostics (PLAN.md 2.8).

## In scope

- Monorepo skeleton from PLAN.md 5.2: Gradle multi-project (`engine`, `shared-java/*`, `languages/java/runner`, `sections/coding-round/*`, `content/tools`), pnpm workspace (`desktop`, `packages/ui`, `packages/editor`, `packages/local-engine-client`, `sections/coding-round/ui`, `languages/java/editor`), CI workflows with the gates in `docs/process/definition-of-done.md` marked "Phase 1a", `.editorconfig`, Spotless, ESLint, Prettier, ArchUnit tests.
- Engine: local HTTP API with a per-launch token; SQLite store with the tables in PLAN.md 2.2 including `edits` and forward-only migrations; `ContentStore` with `PlainFolderSource` (dev) and `FreeProblemSource`; sessions with the clock pause rule, the `session_type` (Interview with the limit, Practice with a stopwatch), hint reveals with `hint` events (Practice only), `EditApplier` implementing every rule of PLAN.md 2.8 with snapshots, undo, "reset file" and "reset problem" from the source, `aiEditsActive` on run events and the `assisted` flag, exercised by a `FakeEditSource` that replays recorded edit streams; a symbol extractor using the JDK `com.sun.source` tree API and a compiler-diagnostics mapper for the editor; runs through the `LanguagePackage` SPI; the Java package with the test kit emitting the run protocol to `results.jsonl`; calibration; the health check framework with the Phase 1a checks (OS, bundled runtime intact, compile-and-run sample, calibration, sessions folder writable, port free, encoding).
- Coding-round section package: schema including the hints requirement, tests grader, scoring rule equal to the prototype's formula with weights read from `problem.json` plus the Practice variant (correctness and phase results only, labelled Practice) and the assisted label, result parts, prompts folder (`system.md`, `interview-mode.md`, `helpful-mode.md`, `write-code.md` with the edit protocol, `hint-explain.md`, `feedback.md`; text moved from the prototype where it exists, used later by the backend).
- React UI in `packages/ui`: start screen choosing Interview or Practice; dashboard with "Interview", "Interview, assisted" and "Practice" labels and solved counting only unassisted interviews; room (file tree, CodeMirror 6 editor with autocomplete, inline diagnostics and AI-edit decorations with Undo, output, brief, Hint control in Practice, timeline strip showing edit and hint events, phase chips, run target picker, reset file and reset problem); result page (score with its label, per-phase results, hints used, edits applied and undone, walkthrough, follow-ups, notes). Assistant panel shows the Ask and Write code controls disabled with "Available after sign-in in a later phase".
- `packages/editor`: CodeMirror 6 with `@codemirror/autocomplete` (keywords, project symbols from the engine, snippets), `@codemirror/lint` fed by compiler diagnostics, read-only and AI-edit decorations; an `lsp/` folder with the `@codemirror/lsp-client` bridge stubbed for Phase 2.
- Tauri 2 shell in Rust: window, engine launcher that starts `runtime/bin/java -jar engine.jar` from the bundled jlink runtime in `bundle.resources` through the shell plugin and passes the port and per-launch token to the UI through a command, capabilities limited to what the shell needs, unsigned dev bundles (`cargo tauri build`) for Windows and macOS. No updater yet. The React UI runs in the system webview unchanged from `packages/ui`.
- Content: migrate the three prototype problems to the new format under `content/problems/` and write their `hints/phase<N>.md` with three levels each; author seven more from PLAN.md 7.3 pack A (Duplicate order detector, Rate limiter audit, Courier load balancer, Friend suggestion, Playlist dedupe, Build-log bracket checker, Shift roster lookup) with hints; every problem verified three ways with `content/tools verify`, which also checks the hints; `content/free/free.json` lists the two free problems, Hold Exposure and Corridor Router (ADR 0001).

## Out of scope

Backend, accounts, entitlements, packs, encryption, payments, live AI calls (Ask, Write code, hint explanation, feedback), language servers, sync, website, signing, notarization, updater, Python or any other language, any other section.

## Constraints

- Boundaries and standards in `CLAUDE.md`. Engine logic stays in Java; the shell holds none.
- The run protocol in PLAN.md 4.3 is the only channel for test results; user output streams separately.
- Preserve the prototype's behaviour exactly where the plan does not change it; where it does (clock pause, run protocol, data files read-only, finish recording first-time passes) follow the plan.
- Use Context7 through the `researcher` subagent for Tauri 2 (core, shell plugin, bundler resources, capabilities), the keychain crate, CodeMirror 6, sqlite-jdbc and Jackson before writing code against them.
- Rust standards from `docs/process/code-standards.md`: the shell holds no domain logic; `clippy -D warnings`; no `unwrap` in command handlers.

## Exit criteria

1. `pnpm dev` on Windows and macOS launches the shell, the engine answers `GET /local/ping`, and the dashboard lists 10 problems.
2. A full Interview session on a free problem: start, edit, run phase tests, unlock by passing, skip, finish, result page with score equal to the prototype's for the same inputs, walkthrough shown. A full Practice session: stopwatch, three hint levels revealed and recorded, Practice label, not counted as solved.
2b. The edit applier passes every rule test of PLAN.md 2.8; a replayed edit stream applies, shows decorations, undoes, and marks the session assisted when a run follows; "reset problem" restores the source copy.
2c. Autocomplete offers project symbols and the editor shows a compile error inline after a save.
3. The Doctor screen shows every Phase 1a check green on a clean machine; calibration factor stored.
4. `java content/tools/Verify.java` is green for all 10 problems on CI.
5. Unsigned builds for Windows (`.exe`) and macOS (`.dmg`) are produced by CI from a tag and start on clean machines.
6. All Phase 1a automated gates green; ArchUnit proves the layer rules; coverage at or above 80% on engine `domain` and `application` packages.
7. A tester outside the project completes one session from the zip and reports no blocker.

## Gates

All rows marked Phase 1a in `docs/process/definition-of-done.md`.

## Deliverables

Code and tests; `docs/contracts/run-protocol.md`, `docs/contracts/problem-format.md`, `docs/contracts/language-contract.md`, `docs/contracts/section-contract.md`, `docs/contracts/edit-protocol.md` written from PLAN.md 2.6, 2.8, 4.1 and 4.3; `docs/adr/0003-tauri-shell.md`, `0004-sqlite-local-store.md`, `0005-run-protocol.md`; `CHANGELOG.md`; `docs/status.md`; `README.md` with the dev setup.

## Report

What shipped, exit criteria met or not with evidence, what the human must try by hand (both operating systems), what moved to "Later".
