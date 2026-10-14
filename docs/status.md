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

## In progress

- Phase 0 tasks from PLAN.md section 13.

## Blocked

- None.

## Later (out of scope findings to revisit)

- Prototype `Json` parser is replaced by Jackson in the engine; nothing to carry over.
- Clock pause rule (PLAN.md 2.2) to be implemented in Phase 1a with the session module.
