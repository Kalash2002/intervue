# Commands

The commands we actually use, grouped by phase. Every phase adds a section (step 10 of `/phase`), and a command that stops working is fixed or removed in the same PR. Run commands from the repository root unless a line says otherwise. No secrets here: values like keys and emails are placeholders in `<angle brackets>`.

## Every phase: gates

```sh
pnpm install                                                 # install every TypeScript part (workspace root)
pnpm -r lint && pnpm -r typecheck && pnpm -r test            # TypeScript gates; `pnpm -r lint typecheck test` runs only lint
pnpm -C web format                                           # Prettier --write on web/
```

## Every phase: Java gates

```sh
./gradlew check                                              # compile, Spotless, tests, ArchUnit, JaCoCo
./gradlew spotlessApply                                      # format Java
```

## Phase 1ad: DSA section

### Commands added or changed

```sh
./gradlew :content:tools:verify --offline                    # now reads content/problems and content/dsa, and checks coveredBy[] and dsa:// links both ways
./gradlew :content:tools:verify --offline -Pids=two-sum,valid-anagram   # -Pids takes DSA question ids as well as problem ids
./gradlew :engine:engineDist --offline                       # discovers every sections/*/java project; lib/ holds one jar per section (coding-round.jar, dsa-quick.jar), named by section id
./gradlew :sections:dsa-quick:java:test --offline            # the DSA section's own tests
pnpm -C packages/room test && pnpm -C packages/sections test && pnpm -C sections/dsa-quick/ui test   # the new TypeScript packages
```

- The engine takes one `--content <dir>` and reads `<dir>/problems` and, when it exists, `<dir>/dsa`; a question id may not appear in both roots.
- A new section adds its folder under `sections/` and one line in `packages/sections/src/index.ts` (`@intervue/sections`). No edit to `desktop/` or `engine/build.gradle.kts` (ADR 0008).
- `engineDist` fails with a clear message when two bundled jars share a file name (a section without its own `base.archivesName`), so no manual duplicate check is needed. Check after `engineDist`: `unzip -l engine/build/engine/lib/dsa-quick.jar` lists the section, and the engine jar manifest `Class-Path` names both section jars.

### Run a DSA session in the browser

```sh
pnpm -C desktop engine:dev                                   # terminal 1
pnpm -C desktop dev:browser                                  # terminal 2
# the UI is http://localhost:1420; the engine API is the same host under /engine (the Vite proxy adds the token)
curl -X POST localhost:1420/engine/sessions -H 'content-type: application/json' -d '{"problemId":"two-sum","type":"PRACTICE","timed":false}'
curl -X POST localhost:1420/engine/sessions/<id>/run -H 'content-type: application/json' -d '{"target":"phase:1"}'   # DSA runs only phase:1
curl -X PUT 'localhost:1420/engine/sessions/<id>/file?path=TwoSum.java' -H 'content-type: application/json' -d '{"text":"..."}'
curl -X POST localhost:1420/engine/sessions/<id>/finish -H 'content-type: application/json' -d '{"early":false}'      # the view carries events and result
curl localhost:1420/engine/sessions                          # history rows, each with sectionId
```

Screenshots follow the Phase 1ac recipe below (headless Edge with `--disable-extensions`), into `docs/plans/phase-1ad-evidence/`.

### Phase 1ad fixes: DSA shape, channels, low-memory verify

```sh
./gradlew :content:tools:verify --offline -Pids=two-sum,valid-anagram,group-anagrams   # a few ids at a time on a low-memory machine
./gradlew :shared-java:problem-format:test --offline          # validator rules, incl. the DSA statement and answer cases
./gradlew :engine:test --offline                              # channel refusals (HTTP 400)
pnpm -C packages/editor test && pnpm -C sections/dsa-quick/ui test   # auto-import, Statement, hints/no-Assistant room
```

- Verify now checks every `dsa-quick` question's `statement.md` (Example blocks, Constraints), `walkthrough.md` (headings in order, brute force, better, optimal, one figure that exists) and starter (`import java.util.*;`). A wrong shape fails with the file and the missing heading. These rules key on the section id `dsa-quick`.
- A section without a channel gets a real HTTP 400 before the stream opens: `POST /sessions/<id>/ask`, `/write` and `/hints/explain` on a DSA session (no ASK or WRITE), and hints where the section has none. The coding round keeps ask, write and hints. Check: `curl -i -X POST localhost:1420/engine/sessions/<id>/ask -H 'content-type: application/json' -d '{"message":"hi"}'` on a DSA session returns `400`.
- The session view carries `channels` and `hintsAllowed`; the DSA room reads them (Hints shown, Assistant tab absent).
- Low memory: verify runs three JVM passes per question. Do not run all 49 DSA plus the 10 coding-round problems with other builds open; pass `-Pids` with three to five ids at a time and repeat until all 59 have passed. Run `./gradlew --stop` between chunks if memory is tight.
- Evidence recipe is unchanged (headless Edge, `--disable-extensions`), into `docs/plans/phase-1ad-fixes-evidence/`.

