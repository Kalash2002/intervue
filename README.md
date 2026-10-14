# Intervue

A desktop practice room for AI-assisted coding interviews. Start with `CLAUDE.md`, then `docs/plan/PLAN.md`.

- `docs/plan/` the product and engineering plan, research notes and the original planning prompt
- `docs/process/` pipeline, definition of done, code standards, review plan
- `PROMPTS.md` the exact sequence of prompts to give Claude Code, phase by phase, with the human steps between them
- `docs/prompts/` one prompt per roadmap phase; run with `/phase docs/prompts/<file>.md`
- `docs/learning/` the minimal learning per phase, each with a tutor prompt to paste into an AI assistant; do it before running the phase
- `docs/status.md` the status board
- `prototype/` the original single-file prototype, kept as a read-only reference; run it with `prototype/run.bat` or `prototype/run.sh`, verify its problems with `java tools/Verify.java` from inside `prototype/`
- `.claude/` agents and the phase skill for Claude Code

The monorepo skeleton (`backend/`, `engine/`, `desktop/`, `web/`, `packages/`, `shared-java/`, `languages/`, `sections/`, `content/`) is created by Phase 1a.
