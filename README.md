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

## Dev setup (Phase 1a)

Needs a JDK 21, Node 24 with pnpm 8 or later, and Rust stable with the Tauri 2 prerequisites (on Windows: the MSVC build tools and WebView2, both standard on Windows 11; on macOS: the Xcode command line tools).

```sh
pnpm install                       # every TypeScript part
./gradlew check                    # Java: compile, Spotless, tests, ArchUnit, coverage
pnpm -C desktop dev                # build engine.jar, start the Tauri shell; the shell starts the engine
```

The first launch runs the Doctor (about 20 seconds: sample compile, encoding, calibration), then shows the dashboard with the problems in `content/problems/`. Sessions and the database live in the app data folder (`%APPDATA%/si.intervue.app` on Windows, `~/Library/Application Support/si.intervue.app` on macOS).

Without the shell, run the engine and the UI separately: `pnpm -C desktop engine:dev` in one terminal and `pnpm -C desktop dev:browser` in another, then open http://localhost:1420. Every command is in `docs/runbooks/commands.md`.

| Part | What it is |
| --- | --- |
| `engine/` | The local engine: HTTP API on 127.0.0.1 with a per-launch token, SQLite store, sessions, edit applier, Doctor |
| `shared-java/` | Run protocol and problem format libraries |
| `languages/java/` | The Java package: runner, test kit, calibration, editor support and its CodeMirror binding |
| `sections/coding-round/` | The coding-round section: grader, score, prompts, and its UI (room and result parts) |
| `content/` | Problems, the free list, and the verify tool |
| `packages/` | Shared TypeScript: app UI, editor, engine client |
| `desktop/` | Tauri 2 shell in Rust; starts the engine, hosts the UI |
| `web/` | The website |
