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
