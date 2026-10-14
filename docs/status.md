# Status board

Updated by the `status-diagnoser` subagent at the start of every phase run and by the main agent at the end. One line per item; link PRs where they exist.

## Current phase

Phase 1a (base app) built on branch `phase-1a`: engine, Java package, coding-round section, ten verified problems, UI, editor and the Tauri shell. Automated gates green on Windows. Waiting on the human checks: macOS run and build, CI on a remote, clean-machine Doctor, and an outside tester (exit criteria 1 on macOS, 3 clean machine, 4 and 5 on CI, 7). Phase 0 validation continues alongside; checkpoint about 2026-11-19.

## Done

- 2026-10-14: prototype fixed (data files read-only, finish records first-time passes, naive solutions and `tools/Verify.java`, per-class child JVM test runner, heap cap, version fields). Three problems verified three ways.
- 2026-10-14: plan written, `docs/plan/PLAN.md`; decisions in section 14 settled.
- 2026-10-14: process layer written: `CLAUDE.md`, `docs/process/`, `docs/prompts/`, `.claude/agents/`, `.claude/skills/phase/`.
- 2026-10-14: desktop shell decided as Tauri 2 in Rust (UI stays React and TypeScript); plan, prompts and process files converted; `docs/learning/` added with a tutor prompt per phase.
- 2026-10-14: PROMPTS.md added at the root with the phase-by-phase prompt sequence.
- 2026-10-14: `.gitignore` and `CONTRIBUTING.md` added with the commit policy: working documents, raw research, interview notes, build output, user data and secrets stay local; dry run verified.
- 2026-10-14: developer-mode routing between the Claude CLI and the backend added (PLAN.md 2.5, decision 8); two CLI facts left for Phase 1b research.
- 2026-10-14: assistant redesigned (PLAN.md 2.8): Interview and Practice session types, Ask and Write code channels, assisted label, three-level authored hints, AI edit applier with undo and reset, editor autocomplete with language-server plugins; gates, prompts and learning files updated.
- 2026-10-14: Phase 0 landing page built in `web/` (days 3 to 5 of PLAN.md section 13): page, waitlist form to KV through a Pages function, PostHog events, Pages config. Live at https://intervue-web.pages.dev with screenshots, KV and PostHog.
- 2026-10-14: prototype verified (`java tools/Verify.java`: starter fails, reference passes, naive times out in phase 3, for all three problems) and published as `web/public/downloads/intervue-prototype.zip` with `RUN.md`, linked as "early prototype" on the landing page.
- 2026-10-14: Phase 0 review written; ADR 0001 (mock and manual UPI first, gateway later; ₹1,500 monthly and ₹3,500 quarterly; two free problems at launch: Hold Exposure and Corridor Router, confirmed) and ADR 0002 (individual, no GST registration until a CA review before the first real payment) accepted; PLAN.md updated for two free problems (launch pack of 8) and the new "would pay" metric.

- 2026-10-14: Phase 1a: monorepo build and gates; contracts and ADRs 0003 to 0005; run protocol and problem format libraries; Java package with test kit, runner, calibration, health checks and editor support; coding-round section with the prototype score; SQLite store; sessions with the pause rule, Practice hints and the AI edit applier; Doctor; local HTTP API with a per-launch token and engine.jar; typed client, CodeMirror editor, app UI and coding-round UI; Tauri 2 shell; three problems migrated and seven written, all ten verified three ways.

## In progress

- Phase 0 validation, alongside the Phase 1a learning sprint: post the pricing survey and recruiting message, 10 interviews, 3 prototype testers.

## Blocked

- None.

## Later (out of scope findings to revisit)

- CA review (ADR 0002): book a CA during Phase 1b; it blocks the first real payment in Phase 1c. Questions in `docs/discovery/ca-questions.md` (local).

- Waitlist endpoint has a honeypot and input caps but no rate limit; spam could exhaust the free KV write quota (failed writes return 503, nothing is silently lost). Add a Cloudflare rate-limiting rule on `/api/waitlist` before promoting the page widely.
- Landing bundle is about 765 kB (247 kB gzip), mostly MUI and posthog-js; trim if page speed matters for conversion.
- The gate `pnpm -r lint typecheck test` in `CLAUDE.md` and `definition-of-done.md` runs only `lint` (pnpm passes the other names as arguments); the working form is `pnpm -r lint && pnpm -r typecheck && pnpm -r test`.

- Prototype `Json` parser is replaced by Jackson in the engine; nothing to carry over.

- Phase 1a review follow-ups (not blocking): engine returns the changed line range of each applied AI edit, so the UI stops diffing files and per-edit Undo marks are exact; `FileEntry` says "editable" by the engine's real rule (kit and `data/` included) and the variant names the first file to open; one editor-tools endpoint with a single javac pass for symbols and diagnostics; dashboard progress computed by the engine; share one tree copy/delete helper between `engine`, `content/tools` and tests; pass the engine's `ProcessControl` to package health checks (SPI change, needs an ADR).
- The shell starts the engine inside Tauri `setup`, so the window appears only once the engine is ready (1 to 3 s); start it on a thread and let `engine_connection` wait.
- The engine's HTTP server runs on one thread over the single SQLite connection, so a long run or the Doctor holds other calls; move to per-session locks if the UI needs to stay live during runs.
- `java.runtime` health check compares versions only; add the `lib/modules` hash once the jlink step records a reference hash.
- The installer bundles every problem folder including `reference/` and `naive/`; Phase 1b packs and encryption replace this.
- The app icon is a placeholder square from `desktop/resources/icon.png`.
- `CLAUDE.md` and `definition-of-done.md` name the verify gate as `java content/tools/Verify.java`; the working command is `./gradlew :content:tools:verify`.
- `definition-of-done.md` names coverage packages `*.domain` and `*.application`, which the engine does not have; the enforced rule (engine/build.gradle.kts) covers the use-case packages `session`, `session.edits`, `health`, `content` and `store` at 80% lines. Update the DoD wording when the process docs are next touched.
- The section UI is wired directly (`App` takes one `SectionUi`); add a registry by section id when the second section lands.
