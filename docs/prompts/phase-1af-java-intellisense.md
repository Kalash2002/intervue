# Phase 1af: Java IntelliSense through the Eclipse JDT language server

## Context to read first

`CLAUDE.md`; `docs/plan/PLAN.md` sections 2.8 (editor assistance), 4.5 (editor support), 6.3 (bundled, downloaded or user-installed toolchains), 6.4 (health checks); `docs/adr/0009-java-system-before-accounts.md`; `docs/contracts/language-contract.md`; `packages/editor/` (the CodeMirror setup, completion, diagnostics, the `lsp/` folder) and `languages/java/editor/`; the engine's `ProcessControl` and the Java package's health checks; Context7 for `@codemirror/lsp-client` and the JDT language server's launch options (the `researcher` subagent records them in `docs/research/phase-1af.md`).

## Goal

In a Java session the editor completes members and types with their signatures, adds imports, shows hover documentation and live diagnostics, and jumps to a definition inside the project, the way an IDE does, on Windows first.

## In scope

- `languages/java/editor/lsp.json` declaring the Eclipse JDT Language Server as a downloadable plugin (version, download URL and hash, Java 21 minimum, runs on the bundled runtime), fetched into the app data folder on first use with a progress line in the Doctor; a Doctor check "Java language server" at warning level (missing means the built-in completion stays).
- The engine starts one server per Java session through `ProcessControl`, points it at the session's work folder, and stops it with the session; the local API exposes the server's stdio as a WebSocket at `/sessions/<id>/lsp` behind the per-launch token.
- `packages/editor` bridges it with `@codemirror/lsp-client`: completion with signatures and auto-import, hover, diagnostics replacing the javac pass while the server is up, go-to-definition within the project; the built-in completion and compiler diagnostics stay as the fallback and are what a session gets when the server is absent or slow to start (first response within 3 s or fall back for that session).
- Locked ranges and AI-edit highlights keep working; the server never writes files (edits still go through the engine's edit applier and the editor's own save path).
- A flag in the engine settings turns the server off; on by default on Windows once the Doctor check passes.

## Out of scope

Other languages' servers; macOS hand testing (CI build only, ADR 0007); formatting on save; refactorings.

## Constraints

- The language contract gains `lsp.json` as an optional, additive field of a language package; SPI version unchanged.
- No new engine dependency for the WebSocket if the HTTP server already supports upgrades; otherwise research first and record the choice.
- Download sizes and first-start time are measured and written into the Doctor's message and the report.

## Exit criteria

1. In a DSA and a coding-round session, typing `new HashMap<>()` without an import compiles after accepting the completion, and hovering `HashMap` shows its documentation (hand check, screenshots).
2. A reference to a project class in another file completes with its methods and jumps to its definition.
3. With the server missing or the flag off, every Phase 1a editor test and the room flow are unchanged.
4. The Doctor reports the server's presence, version and start time; a session falls back to the built-in completion when the server does not answer in 3 s (engine and editor tests with a fake server).
5. Every Phase 1ae gate is green.

## Gates

Rows marked Phase 1a in `docs/process/definition-of-done.md`.

## Deliverables

Code and tests; `docs/contracts/language-contract.md`; `docs/research/phase-1af.md`; `CHANGELOG.md`; `docs/status.md`; `docs/runbooks/commands.md`.

## Report

What shipped, measured download size and start time, each exit criterion met or not, what the owner should try by hand, what moved to "Later".
