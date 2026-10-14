# Code standards: efficient, readable, maintainable

These rules exist so that code written quickly by an agent is still code a person can read in a year.

## Shape

- One responsibility per class or module. If a name needs "and", split it.
- Methods under about 40 lines, files under about 400 lines. Cross the limit only when splitting would scatter one idea across files.
- Depth over width: at most three levels of nesting; extract or return early beyond that.
- Prefer the standard library, then a dependency the repo already has, then new code. A new dependency needs one line in the PR saying why the standard library does not do it.
- No speculative abstraction: no interface with one implementation unless a contract in `docs/contracts/` requires it, no configuration for a value that never changes, no "for later" scaffolding.
- Delete what is no longer used in the same PR.

## Naming

- Names say what a thing is for in the domain's words: `EntitlementExpiry`, `PackCrypto`, `RunProtocolParser`. No `Util`, `Helper`, `Manager`, `Data`, `Info` suffixes.
- Booleans read as questions: `isLocked`, `hasVariant`. Collections are plural.
- Java packages follow the layout in PLAN.md section 5.2; TypeScript folders follow `pages`, `features`, `components`, `api`.

## Comments

- A comment states why, an invariant, a non-obvious ordering, or a workaround with a link. Example: `// Ends sort before starts within one minute so that touching holds do not overlap.`
- No comment that restates the next line. No commented-out code. No change logs in comments; git has them.
- `TODO(#123):` only with an issue number. CI rejects `TODO` without one.
- Javadoc or TSDoc on ports, contracts and public package APIs: one sentence of purpose plus anything a caller must know. Nothing on private members unless the why rule applies.
- Each non-trivial file starts with a two to four line header saying what it owns and what it must not do. Example: `/** Local HTTP API for the UI. Validates input and delegates; holds no state and no business rules. */`

## Formatting

- Java: google-java-format through Spotless; the `spotlessApply` task runs in the pre-commit hook. No manual formatting debates.
- TypeScript, JSON, Markdown: Prettier with the repo config; ESLint for rules Prettier does not cover.
- `.editorconfig` at the root: UTF-8, LF, final newline, 4 spaces for Java, 2 for TypeScript and JSON.

## Rust in the shell

- The shell is thin by design: commands that start the engine, read and write the keychain, report OS facts, run the updater, and later the `proctor` module. If a function needs domain knowledge, it belongs in the engine, not here.
- `rustfmt` and `clippy -D warnings` are gates. `Result<T, ShellError>` for every fallible function; `expect("why this cannot fail")` only during startup; no `unwrap` in command handlers.
- One `unsafe` boundary: the `proctor` module wraps Windows and macOS calls in safe functions with a comment stating the invariant that makes each block sound.
- Dependencies: Tauri and its official plugins, `serde`, `thiserror`, a keychain crate, and nothing else without a one-line reason in the PR.
- Tests: `cargo test` covers command argument validation and the `latest.json` reader; OS-specific code is tested on CI runners of that OS.

## Errors and logging

- Fail loudly at trust boundaries: validate input at the API, the pack reader and the run protocol parser; throw a typed error with a user-readable message.
- Never swallow an exception without a comment that says why it is safe (the prototype's "Windows keeps a file locked for a moment" comment is the model).
- Logs are structured, one line per event, no secrets, no file contents, no chat text.

## Tests

- Test names describe behaviour: `finishRecordsAPhaseThatFirstPassesInTheFinalRun`.
- One runnable check for every non-trivial branch, parser, scoring rule, money or security path. Runners and graders are tested against the real toolchain in CI, not mocked.
- Fixtures live beside the test; golden files for protocol output.

## Efficiency

- Measure before optimising; the calibration tool and `durationMs` fields exist for that.
- Hot paths in the engine (run protocol parsing, file restore) avoid per-line allocations where a buffer does; everything else favours clarity.
- No premature caching. Cache when a measurement shows the same work repeated.
