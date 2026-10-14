# Contributing to Dryrun

These rules let a second developer or an AI coding agent work in this repository without breaking its boundaries. They are the rules of PLAN.md section 5.12 plus the commit policy.

## What is committed and what is not

Commit only what is common to every machine, crucial for the product, or needed by the AI agents to work. Everything a phase produces and then discards stays local and is listed in `.gitignore`.

| Committed | Not committed (local only) |
|---|---|
| Code, tests, build configuration, CI workflows | Build output, `node_modules`, Rust `target`, the jlink runtime, built packs (`.drp`) |
| `content/problems/`, `content/packs/`, `content/free/` | Verification scratch folders, user sessions and databases |
| `docs/plan/PLAN.md`, the single source of truth | `docs/plan/research/` raw research notes and `docs/plan/PLANNING-PROMPT.md`; the plan carries the citations |
| `docs/process/`, `docs/prompts/`, `docs/learning/`, `PROMPTS.md`, `CLAUDE.md`, this file | `docs/plans/` step plans and `docs/research/` notes the pipeline writes per phase; the PR carries the outcome |
| `docs/contracts/`, `docs/adr/`, `docs/runbooks/`, `docs/setup-guides/`, `docs/retro/`, `docs/status.md`, `CHANGELOG.md` | `docs/discovery/` interview notes, which hold personal data; only `phase-0-review.md` is committed |
| `.claude/agents/`, `.claude/skills/`, `.github/` | `.claude/settings.local.json` and any `*.local.*` |
| `.env.example` with fake values | Every real `.env`, certificate, key or token, without exception |

If a working document turns out to matter after the phase, move its substance into the plan, an ADR, a contract or a retro, and leave the draft untracked.

## Rules

1. One part per pull request: backend, engine, desktop, web, one language, one section, or content. Mixed PRs are rejected by CI labels.
2. A change inside a part never edits another part. If you need to, you need a contract change: write the ADR first, make the change additive, then upgrade consumers.
3. Content PRs touch only `content/problems/<id>/` and `pack.json`; CI enforces it. Bump the problem version on every edit. Every phase of a problem has a `hints/phase<N>.md` with three levels.
4. New language or section: copy `scripts/new-language.sh` or `scripts/new-section.sh` output, fill the five folders, never edit `engine/`, `desktop/` or `web/`.
5. Secrets never enter the repository. `.env.example` files list every variable with a fake value. The pre-commit hook runs `gitleaks`.
6. Every runner, grader, scoring rule and the edit applier has tests against real inputs in CI; a test kit change keeps the run protocol byte-compatible or bumps the protocol version with an ADR.
7. Prompts are data in `sections/*/prompts/`; code never embeds prompt text. The Claude CLI is reachable only in developer mode.
8. Database changes are additive Flyway migrations; never edit a shipped migration; the engine's SQLite migrations follow the same rule.
9. Conventional Commits with the area as scope; squash merge; tags per deployable (`app-v1.2.0`, `backend-v1.2.0`, `pack-<id>-v3`, `lang-<id>-v1.0.0`, `section-<id>-v1.0.0`).
10. Before releasing the app: health check green on a clean Windows machine and a clean macOS machine, update from the previous version tested, free problem session completed end to end in both session types.
11. For AI coding agents: read `CLAUDE.md`, then `docs/contracts/` for the part you touch, run that part's tests, never rename contract fields, never change `formatVersion`, protocol or SPI versions without an ADR, and never commit a file the table above lists as local.