## Phase 1ac: shell, Home, section page, theme, tags

### Gates added in this phase

```sh
pnpm -C packages/ui test                                     # includes tokens.test.ts (every token present, text at 4.5:1 in both themes) and the Home, section page and Markdown chip tests
PRINT_CONTRAST=1 pnpm -C packages/ui exec vitest run test/tokens.test.ts   # prints every measured contrast ratio
grep -rnE '#[0-9a-fA-F]{6}' packages/ui/src/styles | grep -v tokens.css    # must print nothing: colour literals live in tokens.css only
grep -rn 'neetcode.io\|leetcode.com' content/problems         # must print nothing: walkthroughs link to dsa://<id> chips
./gradlew :content:tools:verify --offline                    # all ten problems three ways; tags[] validated with the rest of problem.json
```

### Screenshots for the evidence folder

```sh
pnpm -C desktop engine:dev                                   # terminal 1
pnpm -C desktop dev:browser                                  # terminal 2, UI on http://localhost:1420
msedge --headless=new --disable-extensions --hide-scrollbars --remote-debugging-port=9222 --user-data-dir=<temp dir> about:blank   # terminal 3; --disable-extensions matters: the profile's Dark Reader otherwise darkens every capture; then drive it over the DevTools protocol (the script lives in the phase's scratch folder: navigate, click the menu and cards, Page.captureScreenshot at 1366x768 and 1920x1080, both themes) into docs/plans/phase-1ac-evidence/
```

The app has no URLs (PLAN.md 5.2: the shell has no address bar), so a plain `--screenshot` run shows Home only; every other page needs the DevTools route or the built-in browser.

## Phase 1ab: assistant on the developer route and the room flow

### Developer mode

```sh
pnpm -C desktop dev                                          # debug shell passes --developer-mode to the engine; release builds never do
pnpm -C desktop engine:dev                                   # browser mode engine, already started with --developer-mode
java -jar engine/build/engine/engine.jar --data <dir> --content content --developer-mode   # the engine alone with the CLI route on
```

The engine finds the Claude CLI through `INTERVUE_CLAUDE` (path to `claude` or `claude.exe`), then `PATH` (an npm shim resolves to its bundled `claude.exe`). Sign in once with `claude auth login`; `claude auth status` shows the account. Without `--developer-mode` the assistant shows "Assistant unavailable: sign-in arrives in Phase 1b" and no `claude` process starts.

### Check the CLI and read the calls

```sh
TOKEN=$(cat desktop/.dev-data/dev-token)
curl -H "Authorization: Bearer $TOKEN" http://127.0.0.1:4100/ai/status                  # {developer, available, reason}
curl -X POST -H "Authorization: Bearer $TOKEN" http://127.0.0.1:4100/health/run          # includes ai.cli "Claude CLI found and signed in" (warning only)
curl -H "Authorization: Bearer $TOKEN" http://127.0.0.1:4100/sessions/<id>/chat          # chat turns of a session
curl -H "Authorization: Bearer $TOKEN" http://127.0.0.1:4100/sessions/<id>/review        # the written review (requested at finish)
curl -H "Authorization: Bearer $TOKEN" http://127.0.0.1:4100/sessions/<id>               # events of type aiCall: kind, route, model, tokens, durationMs; never text
```

`POST /sessions/<id>/ask`, `/write` and `/hints/explain` stream NDJSON lines `{"t":"delta"|"done"|"error"}`. `POST /sessions/<id>/finish` takes `{"early": true}` to record ending early.

### Tests

```sh
./gradlew :sections:coding-round:java:test --tests '*PointAndExplainTranscriptTest'   # recorded-transcript tests per prompt rule (no model calls)
./gradlew :engine:test --tests '*DeveloperModeTest'                             # no claude process with developer mode off (fake CLI)
pnpm -C sections/coding-round/ui test                                           # room flow, assistant panel, layout guard
```

## Phase 1a: base app (`engine/`, `desktop/`, `packages/`, `content/`)

### Run the app

