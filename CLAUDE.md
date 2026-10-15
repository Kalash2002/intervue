# Intervue: instructions for AI coding agents and humans

Read this first, then `docs/plan/PLAN.md` for the part you touch, then `docs/process/`.

## What this repository is

Intervue is a desktop practice room for AI-assisted coding interviews. The plan in `docs/plan/PLAN.md` is the source of truth for scope, architecture and phases. Work happens one phase at a time from a prompt file in `docs/prompts/`; the `/phase` skill runs that prompt through the pipeline in `docs/process/pipeline.md`.

## Layout and boundaries (summary of PLAN.md section 5)

- `backend/` Spring Boot, hexagonal. `engine/` Java local core. `desktop/` Tauri 2 shell in Rust (`src-tauri/`) with the React UI in `src/`, no business logic. `web/` React website. `packages/` shared TypeScript. `shared-java/` shared Java libraries. `languages/<lang>/` one folder per language package. `sections/<id>/` one folder per section package. `content/problems/<id>/` one folder per problem. `prototype/` the original single-file prototype, read-only reference.
- A change inside one part never edits another part. Cross-part needs go through a contract in `docs/contracts/` and an ADR in `docs/adr/`.
- Content PRs touch only `content/problems/<id>/`, `content/dsa/<id>/` and `pack.json`. New section packages need no `engine/` or `desktop/` edit since ADR 0008, only their folder plus a line in `packages/sections` (the dsa-quick phase made the one-time exception); new language packages never touch `engine/`, `desktop/` or `web/`.
- Prompts for the AI assistant are data in `sections/*/prompts/`, never strings in code. The assistant has two channels, Ask and Write code; Write code returns edits in the protocol of PLAN.md section 2.8 and the engine's `EditApplier` is the only code that writes them, under the rules there (editable files only, base hash, exact match, snapshot, undo). The problem source is never modified; sessions work on a copy.
- The Claude CLI is a development route only (PLAN.md 2.5): `CliModelClient` is reachable solely when `developer.mode` is on, chosen by the remaining-allowance rule; end-user builds call the backend.
- Sessions are Interview or Practice (PLAN.md 2.8). Hints exist only in Practice and only as pack content in `hints/phase<N>.md`. A session with AI edits applied at a run or at finish is `assisted`, which is recorded and shown but never changes the score label, solved or the best score (ADR 0006); in Interview sessions the assistant follows the point-and-explain rules of 2.8.

## Coding standards (details in `docs/process/code-standards.md`)

- Small and plain over clever. Prefer the standard library, then an existing dependency, then new code. No abstraction with one implementation unless a contract requires it.
- Java: google-java-format via Spotless; `-Xlint:all`; records and sealed types where they fit; no checked-exception wrapping without a reason. TypeScript: strict mode, Prettier, ESLint with the repo config; no `any` without a comment saying why. Rust (shell only): `rustfmt`, `clippy` with warnings denied, `Result` everywhere except `expect` at startup with a message, `unsafe` only inside the `proctor` module behind safe wrappers.
- Comments explain why, not what. A short comment is required where logic is non-obvious: a tricky ordering, an invariant, a workaround with its link. No comments that narrate the next line. Javadoc or TSDoc only on ports, contracts and public package APIs.
- Methods under about 40 lines, files under about 400 lines; split when you cross them unless splitting hurts readability.
- Every non-trivial branch, loop, parser, scoring rule, money or security path has a test. Runners and graders are tested against the real toolchain.
- Names say what things are for. No `Util`, `Helper`, `Manager` classes without a narrower name.
- Delete dead code and unused flags in the same PR that makes them dead.

## Gates before any merge (details in `docs/process/definition-of-done.md`)

`./gradlew check` (compile, Spotless, tests, ArchUnit boundary tests), `pnpm -r lint typecheck test`, `cargo fmt --check && cargo clippy -- -D warnings && cargo test` in `desktop/src-tauri`, `java content/tools/Verify.java` for touched problems, no new TODO without an issue link, `CHANGELOG.md` entry, `docs/status.md` updated, `/code-review` run with findings addressed, `/simplify` run on the diff.

## Working rules for agents

- Start every phase by running `/phase docs/prompts/<file>.md`; do not improvise a phase from memory.
- Diagnose before designing; design-review before coding; test before implementing; simplify after implementing; review before merging.
- Say "cannot verify" rather than guess. Look up library APIs with the Context7 tools before writing code against them.
- Never change `formatVersion`, protocol versions or SPI versions without an ADR. Never commit secrets; `.env.example` lists variables with fake values.
- Keep diffs focused on the phase. Note out-of-scope findings in `docs/status.md` under "Later" instead of fixing them.
- Commit only what `CONTRIBUTING.md` lists as committed. Working documents the pipeline writes (`docs/plans/`, `docs/research/`), raw research, interview notes, build output and secrets stay local; `.gitignore` enforces it. If a working note matters later, move its substance into the plan, an ADR, a contract or a retro.
