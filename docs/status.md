# Status board

Updated by the `status-diagnoser` subagent at the start of every phase run and by the main agent at the end. One line per item; link PRs where they exist.

## Current phase

Phase 0, validation (PLAN.md section 10 and section 13). Engineering starts with `docs/prompts/phase-1a-base-app.md`.

## Done

- 2026-10-14: prototype fixed (data files read-only, finish records first-time passes, naive solutions and `tools/Verify.java`, per-class child JVM test runner, heap cap, version fields). Three problems verified three ways.
- 2026-10-14: plan written, `docs/plan/PLAN.md`; decisions in section 14 settled.
- 2026-10-14: process layer written: `CLAUDE.md`, `docs/process/`, `docs/prompts/`, `.claude/agents/`, `.claude/skills/phase/`.
- 2026-10-14: desktop shell decided as Tauri 2 in Rust (UI stays React and TypeScript); plan, prompts and process files converted; `docs/learning/` added with a tutor prompt per phase.
- 2026-10-14: PROMPTS.md added at the root with the phase-by-phase prompt sequence.
- 2026-10-14: `.gitignore` and `CONTRIBUTING.md` added with the commit policy: working documents, raw research, interview notes, build output, user data and secrets stay local; dry run verified.
- 2026-10-14: developer-mode routing between the Claude CLI and the backend added (PLAN.md 2.5, decision 8); two CLI facts left for Phase 1b research.
- 2026-10-14: assistant redesigned (PLAN.md 2.8): Interview and Practice session types, Ask and Write code channels, assisted label, three-level authored hints, AI edit applier with undo and reset, editor autocomplete with language-server plugins; gates, prompts and learning files updated.
- 2026-10-14: Phase 0 landing page built in `web/` (days 3 to 5 of PLAN.md section 13): page, waitlist form to KV through a Pages function, PostHog events, Pages config. Not yet deployed; screenshots, KV namespace id and PostHog key still to add.

## In progress

- Phase 0 tasks from PLAN.md section 13.

## Blocked

- None.

## Later (out of scope findings to revisit)

- Waitlist endpoint has a honeypot and input caps but no rate limit; spam could exhaust the free KV write quota (failed writes return 503, nothing is silently lost). Add a Cloudflare rate-limiting rule on `/api/waitlist` before promoting the page widely.
- Landing bundle is about 765 kB (247 kB gzip), mostly MUI and posthog-js; trim if page speed matters for conversion.
- The gate `pnpm -r lint typecheck test` in `CLAUDE.md` and `definition-of-done.md` runs only `lint` (pnpm passes the other names as arguments); the working form is `pnpm -r lint && pnpm -r typecheck && pnpm -r test`.
- Day 5 prototype zip and its R2 upload are not done; the page shows the download link once `VITE_PROTOTYPE_URL` is set.

- Prototype `Json` parser is replaced by Jackson in the engine; nothing to carry over.
- Clock pause rule (PLAN.md 2.2) to be implemented in Phase 1a with the session module.