```sh
pnpm -C desktop dev                                          # build engine.jar, start the Tauri shell (shell starts the engine)
pnpm -C desktop engine:dev                                   # browser mode, terminal 1: engine on 127.0.0.1:4100, data in desktop/.dev-data
pnpm -C desktop dev:browser                                  # browser mode, terminal 2: UI on http://localhost:1420, proxy adds the dev token
./gradlew :engine:engineDist                                 # engine.jar plus lib/ in engine/build/engine
java -jar engine/build/engine/engine.jar --data <dir> --content content [--port <n>]   # the engine alone; prints INTERVUE_ENGINE_READY port=<n>
```

Without `INTERVUE_ENGINE_TOKEN` the engine writes a fresh token to `<data>/dev-token`. Every request needs `Authorization: Bearer <token>`:

```sh
curl -H "Authorization: Bearer $(cat desktop/.dev-data/dev-token)" http://127.0.0.1:4100/local/ping
curl -X POST -H "Authorization: Bearer $(cat desktop/.dev-data/dev-token)" http://127.0.0.1:4100/health/run   # all Doctor checks
```

### Content

```sh
./gradlew :content:tools:verify                              # verify every problem three ways (starter fails, reference 5x headroom, naive times out at 3x)
./gradlew :content:tools:verify -Pids=hold-exposure          # one or more problems, comma-separated
```

### Build installers

```sh
node desktop/scripts/jlink.ts                                # desktop/resources/runtime from JAVA_HOME (or the java on PATH)
pnpm -C desktop build                                        # engine.jar, runtime, then tauri build with resources: NSIS on Windows, DMG on macOS
```

Unsigned builds: Windows SmartScreen and macOS Gatekeeper warn on first start; signing is Phase 1c.

### Shell gates (from `desktop/src-tauri`)

```sh
cargo fmt --check && cargo clippy --all-targets -- -D warnings && cargo test
```

### Data on this machine

The app data folder holds `intervue.db` (SQLite), `sessions/<id>/work` and `engine.log`. Delete the folder to start fresh; the next launch runs the Doctor again.

## Phase 0: landing page and waitlist (`web/`)

### Run locally

```sh
pnpm -C web dev                                              # page only, http://localhost:5173 (/api proxied to :8788)
pnpm -C web preview                                          # build, then page + functions + local KV on http://localhost:8788
```

Local KV lives in `web/.wrangler/`, never committed.

### Try the waitlist endpoint by hand

```sh
curl -X POST http://localhost:8788/api/waitlist -H "content-type: application/json" --data-binary @signup.json
```

`signup.json` holds `{"email":"<you@example.com>","role":"Final-year student","companies":"","language":"Java","pay":"₹499 a month (about $6)","hp":""}`. Use a file on Windows: Git Bash mangles `₹` in inline arguments. Expect 200 for valid, 400 for bad input, 413 over 4 kB.

### Cloudflare (from `web/`)

```sh
pnpm exec wrangler login                                     # once per machine, opens the browser
pnpm exec wrangler kv namespace create WAITLIST              # once; put the printed id in web/wrangler.toml
pnpm pages:deploy                                            # typecheck, build, upload; preview URL on a feature branch, production on main
```

A new deployment URL (`<hash>.intervue-web.pages.dev`) can fail with "can't provide a secure connection" for a few minutes until its certificate is issued; the project URL `https://intervue-web.pages.dev` works at once.

### Read the waitlist (from `web/`)

```sh
pnpm exec wrangler kv key list --binding WAITLIST --remote
pnpm exec wrangler kv key get --binding WAITLIST --remote "signup:<email>"
pnpm exec wrangler kv key delete --binding WAITLIST --remote "signup:<email>"   # remove a test entry
```

Drop `--remote` and add `--local` to read the local dev store instead.

### Prototype download (from the repository root)

```sh
(cd prototype && java tools/Verify.java)                     # all three problems must print OK before a new zip
git -c core.autocrlf=false archive --format=zip --prefix=intervue-prototype/ --add-file=web/prototype/RUN.md -o web/public/downloads/intervue-prototype.zip HEAD:prototype
```

The zip holds the committed `prototype/` (never local sessions) plus `RUN.md` at its root. Keep `core.autocrlf=false`: without it Git on Windows writes CRLF into `run.sh` and it breaks on macOS and Linux. Rebuild after any prototype commit, then deploy.

### Stop a stuck local server (Windows, PowerShell)

```powershell
Get-NetTCPConnection -LocalPort 8788 -State Listen | ForEach-Object { Stop-Process -Id $_.OwningProcess -Force }
```
