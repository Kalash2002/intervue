# Commands

The commands we actually use, grouped by phase. Every phase adds a section (step 10 of `/phase`), and a command that stops working is fixed or removed in the same PR. Run commands from the repository root unless a line says otherwise. No secrets here: values like keys and emails are placeholders in `<angle brackets>`.

## Every phase: gates

```sh
pnpm install                                                 # install every TypeScript part (workspace root)
pnpm -r lint && pnpm -r typecheck && pnpm -r test            # TypeScript gates; `pnpm -r lint typecheck test` runs only lint
pnpm -C web format                                           # Prettier --write on web/
```

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

### Stop a stuck local server (Windows, PowerShell)

```powershell
Get-NetTCPConnection -LocalPort 8788 -State Listen | ForEach-Object { Stop-Process -Id $_.OwningProcess -Force }
```
