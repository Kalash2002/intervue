# Definition of done and gates

A phase, a feature or a fix is done when every row below is green. Gates are automated where a tool exists; the rest are checklist items on the pull request template.

## Automated gates (CI and local)

| Gate | Command | Rule | Added when |
|---|---|---|---|
| Java compiles with warnings as errors | `./gradlew compileJava` with `-Xlint:all -Werror` on `engine`, `backend`, `shared-java` | Zero warnings | Phase 1a |
| Java formatting | `./gradlew spotlessCheck` (google-java-format) | No diff | Phase 1a |
| Java tests | `./gradlew test` | All pass; JaCoCo line coverage at or above 80% on `*.domain` and `*.application` packages, no threshold elsewhere | Phase 1a |
| Architecture boundaries | ArchUnit tests in `engine/src/test/.../ArchitectureTest.java` and `backend/...` | `api` depends on `application` and `domain` only; `domain` depends on nothing in the repo; adapters are not imported by `domain`; `desktop/` and `web/` never import engine classes | Phase 1a |
| TypeScript | `pnpm -r typecheck` with `strict`, `noUnusedLocals`, `noImplicitReturns` | Zero errors | Phase 1a |
| Lint and format | `pnpm -r lint` (ESLint: typescript-eslint strict, `complexity: 10`, `max-lines: 400`, `max-lines-per-function: 60`, `import/no-restricted-paths` for layer rules) and `prettier --check` | Zero errors | Phase 1a |
| UI tests | `pnpm -r test` (Vitest on `packages/ui`, `packages/editor`, `desktop/src`) | All pass | Phase 1a |
| Rust shell | `cargo fmt --check`, `cargo clippy --all-targets -- -D warnings`, `cargo test` in `desktop/src-tauri`, on Windows and macOS runners | Zero warnings, all tests pass | Phase 1a |
| Content isolation | `.github/workflows/content.yml` isolation job (PLAN.md section 5.5) | A content PR touches only its problem folders and `pack.json` | Phase 1a |
| Content verification | `java content/tools/Verify.java <ids>` | Starter fails phase 1, reference passes all, naive passes all but the last phase and times out there, headroom rules hold, every phase has `hints/phase<N>.md` with three non-empty levels and no code fence | Phase 1a |
| Edit applier rules | Tests in `engine/session/edits/` | One test per rule in PLAN.md 2.8: read-only refusal, path escape refusal, base-hash mismatch, zero and multiple matches, size and encoding caps, snapshot and undo round trip, undo after manual change, reset from source, `aiEditsActive` on runs, `assisted` derivation | Phase 1a |
| Session types | Engine tests | Practice sessions never count as solved; Interview sessions with active edits are `assisted`; hints are refused in Interview sessions | Phase 1a |
| AI channel caps | Backend tests | Ask costs 1, Write code 3, hint explanation 1 equivalents; per-session and per-month limits block with the right message; usage log records kind and edits applied | Phase 1b |
| AI route selection | Engine tests | Developer mode off never reaches the CLI; with it on, each branch of the PLAN.md 2.5 rule (no CLI, signed out, under threshold, over threshold, CLI error mid-session) picks the documented route and logs it | Phase 1b |
| Part isolation | CI job checking changed paths against the PR label (`backend`, `engine`, `desktop`, `web`, `language`, `section`, `content`) | One part per PR | Phase 1a |
| Secrets | `gitleaks` in CI | No findings | Phase 1b |
| App smoke | CI launches the built installer on Windows and macOS runners, waits for the engine to answer `GET /local/ping`, runs the free problem's phase 1 tests through the local API | Passes on both | Phase 1c |
| Dependency hygiene | `./gradlew dependencyUpdates` and `pnpm outdated` monthly, Dependabot for security | No known critical vulnerability in shipped code | Phase 1c |

## Checklist gates (pull request template)

- The change is inside one part, or an ADR explains the contract change.
- Tests were written before or with the code, not after the review.
- No new `TODO` without an issue link; no commented-out code.
- Comments explain why on every non-obvious block and nowhere else.
- `CHANGELOG.md` has an entry under Unreleased.
- `docs/status.md` reflects what this PR changes.
- `/code-review` was run and each finding is fixed or has a written reason.
- `/simplify` was run on the diff.
- For UI changes: a screenshot or a short recording is attached.
- For runner or kit changes: the run protocol is byte-compatible, or the protocol version was bumped with an ADR.

## Phase exit

A phase is complete when the exit criteria in its prompt file are met, every automated gate is green on `main`, the status board shows no open blockers for that phase, and the human has tried the result once by hand.
