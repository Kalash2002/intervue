# Intervue: product and engineering plan

Prepared 2026-10-14 from the prototype in this folder, the thirteen fixed decisions, and the answers given in Step 1.
Every figure marked **(est.)** is an estimate with its assumption stated next to it. Every price or policy that changes over time carries a source and the date it was checked. Where I could not verify something, the text says **not verified** rather than guessing.

## How to read this document

- Sections 1 to 13 follow the order requested. Tables carry the comparisons, costs and the roadmap; prose is kept short.
- "Engine" means the local Java process that replaces `server/Server.java`. "Shell" means the Tauri 2 window around it, written in Rust, showing the React UI in the system webview. "Backend" means the Spring Boot service in the cloud. "Website" means the React site for marketing, account, store and download.
- Answers from Step 1 that shaped the plan: India plus international from day one with the payment provider chosen later; 8 to 20 hours a week, ₹3k to 10k a month, first paying user within 3 months; React and Spring Boot; Windows and macOS together; at least 15 problems with content produced in parallel and dropped into a folder; packs sold monthly and quarterly at launch (₹1,500 and ₹3,500, configurable; no annual term until the catalogue justifies it); a payment page with a mock gateway behind a pluggable port, the real gateway integrated when you choose one; Tauri 2 in Rust as the desktop shell, with the UI still in React and TypeScript, and a per-phase learning folder with tutor prompts; an assistant with Ask and Write code channels in both session types, Interview sessions marked assisted when AI edits are used, a Practice session type with three-level authored hints and no proctoring, edits applied immediately with undo on a copy of the problem, and editor autocomplete with language-server plugins (section 2.8); Indian or India-billed infrastructure where it is cost-effective; one device by default with purchasable slots; lock and delete pack content on the first online check after expiry; user data kept on the server for the life of the account; admin-generated single-use redeem codes; promo codes at purchase; a later proctored "industry" mode.

## One paragraph on the thirteen decisions

I am not reopening any of them. Two carry a cost worth naming so the roadmap is honest about it. Decision 7's rule that a language ships only when every problem in the catalogue has a verified variant is correct for the user, but it makes each new language a catalogue-sized project, so the roadmap prices it that way and the authoring pipeline is built to port in bulk. Decision 13's encryption at rest protects against casual copying of pack files between machines; it cannot stop the owner of a signed-in machine from extracting the one problem that is open in front of them, because the user must be able to read and edit that problem's files. The plan states this plainly in section 2 and spends no further effort pretending otherwise. Decision 4's "Sonnet 5.5 at low effort for everything" is kept for chat; section 2 argues for medium effort on the one reviewer-feedback call per session, with the cost shown in section 9, and you can overrule it with a configuration value.

## Contents

1. Product definition
2. High-level design (architecture, offline-first, pack delivery and auth, code execution, AI gateway, extensibility model, industry mode, session types and assistant channels)
3. Tech stack, desktop packaging, the Tauri decision, Indian and cost-effective infrastructure
4. Multi-language runtime
5. Repository layout, architecture and independent delivery
6. Setup guide and environment health check
7. Content system
8. Payments and licensing
9. Economics
10. Phased roadmap
11. What you need to learn
12. Risks and blockers
13. First two weeks
14. The decisions, as they stand
15. Engineering process: prompts, gates, review and the agent team

Appendix: sources checked on 2026-10-14 are cited inline. The raw research notes behind them sit beside this file in `docs/plan/research/` on the author's machine only; they are working documents and `.gitignore` keeps them out of the repository, because this plan carries every citation that matters. The process files live in `CLAUDE.md`, `CONTRIBUTING.md`, `docs/process/`, `docs/prompts/`, `.claude/agents/` and `.claude/skills/phase/`; the minimal learning per phase, each with a tutor prompt, lives in `docs/learning/`.

## 1. Product definition

### Who it is for and the promise

Intervue is for engineers preparing for the AI-assisted coding round: a timed session in a codebase they did not write, with an assistant that can answer or write code, graded on tests and on how they worked, including how they used the AI. Primary buyers at launch are Indian working engineers with one to five years of experience and final-year students; international buyers are served from day one with USD pricing and card payments.

One-sentence promise: **rehearse the real AI-assisted interview on your own machine, with the same constraints, and get a score and a written review you can act on.**

| | Free | Paid |
|---|---|---|
| App | Full desktop app, every feature | Same app |
| Content | Two built-in problems at launch (Hold Exposure and Corridor Router; ADR 0001) in every supported language | Packs of problems, sold per pack per month or per quarter; longer terms only when the catalogue has grown |
| AI assistant (Ask and Write code) and written feedback | Included within the per-user caps in section 9 | Same caps, raised for paying users |
| Session types and hints | Interview and Practice on the free problems, with their three-level hints | Same on every pack problem; hints ship inside the pack |
| Editor assistance | Autocomplete, symbols and inline compiler diagnostics; language-server IntelliSense as an optional download | Same |
| Progress, history, dashboard | Included, synced | Included, synced |
| Devices | 1 active device | 1 by default; extra device slots purchasable |

Users never pay for the app, for a language or for a section as such. They pay for content packs. A pack's entitlement covers every language variant of every problem in it, and every section that pack contains.

### Website versus app

| Concern | Website | Desktop app |
|---|---|---|
| Marketing, pricing, FAQ, setup guide | Yes | Links to it |
| Account: sign in, devices, privacy, delete account | Yes | Sign in via the website (device flow), view devices |
| Store: buy, renew, redeem a code, apply a promo code, invoices | Yes | Opens the website |
| Download installers | Yes | Self-updates |
| Dashboard and history | Phase 3: read-only view of synced sessions, scores, feedback | Yes, always, offline too |
| Practice (timed sessions) | Not at launch; phase 5 evaluates in-browser practice | Yes, the core of the product |
| AI assistant and written feedback | Phase 3: request feedback for synced sessions | Yes when online |
| Admin: codes, grants, flags | Yes, protected area | No |

Both exist because the practice must run on the user's machine (decision 9) while purchase, identity and history must live where any device can reach them (decisions 2, 3, 13). The website is the account and store; the app is the practice room.

### Languages and sections as the user sees them

- **Launch is Java only (ADR 0007, 2026-10-15).** The picker below appears once a second language package ships (Phase 6 on); until then the set-up screen shows Java as a fixed field.
- **Choosing a language.** A language picker sits in the app header and on the website profile. It changes which variant of every problem is shown: starter files, tests, reference and naive solutions, editor mode and health checks. Briefs, phases, walkthroughs and scores are the same for all languages. A language appears in the picker only when its package passes the health check on this machine and every problem in the installed packs has a variant for it.
- **Switching language on the same problem.** Allowed before the clock starts. During a session the language is fixed; the session records it. A second attempt can use another language.
- **Progress across languages.** A problem is "solved" when any finished session in any language passes every phase. The dashboard shows per-problem best score with the language of that best attempt, and a small per-language breakdown on the problem page. Totals (solved, attempted, time practised) count problems, not variants, so switching language never inflates progress.
- **How a new section shows up.** A section package registers itself with the shell: a name, an icon, a session UI mode, a result-page layout and a scoring rule. Navigation gains a tab; the dashboard gains a row group; the store shows packs tagged with that section; filters gain the section value. The shell code does not change (section 5 proves this with the system design example).

### Session types and the assistant

A session starts as **Interview** or **Practice** (section 2.8). Interview is the timed rehearsal: no hints, the full score, and proctoring later; the assistant follows point-and-explain rules (it names the place and the logic, writes code only for a named piece), and a passing Interview session counts as solved whether or not the assistant edited code; the written review grades how the AI was driven (ADR 0006). Practice is for learning: a stopwatch instead of a limit, three authored hint levels per phase with an online "explain further", Write code without a label, and a score shown as "Practice" that never counts as solved. In both types the assistant has two channels, **Ask** and **Write code**; Write code edits the session's copy of the files immediately with one-click undo, the problem source is never touched, and "Reset problem" recreates the copy.

### Filtering

Every problem carries: section, techniques (one or more from the NeetCode 150 topic groups), difficulty (Easy, Medium, Hard), domain (e-commerce, fintech, logistics, social, streaming, healthcare, developer tools, gaming, other), and the languages with a verified variant. Filters combine with AND across axes and OR within an axis. Example: section = coding round AND technique in {graphs, heaps} AND difficulty = Medium AND language = Python.

### Offline versus online (decision 12)

| Capability | Offline | Online |
|---|---|---|
| Start a timed session on an owned, downloaded pack or the free problem | Yes | Yes |
| Compile, run, phase unlock, timeline, clock | Yes | Yes |
| Score, per-phase results, walkthrough, follow-up questions, notes | Yes, computed locally | Yes |
| AI assistant, Ask and Write code | No. The session page shows "Offline: no assistant this session" before the clock starts, and the timeline records the session as assistant-less | Yes, through the backend |
| Hints (Practice; Interview where the section allows it, ADR 0012) | Yes, authored levels are pack content | Yes, plus "explain this hint further" |
| Editor autocomplete and compiler diagnostics | Yes | Yes |
| Language-server IntelliSense | Yes if the server was downloaded earlier | Yes |
| Written feedback | No. The result page shows "Request feedback when online"; the request is queued | Yes, streamed; for synced offline sessions, requested later and attached |
| Dashboard, history | Local sessions, all of them | Local plus other devices' synced sessions |
| Pack download, entitlement refresh, sign-in | No. Owned packs stay usable until their local expiry | Yes |
| Expired or refunded pack | Locked at the stored expiry time; content deleted at the next online check | Locked and deleted on the first check |

## 2. High-level design

### 2.1 Architecture

```
                 +--------------------------+        +---------------------------+
                 |  Website (React, static) |        |  Desktop app              |
                 |  marketing, account,     |        |  Tauri 2 shell (Rust)     |
                 |  store, download, admin  |        |   - window, tray, updater |
                 +------------+-------------+        |   - device secret (OS     |
                              | HTTPS/JSON            |     keychain)             |
                              |                       |   - native checks (later: |
                              v                       |     proctoring)           |
+-------------+   +-----------+---------------+       |  React UI (shared pkg)    |
| Payment     |-->|  Backend (Spring Boot 3,  |<------|  Engine (Java 21, local   |
| provider    |   |  Java 21) on one VPS      | HTTPS |   HTTP on 127.0.0.1)      |
| webhooks    |   |  - identity & devices     |       |   - sessions, runs, score |
+-------------+   |  - catalog & packs        |       |   - language runners      |
                  |  - entitlements & codes   |       |   - pack client (verify,  |
+-------------+   |  - signed URLs & keys     |       |     decrypt, cache)       |
| Claude API  |<--|  - sync                   |       |   - sync client (outbox)  |
| (Anthropic) |   |  - AI gateway             |       |   - health checks         |
+-------------+   |  - admin & flags          |       |   - SQLite store          |
                  +-----+-----------+---------+       +-------------+-------------+
                        |           |                               |
                        v           v                               | signed, short-lived URLs
                  +-----------+ +---------------------+             v
                  | Postgres  | | Object storage + CDN|<-------------+
                  | (same VPS | | (Cloudflare R2):    |  encrypted packs, installers,
                  |  at first)| | packs, installers,  |  language toolchains, update feeds
                  +-----------+ | toolchains, feeds   |
                                +---------------------+
```

Code execution happens only inside the engine on the user's machine at launch. The engine is the same Java program that would run inside a container if website practice is ever built (section 2.4), which is the main reason the local core stays in Java rather than being rewritten in Rust inside the shell.

### 2.2 Offline-first design for the app

**Local store.** SQLite (one file per OS user profile, `intervue.db`), accessed from the engine through `sqlite-jdbc`. It replaces `session.json`. Tables:

| Table | Key columns | Notes |
|---|---|---|
| `sessions` | id, problem_id, variant_lang, section_id, pack_id, session_type (interview, practice), started_at, finished_at, minutes, active_ms, unlocked, assisted, hints_used (JSON), final_results (JSON), score (JSON), sync_state | One row per attempt; immutable after finish except `notes` and `feedback` |
| `events` | session_id, seq, t_ms, type, data (JSON) | Append-only timeline; `seq` is per session; types include `aiEdit`, `aiEditUndo`, `hint`, `resetFile`, `resetProblem` (section 2.8) |
| `edits` | session_id, edit_id, reply_id, path, op, before_hash, after_hash, snapshot_path, undone_at | One row per applied AI edit; snapshots live under `sessions/<id>/edits/` |
| `chat_turns` | session_id, seq, role, text, model, t_ms | Append-only |
| `session_files` | session_id, path, content, is_diff_from_starter | Written at finish only; the working copy lives on disk under `sessions/<id>/work/` as today |
| `sync_outbox` | kind, ref_id, payload, created_at, attempts, last_error | Everything not yet acknowledged by the backend |
| `packs` | pack_id, version, format_version, manifest (JSON), state (downloaded, locked, deleted), content_key_wrapped, downloaded_at | |
| `entitlements_cache` | entitlement_id, pack_id, expires_at, device_limit, status, signed_blob, fetched_at | Signed by the backend; verified with the embedded public key |
| `settings` | key, value | language, theme, calibration factors, last server time offset |
| `health_results` | check_id, status, measured, ran_at | Fed to the dashboard and the copy-report button |
| `schema_version` | version | Migrations run by the engine at start; forward-only |

The working files of a session stay on disk because javac and the other toolchains need real files. Everything else moves into SQLite so sync can ask "what is not yet uploaded".

**Cached locally:** downloaded packs (encrypted), the free problem (plaintext inside the installer), the user's profile and entitlement blobs, calibration results, the last known server time, feature flags with their fetch time.

**Sync protocol.** Append-only, device-owned, cursor-based.

| Endpoint | Direction | Payload | Rule |
|---|---|---|---|
| `POST /v1/sync/push` | app to backend | `{deviceId, sessions:[{session, events[seq>ack], chatTurns[seq>ack], files? , notes, notesUpdatedAt}]}` | Server stores by (sessionId, seq); duplicates are ignored. Returns `{acks:[{sessionId, eventSeq, chatSeq}], serverTime}` |
| `GET /v1/sync/pull?cursor=` | backend to app | Summaries of finished sessions from other devices, feedback texts, entitlement changes, flag changes, `nextCursor` | The app never pulls another device's working files; only summaries, scores and feedback |
| `POST /v1/sync/feedback-request` | app to backend | `{sessionId}` | Queues the deferred AI feedback job for a synced offline session |

Conflict rules: a session is owned by the device that created it and is immutable once finished, so there is nothing to merge. `notes` uses last-writer-wins by `notesUpdatedAt`. Feedback is produced server-side and attaches to the session; if a device also holds a local (prototype-era) feedback text, the server copy wins. Entitlements are server-authoritative; the local cache is a signed copy with an expiry.

**AI features offline.** The session page asks the engine `GET /local/health/ai` before the clock starts and shows the offline banner when the backend is unreachable or the user is signed out. The chat panel is disabled and the result page offers "Request feedback when online". The request is placed in `sync_outbox` as `feedback-request`; when the session and its files have been pushed, the backend runs the feedback job and the next pull brings the text back. The website shows the same text once Phase 3 ships.

### 2.3 Pack delivery and app authentication, end to end

**Sign-in (device authorization flow).**

1. App calls `POST /v1/auth/device/start {deviceId, devicePublicKey, appVersion, os}`; gets `{deviceCode, userCode, verificationUrl, expiresIn:600, interval:5}`.
2. App opens `https://intervue.si/activate?code=USER-CODE` in the system browser. The user signs in on the website (email magic link or Google) and approves the device; the page shows the device name and OS.
3. App polls `POST /v1/auth/device/token {deviceCode}` until it receives `{accessToken (15 min, JWT), refreshToken (opaque, 90 days, rotating, bound to deviceId)}`.
4. The refresh token is stored in the operating system keychain (Windows Credential Manager, macOS Keychain) by the Rust shell through a keychain crate such as `keyring`, whose current API the research step verifies, and handed to the engine at startup over the local socket, never written in plaintext.
5. Refresh: `POST /v1/auth/refresh` rotates the token; reuse of an old token revokes the whole device. Sign-out: `POST /v1/auth/devices/{id}/revoke`; "sign out everywhere" revokes all devices and bumps the user's token generation so every access token dies within 15 minutes.

**Entitlement check.** Every app start while online, every 24 hours while running, and before any pack download: `GET /v1/me/entitlements` returns a list of `{entitlementId, packId, status, expiresAt, deviceLimit, grantType}` plus a detached signature (Ed25519, backend key, public key embedded in the app). The engine stores the signed blob in `entitlements_cache` and enforces `expiresAt` offline.

**Signed CDN URLs.** `POST /v1/packs/{packId}/download` checks the entitlement and the device count, then returns `{url, expiresAt (+10 min), sha256, size, version}`. The URL is a Cloudflare R2 presigned GET; nothing else can read the bucket. Installers and toolchains are public objects on the same bucket behind the CDN, because they contain nothing paid.

**Pack verification.** A pack is `pack-<id>-<version>.drp`: a zip containing `manifest.json`, `manifest.sig` and `content.enc`. The engine verifies `manifest.sig` with the embedded publisher public key, compares `sha256(content.enc)` with the manifest, checks `formatVersion` against what this app and the installed language and section packages can read, and only then stores the file.

**Encryption at rest and unlocking.** `content.enc` is AES-256-GCM encrypted with a random per-pack-version content key. The content key never travels with the pack. `GET /v1/packs/{packId}/key` returns the content key wrapped for this device's public key (X25519, generated at first run; the private key is stored in the OS keychain by the shell). The engine keeps the wrapped key in `packs.content_key_wrapped` and unwraps it in memory only while the signed-in user holds a valid, unexpired entitlement in the cache. Opening a problem decrypts that problem's files to `sessions/<id>/work/` because the toolchains need plaintext; reference solutions, grader notes and walkthroughs are decrypted in memory and streamed to the UI, never written.

**The free built-in problem** ships unencrypted inside the installer at `resources/free/hold-exposure/` with the same folder format, and is loaded by the same content store behind a `FreeProblemSource`. It needs no sign-in, which is what lets a new user start a session before creating an account.

**Failure states shown to the user.**

| Situation | What the user sees | What the engine does |
|---|---|---|
| Download fails or hash mismatch | "Download interrupted, retry" with the byte count | Deletes the partial file; retries with backoff; nothing is installed until verification passes |
| Entitlement expired (local clock) | Pack card shows "Expired on <date>. Renew" | Locks the pack: sessions cannot start; existing sessions and results open with a "repurchase to view walkthrough" note |
| Entitlement revoked or expired (server says so) | Same card, plus "content removed from this device" | Deletes `content.enc` and the wrapped key; keeps sessions, events, scores and notes; marks the pack `deleted` |
| Repurchase | Pack card returns to "Download" | Downloads the current version; history reappears intact because it never left the database and the server |
| Device limit hit | "This account is active on N devices. Sign out one on the website or add a device slot" | No tokens are issued until a device is revoked or a slot is bought |
| Clock set backwards past the last server time | "Clock problem: connect once to continue" | Treats entitlements as expired until an online check succeeds |

**Pack updates.** The manifest carries `version` and `minAppVersion`. `GET /v1/catalog` lists current versions; the engine compares with `packs.version`, downloads in the background, verifies, and swaps on the next app start so a running session is never disturbed. A pack update never changes the format of a session already recorded.

**Storage and CDN options and cost.** Cloudflare R2 is the default because egress is free, which matters for installers of 100 MB or more; the alternatives are Backblaze B2 with Cloudflare in front, or AWS S3 with CloudFront. Verified prices are in section 9.

**What this does and does not do against piracy.** It stops pack files being copied between machines, stops a signed-out or expired account from opening content, and stops a revoked device from keeping its packs. It does not stop a paying user from copying the one problem they are working on, from screenshotting walkthroughs, or from patching the app on their own machine. Pricing, frequent content updates and a fair device policy do more against piracy than more cryptography would.

### 2.4 Code execution

Local in the app at launch. For the website, three options:

| Option | Security model | Cost per language (est.) | When to pick |
|---|---|---|---|
| (a) No execution on the website; app only | Nothing untrusted runs on your servers | ₹0 | Default until website practice has proven demand |
| (b) Sandboxed containers per language (the engine image plus the language toolchain, gVisor or Firecracker, 1 vCPU, 512 MB, 60 s cap per run) | Kernel-level isolation, network off, read-only root, per-run container | One shared VPS with 4 vCPU runs about 8 concurrent sessions; roughly ₹1,500 to 3,000 a month per language image kept warm, plus your time to harden it | When website practice is wanted and you can own the ops |
| (c) Third-party execution API (Judge0 or similar) | Their sandbox; your tests and data leave your control | Per-execution pricing; a 60-minute session with 20 runs costs more than (b) at any real volume, and custom multi-file projects with a test kit fit poorly | Only for a quick experiment |

Recommendation: (a) now, (b) later, with the engine running unchanged inside the container so the website and the app share one runner.

### 2.5 AI gateway

One backend path for every model call. The app never holds an API key.

| Endpoint | Purpose | Notes |
|---|---|---|
| `POST /v1/ai/chat` (SSE) | Assistant reply for a session | Body: `{sessionId, problemRef:{packId, problemId, lang}, mode, message, files:[{path, content, readOnly}], history:[last N turns]}`. The backend holds the brief and the rules, so the app sends only files and history |
| `POST /v1/ai/edit` (SSE) | Write code channel | Same body as chat plus `target` (optional file or method named by the user) and per-file hashes; streams the edit protocol of section 2.8; costs three message-equivalents |
| `POST /v1/ai/hint-explain` (SSE) | Practice only | Body: `{sessionId, problemRef, phase, level, files, history}`; the backend reads the authored hint text from the pack; Ask rules; one message-equivalent |
| `POST /v1/ai/feedback` | Written review | Body: `{sessionId}`; the backend reads the synced session including edit and hint events. Returns a job id; result delivered by SSE or by pull |
| `GET /v1/ai/usage` | Remaining allowance | Shown in the app header |

Internals: `ModelClient` interface with `AnthropicModelClient` (SDK, `claude-sonnet-5-5`, effort `low` for chat, `medium` for feedback) and `CliModelClient` (the prototype's `claude -p` path, kept for your own development without spending API money). `PromptBuilder` is section-aware and language-aware through data, not code: it takes the section's `prompts/system.md`, `prompts/interview-mode.md` or `prompts/helpful-mode.md`, and for the Write code channel `prompts/write-code.md` with the edit protocol and the list of files that must not be edited, then the problem's brief, the variant's read-only files, then the editable files with their hashes, then history (past edits summarised to one line each), then the message. The language only changes the fence tag and a one-line "Plain <language> <version>, standard library only" rule read from the language definition.

Prompt caching: order is rules, brief, read-only files (cache breakpoint), editable files, history, message. The stable prefix is cached with a 1-hour TTL so a 60-minute session pays the write once. Limits are counted in message-equivalents: Ask and hint explanations cost one, Write code costs three; per session and per month per plan, one concurrent call per user; paying users get higher caps (numbers in section 9). Logging: `ai_usage(user_id, session_id, kind (chat, edit, hint, feedback), model, input_tokens, cache_read, cache_write, output_tokens, cost_micros, latency_ms, refused, edits_returned, edits_applied)`. Refusals: handle `stop_reason = refusal` by showing "The assistant declined this message" and not charging the user's allowance.

Swapping out the CLI path: `Chat.chat` and `Chat.feedback` in the prototype call `Claude.ask`. In the engine those two call sites become `AiClient.chat`, `AiClient.edit`, `AiClient.hintExplain` and `AiClient.feedback`, which talk to the backend; the backend's gateway calls `ModelClient`. Nothing else in the engine or UI changes.

**Developer-mode routing between the CLI and the backend (added 2026-10-14).** The CLI adapter is for your own machine only; a personal subscription is not a backend for other people's sessions, so this rule exists only when the engine setting `developer.mode` is on, and end-user builds never read it. With developer mode on and `ai.provider = auto`, `AiClient` picks a route per call:

| Condition | Route |
|---|---|
| CLI found on PATH, `claude --version` succeeds, a one-token probe shows it is signed in, and the remaining allowance is above `ai.cliMinRemainingPercent` (default 50, configurable) | `CliModelClient`: `claude -p` with the Sonnet 5.5 model id and low effort where the CLI exposes an effort setting |
| Any of those false, or the CLI returned a usage-limit or sign-in error earlier in this session | Backend, exactly as end users get it |

Every call logs the route it took, the usage reported and the reason for any switch. The remaining allowance is the open point: the interactive `/usage` screen shows a percentage, but no documented non-interactive command or output field for it was found when this was written, and the streamed `result` message reports the call's tokens and cost, not the remaining allowance. Until the `researcher` step verifies a way to read it, the engine keeps a local budget: it sums the CLI's reported usage per rolling window against a configurable budget (`ai.cliWindowBudgetTokens`, `ai.cliWindowHours`) and treats a usage-limit error as zero remaining, so "50% left" means 50% of that local budget. Whether the CLI accepts an effort setting non-interactively is also unverified; if it does not, only the backend route is guaranteed low effort. Both facts are on the Phase 1b research list.

Recorded in Phase 1ab (CLI 2.1.299): `claude auth status` reports sign-in without spending tokens and replaces the one-token probe; `--effort low` is accepted non-interactively with `-p`; the CLI runs with no tools (`--tools ""`, MCP disallowed, one turn, no session persistence) and the prompt on stdin. Phase 1ab uses the CLI route whenever `developer.mode` is on and the CLI is signed in; the remaining-allowance rule and the local budget stay open for Phase 1b.

### 2.6 Extensibility model

Entities and fields:

| Entity | Fields |
|---|---|
| `Section` | id, name, version, runnerKind (code-tests, none, simulator), graderKind (tests, rubric, reviewer-model, multiple-choice), sessionUiMode, scoringRuleId, resultParts[], healthChecks[], promptTemplates |
| `Track` | id, name (NeetCode topic group), order, description |
| `Problem` | id, version, sectionId, title, summary, difficulty, techniques[] (track ids), domain, minutes, phases[], followUps[], briefPaths, walkthroughPath, gradingNotesPath |
| `Phase` | id, title, briefPath, hintsPath (three levels), weight, allOrNothing, testsRef (per variant), timeoutRefMs |
| `ProblemVariant` | problemId, language, starterDir, readOnly[], mainEntry, testsPerPhase{phaseId: testRef}, referenceDir, naiveDir, dataFiles[], verifiedAt, verifiedWith (toolchain version) |
| `Pack` | id, version, formatVersion, title, description, sectionIds[], languages[], problemRefs[{id, version, sha256}], minAppVersion, publishedAt, signature |
| `Entitlement` | id, userId, packId, grantType (purchase, redeem, admin), planId, term (month, quarter; others later), pricePaid, currency, startsAt, expiresAt, status (active, expired, revoked, refunded), deviceLimit, providerRef |
| `Plan` | id, packId, term, currency, amount, aiCaps{messagesPerMonth, messagesPerSession, feedbackPerMonth}, effectiveFrom, effectiveTo, active. Prices are data edited in the admin page; the website and the app read the same catalog |
| `Attempt` (session) | id, userId, deviceId, problemId, problemVersion, language, sectionId, sessionType (interview, practice), startedAt, finishedAt, activeMs, unlocked, assisted, hintsUsed{phase: maxLevel}, finalResults, score, notes, feedbackId, assistantAvailable |
| `Run` | attemptId, seq, target, compileError, aiEditsActive, suites[{name, tests[{name, status, ms, detail}]}], durationMs |
| `Edit` | attemptId, editId, replyId, path, op, hunks, beforeHash, afterHash, undoneAt |
| `Event` | attemptId, seq, tMs, type (run, chat, aiEdit, aiEditUndo, hint, phasePassed, skip, resetFile, resetProblem, pause, resume, finish, healthWarning), data |

Section types and their parts:

| Section | Runner | Grader | Session UI mode | Scoring rule |
|---|---|---|---|---|
| Coding round (three phases) | code-tests via the language runner | tests, plus reviewer-model feedback | editor + files + output + brief + chat | correctness 70 weighted per phase, time 10, rhythm 20 (today's formula, weights from content) |
| DSA quick questions | code-tests, single phase | tests | editor + brief, no file tree | correctness 80, time 20 |
| System design | none | rubric shown offline, reviewer model online | canvas or text answer + brief + timer | rubric points by reviewer, self-marked offline |
| Low-level design | code-tests optional | rubric + tests | editor + brief | 50 tests, 50 rubric |
| AI/ML questions | none or notebook-free text | multiple choice + reviewer | question list | correct answers, no time |

Mapping today's artefacts: `problem.json` becomes `Problem` plus one `ProblemVariant` (java); `kit/` becomes the Java language package's `testkit/`; `Sessions.score` becomes the coding section's `scoring/CodingRoundScore.java` with weights read from phases; `walkthrough.md` is a `resultPart` of the coding section; `TestRunner` output becomes the JSON-lines run protocol of section 4.

Interfaces a new package implements (Java, in the engine):

```java
interface LanguagePackage {
    LanguageDefinition definition();          // id, display name, toolchain policy, versions
    Runner runner();                          // compile(workDir) / run(workDir, target, limits) -> RunResult stream
    TestKit testKit();                        // files to copy into work/, result-line format = run protocol
    CalibrationBenchmark benchmark();         // program + reference ms on the reference machine
    List<HealthCheck> healthChecks();         // toolchain present, version, compile+run sample
    EditorSupport editor();                   // CodeMirror language id, formatter if any
}

interface SectionPackage {
    SectionDefinition definition();           // id, name, runnerKind, graderKind, uiMode
    ContentSchema schema();                   // validates a problem folder of this section
    Grader grader();                          // RunResult + content -> PhaseResults
    ScoringRule scoring();                    // PhaseResults + Timeline -> Score
    List<ResultPart> resultParts();           // what the result page shows, in order
    List<HealthCheck> healthChecks();
    PromptTemplates prompts();                // system, interview-mode, helpful-mode, write-code, feedback
    Set<AssistantChannel> channels();         // ASK, WRITE_CODE; a section without code has ASK only
    boolean supportsHints();                  // hints/phaseN.md required by the content schema when true
}
```

The UI side mirrors this with a `SectionUiPackage` (React) that provides the session view and result-page parts, registered in a manifest the shell reads at start.

### 2.7 Industry mode: proctored assessments (later phase)

You want companies to use the app the way they use CoderPad or HackerRank, with malpractice detection that a browser cannot do. The design below is a later phase (section 10, Phase 8) and nothing in Phases 1 to 7 depends on it, but the shell is built so it can be added as a package: a `proctor` module in the Rust shell and an `assessment` section package. Facts below come from the official documentation checked on 2026-10-14; see the notes in the "Not verified" column.

**What a desktop app can observe that a browser cannot**

| Signal | Windows | macOS | Not verified or weak points |
|---|---|---|---|
| Other windows on screen, including overlays | `EnumWindows` plus extended styles (`WS_EX_TOPMOST`, `WS_EX_LAYERED`, `WS_EX_TRANSPARENT`, `WS_EX_NOACTIVATE`), and `GetWindowDisplayAffinity` to find windows excluded from capture, readable from any process for layered windows ([Microsoft Learn](https://learn.microsoft.com/en-us/windows/win32/api/winuser/nf-winuser-getwindowdisplayaffinity)) | `CGWindowListCopyWindowInfo` returns bounds, layer and owner for on-screen windows; window titles need Screen Recording permission on 10.15 and later | Affinity probe fails on non-layered windows; macOS title gating is from an Apple forum answer, not a doc page |
| Hidden "second screen" | `GetSystemMetrics(SM_CMONITORS)` and `EnumDisplayMonitors`, which also lists mirror-driver pseudo monitors | `NSScreen.screens` and screen-parameter change notifications | Mirroring to a TV or AirPlay shows as one display on macOS |
| Background apps and remote-control tools | Process list via `CreateToolhelp32Snapshot`, no privilege needed; `SM_REMOTESESSION` and `SM_REMOTECONTROL` for RDP and shadowing ([Microsoft Learn](https://learn.microsoft.com/en-us/windows/win32/termserv/detecting-the-terminal-services-environment)) | `NSWorkspace.runningApplications` for apps; daemons such as `screensharingd` or VNC servers need a full process list | Evasion tools disguise process names, so match by code signature and publisher, not name |
| Virtual machine | CPUID hypervisor bit and `Win32_ComputerSystem.HypervisorPresent` and `Model` | SMBIOS model and `sysctl` strings | Hyper-V, WSL2 and virtualization-based security set the hypervisor bit on a normal laptop, so treat it as a warning, not a verdict |
| Focus loss, lock, sleep | Tauri window focus and blur events; session lock and suspend through the `windows` crate (session notifications and power broadcast messages) | Tauri window events; `NSWorkspace` notifications through `objc2` | Reliable; the exact crate APIs are verified in the Phase 9 research step |
| Screen sharing in progress | Indirect: capture-excluded windows and known process names | No public API found; indirect only | Weak on macOS |
| Own window protected from capture | Tauri's window content-protection setting, which on Windows maps to `WDA_EXCLUDEFROMCAPTURE` on Windows 10 2004 and later (Microsoft documents the flag; Electron documents the same mapping at [electronjs.org](https://www.electronjs.org/docs/latest/api/browser-window); the Tauri method name is verified in research) | `sharingType = .none`, but ScreenCaptureKit-based tools still capture it | Protects the question text from screenshots on Windows; weaker on macOS |

**Scope.** Proctoring applies only to Interview and assessment sessions; Practice sessions never load the `proctor` module (section 2.8).

**Detection design.** Screenshots are blind to capture-excluded overlays by construction, which is exactly how the current "invisible" assistant tools work. Detection therefore runs from the enumeration side: every few seconds the proctor collects the window list with styles and affinity, the display count, the process list with signatures, the remote-session flags and the focus state, computes a risk score from weighted rules, and records events into the session timeline (`proctorWarning`, `proctorViolation`). Candidates see what is being checked before they consent. Companies see a per-session integrity report with the raw events, never a silent verdict. A second physical device is out of software reach; webcam snapshots are a separate decision with heavier privacy weight and are not planned.

**Build notes.** Because the shell is Rust, the calls above are made directly: `desktop/src-tauri/src/proctor/windows.rs` through the `windows` crate (window enumeration, display affinity, monitors, processes, session flags) and `desktop/src-tauri/src/proctor/macos.rs` through `objc2` and `core-graphics` bindings, with one `proctor` Tauri command exposing a sampled snapshot to the UI. No second toolchain is needed. Apple's Automatic Assessment Configuration framework offers a real exam lockdown mode but needs an entitlement granted through Apple's request form ([Apple docs](https://developer.apple.com/documentation/automaticassessmentconfiguration)); plan for it, do not depend on it. Safe Exam Browser's Windows code is MPL-2.0 and shows a working prohibited-process and display-limit design worth reading.

**Legal.** Candidates are not employees, so India's DPDP Act requires notice and free, specific, informed consent before any monitoring, and the GDPR treats consent in recruitment as weak and expects a lawful basis, transparency and a data protection impact assessment. This mode needs a lawyer's review before the first company customer. Not legal advice.

**Business shape.** Companies buy seats and assessments, not packs; the backend gains `organizations`, `assessments`, `invites` and `integrity_reports`, and the website gains a company dashboard. Pricing and economics are out of scope for this plan and should be revisited after Phase 3 with real demand.

### 2.8 Session types, assistant channels, hints and editor assistance

Added 2026-10-14 after your request for a Hello Interview style assistant with "Ask" and "Write code", hints per phase, and a practice mode. Decisions you made: Write code is allowed in both session types, and an Interview session in which AI edits were used is scored as assisted (revised 2026-10-14 after the first hands-on run, ADR 0006: AI use no longer changes the label, solved or the best score; the reviewer grades control of the AI, and the Interview-mode rules became the point-and-explain rules below); Practice mode is the non-interview mode with hints and no proctoring; edits apply immediately with one-click undo; the problem source stays intact and the editor works on a copy that reset recreates; hints are authored in three levels with an online "explain further"; a Write code call costs three message-equivalents; the assistant works on the whole project unless the user names a file or a method; the reviewer sees every edit event.

#### Session types

| | Interview | Practice |
|---|---|---|
| Purpose | Rehearse the real round | Learn the problem |
| Clock | Enforced limit from `problem.json`, time score applies | Stopwatch by default; the user may switch the limit on; no time score |
| Hints | None, and the Hint button is hidden, unless the section allows hints in Interview (`hintsInInterview()`, ADR 0012: `dsa-quick`); then three authored levels, no explain | Three authored levels per phase, plus "explain this hint further" online where the section has Ask |
| Ask channel | Point-and-explain rules (below) | Helpful by default, point-and-explain rules selectable |
| Write code channel | Allowed for a piece the candidate names after stating the approach; every applied edit is recorded and the count is shown on the result page | Allowed |
| Score | Full formula; shown as "Interview" | Correctness and phase results only; shown as "Practice" |
| Counts as solved or best score | Every Interview session that passes, with or without AI edits (ADR 0006) | Never |
| Proctoring (Phase 9) | Applies, in assessment sessions | Never |
| Offline | Yes, without either assistant channel | Yes, with authored hints; no AI channels |

The session type is chosen on the start screen and recorded in `sessions.session_type`; it cannot change after the clock starts.

**Clock and pauses (decided 2026-10-14).** The clock pauses whenever the app is closed, in both session types, and resumes when the app starts again. The engine records `lastAliveAt` every 15 seconds while it runs; at start it closes the gap for every unfinished session with a `pause` event at `lastAliveAt` and a `resume` event at the current time. A clean shutdown writes the `pause` directly. `active_ms` is wall time minus paused gaps, and the Interview limit and time score use `active_ms`. Accepted trade-off: closing the app stops an Interview clock; the result page lists every pause, so a reviewer sees it.

#### Assistant channels

- **Ask**: today's chat. Receives the brief, the files, the history and the message; replies in text.
- **Write code**: the same context plus the section's `prompts/write-code.md`, which asks the model to return edits in the protocol below and a short note for the chat. If the user names a file or a method in the message, the prompt instructs the model to confine edits to it; otherwise every editable file is in scope. Read-only files are listed as "do not edit" and the engine enforces it regardless.
- **Explain this hint**: Practice only, online only, and only in sections with the Ask channel; the revealed hint text plus files and history, Ask rules, one message-equivalent.

#### Edit protocol

The backend streams JSON lines; the engine applies them one at a time and the UI shows each as it lands.

```
{"t":"note","text":"I will index holds by account so holdsFor stops scanning the whole list."}
{"t":"edit","id":"e1","path":"HoldBook.java","op":"replace","base":"sha256:…","search":"<exact existing text>","replace":"<new text>"}
{"t":"edit","id":"e2","path":"AccountIndex.java","op":"create","content":"<file text>"}
{"t":"done","edits":2}
```

Engine rules, each with a test in `engine/session/edits/`:

| Rule | Behaviour |
|---|---|
| Allowed paths | Editable source files inside `work/`, and new files with the variant's source extension; never read-only files, never `data/`, never outside `work/`. A disallowed edit is shown as "not allowed" and skipped |
| Base version | `base` must equal the hash of the file the model was shown; a mismatch (the user typed meanwhile) shows "your file changed; the suggestion is shown but not applied" |
| Exact match | `search` must occur exactly once; zero or many matches leave the edit unapplied and shown as a suggestion |
| Size and encoding | Files over 200 KB and non-UTF-8 files are not edited; line endings follow the file's existing style |
| Atomic per edit | A snapshot of the file is written to `sessions/<id>/edits/<editId>.before` before the write; failure restores it |
| Events | `aiEdit {id, path, op, hunks, beforeHash, afterHash, replyId}` on apply; `aiEditUndo {id}` on undo; every `run` event carries `aiEditsActive`, the number of applied edits not yet undone |
| Undo | One click per edit, and "undo everything from this reply"; undo restores the snapshot only if the current content still matches `afterHash`, otherwise it shows a diff and asks |
| Reset | "Reset file" and "Reset problem" copy fresh files from the pack source into `work/`; the source is never modified; both are events |
| History | Past edits enter the prompt history as one line each ("assistant edited HoldBook.java, 2 hunks"), never as full files |
| Finished, offline, caps, signed out | The Write code control is disabled with the reason shown before typing |
| Injection | File contents are data inside the prompt; instructions come only from the section's prompt files; a file containing instruction-like text changes nothing |

`assisted` is true when any `run` event or the finish has `aiEditsActive > 0`. It is stored and the result page shows the number of AI edits applied and undone, but it changes neither the label, nor solved, nor the best score (ADR 0006). The reviewer prompt receives the edit events and scores "Control of the AI" from what was accepted, undone and reverted; that score, visible on the result page, is the product's answer to leaning on the assistant.

#### Point-and-explain rules (Interview mode)

Decided 2026-10-14. The assistant is a tool the candidate drives, not an answer key. In an Interview session, on the Ask channel:

1. Asked to fix, solve or make the tests pass: name the exact place (file, method, line) and describe the logic that has to change, in words. No solution code.
2. Asked to explain any code, test, failure or concept: explain fully, using the candidate's own code as the example.
3. Asked a specific question ("is the visited check in `fewestHops` right?"): answer honestly, including a plain yes or no.
4. Never volunteer work on other phases, other bugs, or optimisations the candidate did not ask about.

On the Write code channel: write only the piece the candidate named (a method, a class, a test, a data structure), and only after the candidate stated the approach in the message or earlier in the chat; a bare "fix it" or "make it pass" gets the reply of rule 1 and no edit. Practice uses Helpful rules by default with these rules selectable. The rules live in `prompts/interview-mode.md` and `prompts/write-code.md`, and each has a recorded-transcript test.

**Availability (Phase 1ab).** Before the backend exists, both channels, hint explanations and the written review run through the developer-mode CLI route of section 2.5 on the developer's own machine; end-user builds get the backend route in Phase 1b. The panel shows "Assistant unavailable" with the reason (developer mode off, no CLI, signed out) instead of disabled controls.

#### Hints

Each problem has `hints/phase<N>.md` with three sections: level 1 points at where to look, level 2 names the approach, level 3 gives concrete steps without code. The content tool refuses a problem that lacks any level for any phase. Hints are pack content, so they work offline. A `hint {phase, level}` event records each reveal. In Practice mode the Hint button reveals the next level; "explain further" sends the revealed text to the Ask channel online. Interview mode shows no hints unless the section allows them (ADR 0012; the DSA section does, and has no assistant, so no explain), and hints never deduct from the score.

#### Editor assistance

You asked for a simple editor with autocomplete, suggestions and IntelliSense through plugins. Two tiers:

| Tier | What | How | Ships |
|---|---|---|---|
| 1, built in | Keyword and project-symbol completion, snippets, bracket matching, inline compiler diagnostics after every run or save, go-to-symbol inside the project | CodeMirror 6 `@codemirror/autocomplete` and `@codemirror/lint`; the engine extracts symbols from the editable files with the JDK's own `com.sun.source` tree API for Java and simple parsers for other languages, and maps `javac` diagnostics to file and line | Phase 1a |
| 2, language server plugins | Real IntelliSense: type-aware completion, hover, signatures, diagnostics as you type | CodeMirror's official `@codemirror/lsp-client` ([github.com/codemirror/lsp-client](https://github.com/codemirror/lsp-client), published July 2025) connected over the engine's local socket to a language server the language package declares in `editor/lsp.json`: Eclipse JDT Language Server for Java, which needs Java 21 or newer and runs on the bundled runtime ([github.com/eclipse-jdtls/eclipse.jdt.ls](https://github.com/eclipse-jdtls/eclipse.jdt.ls), checked 2026-10-14; its download size was not verified, expect tens of MB and about 1 GB of heap), pyright or jedi for Python, typescript-language-server, gopls | Java in Phase 2 behind a flag, each later language with its package |

Language servers are downloaded on demand like toolchains, hash-verified, started by the engine only while a session of that language is open, and declared as a warning-level health check ("IntelliSense unavailable, basic completion active"). The editor itself stays simple: no formatter beyond `gofmt`, no refactoring menus.

## 3. Tech stack

### 3.1 Recommendations

| Layer | Recommendation | Why for you | Alternative and when to pick it |
|---|---|---|---|
| Backend | Spring Boot 3.x, Java 21, Gradle, Flyway, Spring Security (JWT for app, session cookie for website), springdoc OpenAPI | Your strongest skill; one runtime for backend, engine and content tools | Quarkus if cold start or memory on the VPS becomes a problem; unlikely at this scale |
| Database | PostgreSQL 16 in Docker on the same VPS, nightly `pg_dump` to object storage | Zero monthly cost beyond the VPS; you know SQL | Supabase managed Postgres in its Mumbai region, or DigitalOcean Managed Postgres in Bangalore at $15.15 a month, when you want backups and failover handled, roughly from 1,000 paying users (verified 2026-10-14, section 3.4) |
| Web front end | React 19 + TypeScript + Vite + MUI, TanStack Query, React Router | You know React, Redux and MUI; Vite keeps the build simple | Next.js if SEO landing pages and server rendering matter more than simplicity; pick it only if you will host it on Cloudflare Pages or Vercel and accept a second runtime |
| Desktop shell | **Tauri 2, Rust**, with the React and TypeScript UI rendered in the system webview (WebView2 on Windows, WKWebView on macOS); Tauri bundler, updater plugin, shell plugin to launch the engine | Shell under 1 MB so the installer is mostly the Java runtime; native checks for the later proctoring mode are plain Rust; one toolchain for shell and native work. Costs you a Rust learning sprint in Phase 1a (section 3.3, `docs/learning/phase-1a.md`) | Electron if the Rust sprint has not produced a shell that launches the engine within three weeks: the UI and the engine are unchanged, only the shell folder and the build config differ |
| Local engine | Java 21, JDK HTTP server, Jackson, sqlite-jdbc, shaded jar run by the bundled runtime | Keeps the prototype's logic and your skills; the same engine can run in containers later | Rewriting the engine in Rust would remove one runtime but discard the prototype's logic and lose container reuse |
| Local store | SQLite via `sqlite-jdbc` | Queryable, single file, well understood | H2 if the native library per platform causes packaging trouble |
| Editor | CodeMirror 6 with `@codemirror/autocomplete` and `@codemirror/lint` built in; `@codemirror/lsp-client` (official, July 2025) for optional language-server IntelliSense, with servers downloaded on demand per language | Small, offline-capable, and the language server is a plugin the language package declares, not a shell change (section 2.8) | Monaco if you want VS Code's look; it brings TypeScript IntelliSense only and 25 MB of bundle, and its Java, Python and Go support is colouring only |
| Object storage and CDN | Cloudflare R2 behind Cloudflare's CDN | Free egress, 10 GB free, served from 22 Indian cities, and Cloudflare is registered for Indian GST so the invoice is clean; installers and toolchains are large, which is exactly what free egress is for | E2E Object Storage (Indian, Delhi NCR) if you want packs stored in India; it has a ₹625 a month floor so it costs more for a few GB (section 3.4) |
| Auth | Own implementation: email magic link + Google OAuth on the website, device authorization flow for the app, rotating refresh tokens | Few users, simple needs, no per-MAU bill; the flows are small and documented in 2.3 | Clerk or Supabase Auth if you want hosted UI and MFA quickly; pick at a few thousand users or if you add SSO for companies |
| Hosting | One **E2E Networks** cloud VM (Indian company, INR billing, GST invoice, hourly billing, snapshots), Docker Compose: backend, Postgres, Caddy for TLS. Smallest fit: E1LC-2.6GB, 2 vCPU, 6 GB, ₹894 a month plus 18% GST | Indian-billed as you asked, cheaper than Hetzner's equivalent after tax, one box is enough until well past 10,000 users for this workload | Hostinger India KVM 2 (2 vCPU, 8 GB) at ₹799 intro and ₹1,199 renewal if you prefer a hosting-company VPS; Oracle Cloud Always Free in Mumbai for staging at ₹0; Hetzner only if you want EU hosting. Section 3.4 has the comparison |
| Website hosting | Cloudflare Pages | Free, global, previews per PR, Indian edge cities | Vercel if you choose Next.js |
| CI | GitHub Actions; Windows runner for the Windows build, macOS runner for the macOS build and notarization | Free minutes cover a solo cadence; macOS runners cost per minute and are budgeted in section 9 | A self-hosted runner on your own Windows laptop for signing with a hardware token, if cloud signing is not used |
| Error tracking | Sentry (backend and engine through the Java SDK, the Rust shell through the Sentry Rust SDK, web and the webview UI through the browser SDK) | Free developer tier is enough at the start | GlitchTip self-hosted on the VPS when Sentry's free quota runs out |
| Analytics | PostHog cloud free tier for product events; Plausible or Umami for the website | Privacy-friendly, generous free tier | Self-hosted Umami on the VPS |
| Email | Zoho ZeptoMail (Indian, INR): ₹150 per 10,000 emails, first 10,000 free | Transactional only; low volume; cheapest option found and billed in INR | Brevo (free 300 a day, Starter ₹625 a month) or Amazon SES at $0.10 per 1,000 |
| Monitoring and analytics | Sentry Developer tier (free), Uptime Kuma and Umami self-hosted on the same VM | Zero cost, nothing leaves your box except crash reports | GlitchTip self-hosted when Sentry's free quota runs out; PostHog cloud free tier for product funnels |
| Domain | `.com` at Cloudflare Registrar or Porkbun at about $11 a year; `.in` at Porkbun $7.83 or BigRock ₹549 first year and ₹899 renewal | Indian registrars charge ₹1,479 to ₹1,599 on `.com` renewals, so buy `.com` abroad and `.in` in India if you want INR billing | BigRock for both if you must have an INR invoice for everything |
| Payments | Provider-agnostic `PaymentPort`; first adapter chosen in section 8 | You decide the provider after analysis; the code does not care | |

**Kept, rewritten or dropped from the prototype**

| Prototype piece | Decision |
|---|---|
| `Server.java` | Rewritten into the engine's modules; routing style, JDK HTTP server, process control and the sessions model are kept |
| `app.js`, `index.html`, `app.css` | Dropped; replaced by React components with the same three views and the same visual design |
| Test kit (`kit/`) | Kept as the Java language package's test kit; output changes to the run protocol |
| Problem folder format | Kept and extended: brief, walkthrough, grading notes unchanged; starter, reference, naive move under `variants/java/` |
| Score formula | Kept as the coding section's scoring rule; weights move into content |
| Hand-written JSON parser, CodeMirror 5, custom markdown renderer | Dropped |

### 3.2 Desktop packaging (decision 10)

**Bundled runtime.** Eclipse Temurin 21 (LTS until at least December 2029, GPLv2 with Classpath Exception, free to bundle; source: [adoptium.net/docs/faq](https://adoptium.net/docs/faq/), checked 2026-10-14) trimmed with `jlink --add-modules java.base,jdk.compiler,jdk.httpserver,java.management,jdk.management,java.sql,jdk.crypto.ec --strip-debug --no-header-files --no-man-pages --compress zip-6`. A `java.base` plus `jdk.compiler` image measured 42.3 MiB compressed on Windows x64 with Oracle JDK 21.0.2 (local measurement, not a published figure); the extra modules above add a few MB (est.). The runtime runs the engine and compiles the user's Java, which satisfies decision 10. The engine starts as `java -jar engine.jar` with its libraries on the class path: putting sqlite-jdbc or Jackson on the module path would also require `java.sql.rowset` and `java.xml` (measured in Phase 1a research). Do not bundle Oracle JDK: its no-fee licence forbids bundling with a product that charges fees ([oracle.com no-fee licence](https://www.oracle.com/downloads/licenses/no-fee-license.html), checked 2026-10-14). Ship the Temurin licence and notice files inside the app.

**Tool: the Tauri 2 bundler**, not jpackage. jpackage produces installers but does not notarize for macOS and needs WiX on Windows ([jpackage man page](https://docs.oracle.com/en/java/javase/21/docs/specs/man/jpackage.html), checked 2026-10-14), and it gives no auto-update. Tauri's bundler produces NSIS and MSI installers on Windows and `.app` and `.dmg` on macOS, signs and notarizes macOS builds from environment variables, signs Windows builds through a configurable `signCommand`, and the updater plugin reads a signed static JSON file per platform from any static host such as R2 ([Tauri updater plugin](https://v2.tauri.app/plugin/updater/), [Tauri macOS signing](https://v2.tauri.app/distribute/sign/macos/), [Tauri Windows signing](https://v2.tauri.app/distribute/sign/windows/), checked 2026-10-14). The Java runtime ships as a bundle resource folder and the shell starts the engine with the shell plugin; Tauri's `externalBin` sidecar mechanism expects single binaries with a target-triple suffix ([Tauri sidecar docs](https://v2.tauri.app/develop/sidecar/)), which does not fit a JDK image, so it is not used.

| Item | Windows | macOS |
|---|---|---|
| Installer format | NSIS `.exe` (MSI optional), per-user install by default, "all users" option; installs the WebView2 runtime if the machine lacks it, which Windows 11 and most Windows 10 machines do not ([Microsoft WebView2 distribution](https://learn.microsoft.com/en-us/microsoft-edge/webview2/concepts/distribution), checked 2026-10-14) | `.dmg` for first install, `.app.tar.gz` for updates, per-arch (arm64 and x64) |
| Expected download size (est.) | Shell under 1 MB plus about 42 MB of compressed Java runtime plus the free problem and UI assets: **about 45 to 55 MB**; the WebView2 bootstrapper adds about 2 MB where needed | Same components: **about 45 to 55 MB** per arch |
| Code signing | An **individual validation (IV) or OV code signing certificate**. Azure Artifact Signing is not available to individuals or organisations in India ([Microsoft Learn quickstart](https://learn.microsoft.com/en-us/azure/artifact-signing/quickstart), checked 2026-10-14). SSL.com lists IV at $129 per year with no business registration, OV at $129, EV at $349 ([ssl.com IV](https://www.ssl.com/products/software-integrity/code-signing/iv/), checked 2026-10-14). Since June 2023 the key must live in hardware or a cloud HSM: either a USB token (SSL.com YubiKey add-on $379) used on a self-hosted runner, or the CA's cloud signing service used from GitHub Actions (SSL.com eSigner; its subscription price was not verified) | Apple Developer Program, $99 per year ([developer.apple.com](https://developer.apple.com/support/purchase-activation/), checked 2026-10-14; INR amount shown only inside the Apple Developer app, enrolment from India only through that app). Developer ID Application certificate, Hardened Runtime, `notarytool`, stapling, all driven by the Tauri bundler from `APPLE_CERTIFICATE`, `APPLE_CERTIFICATE_PASSWORD`, `APPLE_ID`, `APPLE_PASSWORD` and `APPLE_TEAM_ID` (or an App Store Connect API key) on a GitHub macOS runner |
| Yearly cost (est.) | $129 certificate plus cloud signing or a one-time token | $99 |
| False positives | SmartScreen warns until the publisher identity has reputation; EV no longer bypasses it, and reputation accrues per certificate and per file ([Microsoft Learn SmartScreen](https://learn.microsoft.com/en-us/windows/apps/package-and-deploy/smartscreen-reputation), checked 2026-10-14). Reduce it by signing every build including betas with the same certificate from day one, keeping the certificate for years, submitting the installer to Microsoft's malware analysis portal and to major antivirus vendors' false-positive forms before launch, and avoiding packers | Notarized apps open without warnings; antivirus is rarely an issue on macOS |
| Antivirus on the bundled runtime | Some scanners flag `java.exe` in user profiles. Install per-machine under Program Files when possible and sign every executable inside the runtime folder | Not an issue |
| Self-update | Tauri updater plugin reading `https://cdn.intervue.si/releases/stable/latest.json`; every update artefact is signed with the updater key generated by `tauri signer generate`, whose private half lives only in CI secrets as `TAURI_SIGNING_PRIVATE_KEY`; checks at launch and every 4 hours; passive install mode on Windows | Same feed with the macOS entries; the app is signed and notarized, which the updater requires |

**Steps you will follow (the "steps and navigation" you asked for) are written as a runbook in `docs/runbooks/release-app.md`:** enrol in the Apple Developer Program from the Apple Developer app; create the Developer ID Application certificate in Xcode or the portal and export it as a `.p12`; create an app-specific password; buy the IV certificate, complete identity validation, choose cloud signing or token; generate the updater key pair with `tauri signer generate`; put `APPLE_CERTIFICATE`, `APPLE_CERTIFICATE_PASSWORD`, `APPLE_ID`, `APPLE_PASSWORD`, `APPLE_TEAM_ID`, `TAURI_SIGNING_PRIVATE_KEY` and the Windows cloud-signing credentials used by `signCommand` in GitHub Environment secrets; run the release workflow on a tag; download both installers from the `beta` channel and test on clean machines; promote to `stable` by copying `latest.json` and the artefacts.

**CI minutes (est.).** A macOS build with a Rust compile and notarization takes about 20 to 30 minutes with the Rust build cache warm; at $0.062 per minute ([GitHub Actions pricing](https://docs.github.com/en/billing/reference/actions-minute-multipliers), checked 2026-10-14) a release costs about $1.50 to $2 in macOS minutes beyond the free allowance, and the 2,000 free minutes a month of the Free plan cover Linux and Windows CI at a solo cadence.

### 3.3 Electron or Tauri: the trade-off and the choice

You know JavaScript, React, Redux and MUI and no Rust. The shell in this design is deliberately thin: it opens a window, starts the engine, holds the device secret, runs the updater, and later hosts the native proctoring checks. Everything else is React and Java in both options. That is what keeps the choice cheap to reverse. You chose **Tauri 2 in Rust** on 2026-10-14 after reading the comparison below; the UI stays React and TypeScript in both options, so "Rust" here means the shell only.

| | Electron | Tauri 2 |
|---|---|---|
| New language | None. Main process is TypeScript | Rust for the shell: ownership and borrowing, `Result` and `?` error handling, Cargo, `async` with Tokio for commands, traits for plugins |
| New concepts | Main versus renderer, preload bridge and `contextIsolation`, IPC, `safeStorage`, child processes, electron-builder config, electron-updater feed | Commands and IPC, the v2 capabilities and permissions model, sidecars for the engine (`externalBin`), updater with mandatory signatures, plugin system, per-OS webview quirks (WebView2 on Windows, WKWebView on macOS) |
| Time to a working signed shell with the engine running (est. for you) | 1 to 2 weeks part-time, because the concepts map onto what you know and electron-builder covers signing, notarization and updates in one file | 3 to 6 weeks part-time: 2 to 3 weeks of Rust basics before the shell feels natural, then the Tauri specifics; signing and notarization are documented but spread across the CLI config and environment variables |
| Later native work (proctoring, 2.7) | N-API addons in C++ or Rust via napi-rs, a second toolchain either way | Rust directly with the `windows` and `objc2` crates, no extra toolchain |
| Download size (verified runtime sizes, section 3.2) | About 90 to 100 MB of shell in every installer and every full update | About 0.6 MB of shell; the installer is roughly the bundled Java runtime alone, 50 to 60 MB |
| Memory at idle (est.) | 150 to 250 MB for the shell plus the engine | 30 to 60 MB plus the engine |
| Ecosystem and answers when stuck | Very large; most desktop apps built with web tech use it | Smaller but active; v2 docs are good |
| Risk to the 3-month target | Low | Medium: Rust learning time comes straight out of Phase 1 |

**What the Tauri choice means for the plan.** Phase 1a starts with a two to three week Rust and Tauri learning sprint, written out in `docs/learning/phase-1a.md`, and its estimate grows from 5 to 6 weeks to 7 to 9. The installer shrinks to about 50 MB, which matters on Indian connections. The proctoring work in Phase 9 becomes plain Rust instead of a second native toolchain. The shell stays free of logic, so if the sprint has not produced a shell that launches the engine within three weeks, the fallback is Electron with the same UI and engine; the only lost work is the shell folder and the build config.

Learning route: "The Rust Programming Language" (the official book) chapters 1 to 10, then the Tauri 2 quick start, then port the engine launcher as the first command. Budget about 40 hours before you are productive in the shell. The tutor prompt for this sprint is in `docs/learning/phase-1a.md`.

### 3.4 Indian and cost-effective infrastructure

You asked for Indian alternatives and the lowest sensible cost. Prices below were read on the vendors' pages on 2026-10-14; INR prices exclude 18% GST unless stated. Where a provider's GST treatment was not on its page it says so.

**Compute for the backend**

| Provider | Plan | Price as listed | Data centre | Billing and GST | Fit |
|---|---|---|---|---|---|
| E2E Networks (Indian) | E1LC-2.6GB, 2 vCPU, 6 GB, shared | ₹894 a month or ₹4.48 an hour | Delhi NCR, Chennai | INR is the authoritative currency, GST invoices issued ([e2enetworks.com/pricing](https://www.e2enetworks.com/pricing)) | **Recommended for production.** Real cloud (API, snapshots, hourly), Indian company, 6 GB is comfortable for Spring Boot plus Postgres |
| E2E Networks | C3.8GB, 4 vCPU, 8 GB, CPU-intensive | ₹3,422 a month | same | same | The step up at a few thousand MAU |
| Hostinger India | KVM 2, 2 vCPU, 8 GB, 100 GB, 8 TB | ₹799 a month introductory, renews ₹1,199 | India | INR; GST not stated on the page | Cheapest 8 GB found; a hosting VPS rather than a cloud, intro price needs an upfront term |
| MilesWeb (Indian) | SM-L1, 1 vCPU, 4 GB, 50 GB | ₹549 introductory, renews ₹949 | India | GSTIN shown, taxes extra | Cheapest absolute; 1 vCPU is tight for Postgres plus the API |
| Utho (Indian) | Basic 1, 2 vCPU, 4 GB, 80 GB NVMe | ₹4,305 a quarter (₹1,435 a month) | Mumbai, Noida | INR; GST not stated | Dearer than E2E for less RAM |
| Oracle Cloud Always Free | Arm 2 OCPU, 12 GB total, 10 TB egress a month | ₹0 | Mumbai or Hyderabad as home region | None | **Staging and CI helpers.** Capacity is often exhausted and idle instances can be reclaimed, so not for production ([Oracle free tier docs](https://docs.oracle.com/en-us/iaas/Content/FreeTier/freetier_topic-Always_Free_Resources.htm)) |
| DigitalOcean | Basic 2 GiB | $12 a month | Bangalore | USD; adds 18% GST unless you give a GSTIN ([DigitalOcean India tax page](https://docs.digitalocean.com/platform/billing/taxes/ind/)) | Fine, but a foreign card payment every month |
| AWS Lightsail or EC2 | Lightsail 512 MB, or EC2 t4g.small 2 vCPU, 2 GiB | $5 a month; about $8.18 a month plus storage | Mumbai | USD list prices; GST handling by AWS's Indian entity not verified here | Only if you want the AWS ecosystem |
| Hetzner (for comparison) | CPX12, 1 vCPU, 2 GB | €11.99 a month | Germany or Finland | EUR, no Indian GST on the invoice | What the first draft of this plan assumed; E2E gives three times the RAM for a similar price after tax, in India |

**Object storage and CDN**

| Provider | Price | Egress | Indian presence | Verdict |
|---|---|---|---|---|
| Cloudflare R2 plus Cloudflare CDN | $0.015 per GB-month, 10 GB free | Free | Served from 22 Indian cities; Cloudflare charges Indian GST and accepts a GST ID ([Cloudflare network](https://www.cloudflare.com/network/), [Cloudflare sales tax](https://developers.cloudflare.com/billing/understand/sales-tax/)) | **Keep.** Installers are the only large transfer and egress is free |
| E2E Object Storage (Indian) | ₹625 a month flat for 0 to 250 GB, then ₹2.5 per GB | Not charged separately; CDN overage ₹3 per GB | Delhi NCR | Pick only if you want packs stored in India; costs more than R2 for a few GB because of the floor |
| Utho object storage (Indian) | From ₹500 a month including 100 GB, ₹4 per GB above; the API lists lower tiers, so confirm in the console | No egress fees in India | Noida | Same verdict as E2E |
| AWS S3 Mumbai | $0.025 per GB-month | $0.1093 per GB | Mumbai | Egress kills it for installers |
| Backblaze B2, Wasabi | $6.95 and $7.99 per TB | Free or capped | No Indian region | Not for this |

**Email, domains, database**

| Need | Recommendation | Price | Alternative |
|---|---|---|---|
| Transactional email | Zoho ZeptoMail (Indian) | ₹150 per credit of 10,000 emails, first credit free, taxes extra ([Zoho CPaaS pricing](https://www.zoho.com/cpaas/pricing.html)) | Brevo free 300 a day; Amazon SES $0.10 per 1,000 |
| Domains | `.com` at Cloudflare Registrar or Porkbun, about $11; `.in` at Porkbun $7.83 or BigRock ₹549 then ₹899 | Indian registrars' `.com` renewals are ₹1,479 to ₹1,599 ([BigRock](https://www.bigrock.in/), [GoDaddy India](https://www.godaddy.com/en-in/tlds/com-domain)) | BigRock for everything if an INR invoice matters more than ₹500 a year |
| Managed Postgres later | Supabase, Mumbai region | Free 500 MB; Pro $25 a month | DigitalOcean Managed Postgres Bangalore $15.15 a month; AWS RDS Mumbai db.t4g.micro about $15.3 a month plus storage; Neon has no Indian region |

**GST on foreign cloud bills.** Foreign providers of online services to an unregistered Indian individual are meant to register and charge IGST themselves; DigitalOcean adds 18% unless you provide a GSTIN, Cloudflare is registered for Indian GST, and Hetzner's invoices carry no VAT and say nothing about GST (vendor pages checked 2026-10-14; the CBIC OIDAR flyer is from 2017). Preferring India-billed providers keeps the accountant's job simple, which is the practical reason behind the E2E and ZeptoMail picks beyond price. Not tax advice.

**Resulting monthly bill at launch (est., GST included where applicable, assumed ₹85 per USD for the foreign items):** E2E E1LC-2.6GB ₹1,055; R2, Pages, Umami, Uptime Kuma ₹0; ZeptoMail ₹0 for the first 10,000 emails; domain about ₹80; signing and developer accounts about ₹1,615 (Apple $99 and the IV certificate $129 a year); macOS CI minutes about ₹340: **about ₹3,100 a month**, of which hosting is a third and signing half. Section 9.2b has the table at 100, 1,000 and 10,000 MAU.

## 4. Multi-language runtime

### 4.1 Language definition contract

Every language package provides `language.json` and a `LanguagePackage` implementation. The contract:

| Field | Meaning |
|---|---|
| `id`, `displayName`, `spiVersion` | Identity; the engine refuses a package whose `spiVersion` it does not know |
| `toolchain.policy` | `bundled`, `downloaded` or `user-installed` |
| `toolchain.minVersion`, `toolchain.recommended` | Version check bounds |
| `toolchain.downloads[os-arch]` | `{url, sha256, sizeBytes, installDir, binaries:{compiler, runtime}}` for the downloaded policy |
| `toolchain.detect` | Commands and version regexes for a user-installed toolchain |
| `sample` | Program that must compile and run with expected output, used by the health check and the encoding check |
| `compile` | Command template with placeholders `{work}`, `{out}`, `{files}`, and the policy for warnings |
| `run` | Command template for the test runner and for the demo entry point; environment variables to set; the flags that force UTF-8 |
| `isolation` | Per OS: how the child is limited (job object, prlimit), and the kill rule |
| `perTestTimeout` | Mechanism name: `child-per-class-restart-on-timeout` (Java), `child-per-test` (Python), `worker-terminate` (Node), `child-per-test` (Go) |
| `limits` | Memory cap default, output cap, open-files cap |
| `layout` | Starter project shape: flat files, data folder name, test file naming, entry point naming |
| `readOnlyPolicy` | Which files the engine restores before every run: everything listed in `variant.json.readOnly`, plus the test kit files |
| `calibration` | Benchmark program, repetitions, reference milliseconds on the reference machine, clamp range |
| `editor` | CodeMirror language package id, indentation, comment syntax, formatter command if any, symbol extractor for project completion, and `lsp` (optional): server name, download per OS with hash and size, start command, heap cap, the Java minimum where the server runs on the bundled runtime |

### 4.2 The four languages

| | Java | Python | JavaScript/TypeScript | Go |
|---|---|---|---|---|
| Toolchain and version | Temurin 21 LTS bundled (LTS to at least Dec 2029 per [adoptium.net/support](https://adoptium.net/support/), checked 2026-10-14) | CPython 3.14 (bugfix line; 3.12 and 3.13 are security-only as of 2026-10-14 per [python.org/downloads](https://www.python.org/downloads/)); minimum 3.12 | Node 24 LTS now (EOL 2028-04-30); Node 26 becomes Active LTS on 2026-10-28 ([nodejs/Release schedule](https://raw.githubusercontent.com/nodejs/Release/main/schedule.json), checked 2026-10-14); TypeScript through Node's built-in type stripping, stable since 24.12 ([nodejs.org/api/typescript](https://nodejs.org/api/typescript.html)) | Go 1.27 (1.27.2 released 2026-10-08, [go.dev/dl](https://go.dev/dl/)); minimum: the two most recent minors, as Go supports |
| How obtained | Inside the installer | Downloaded on demand: python-build-standalone `install_only_stripped`, 22.2 MiB Windows x64, 25.4 MiB macOS arm64 for 3.14.8 ([astral-sh releases](https://github.com/astral-sh/python-build-standalone/releases), checked 2026-10-14); PSF licence plus permissive dependencies; or user-installed | Downloaded on demand: official binaries, 35.9 MiB Windows x64 zip, 26.1 MiB macOS arm64 tar.xz for 24.21.0 ([nodejs.org/dist](https://nodejs.org/dist/v24.21.0/)); MIT; or user-installed | Downloaded on demand: official archive, 75.3 MiB Windows amd64, 65.0 MiB macOS arm64 for 1.27.2; BSD-3; or user-installed |
| Version check | `java -version` of the bundled runtime; hash of `lib/modules` | `python -c "import sys; print(sys.version)"` | `node --version`; type stripping probe runs a one-line `.ts` | `go version` |
| Sample program | Reads a UTF-8 file, prints a checksum | Same | Same, written in TypeScript | Same |
| Compile | `javac -encoding UTF-8 -nowarn -d {out} {kitFiles} {files}` | None; byte-compile `python -m compileall -q {work}` to catch syntax errors before running | None; `node --check {entry}` for each file | `go vet ./... && go build -o {out}/tests ./...` with `GOFLAGS=-mod=readonly GOPROXY=off GOTOOLCHAIN=local GOCACHE={appdata}/go-cache GOFLAGS` set; standard library resolves offline ([go.dev/ref/mod](https://go.dev/ref/mod)) |
| Run tests | `java -Xmx{cap} -Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -cp {out} kit.TestRunner --results {out}/results.jsonl {classes}` | `python -X utf8 -I kit/runner.py --results {out}/results.jsonl {modules}` | `node --experimental-default-type=module kit/runner.ts --results {out}/results.jsonl {files}` | `{out}/tests --results {out}/results.jsonl` where the kit is a tiny runner compiled with the tests, not `go test` |
| Process isolation | Child process tree, killed with `ProcessHandle.descendants()`; Windows Job Object with memory and kill-on-close via a small JNA call; `prlimit` on Linux | Same wrapper | Same wrapper | Same wrapper |
| Per-test timeout | One child JVM per test class; a TIMEOUT ends that JVM and the rest continue in a fresh one (built in the prototype today) | Threads cannot be killed in CPython ([threading docs](https://docs.python.org/3/library/threading.html)), so the kit runs each test in a child process with `subprocess.run(timeout=)`; inside the child `faulthandler.dump_traceback_later(timeout, exit=True)` is the backstop ([faulthandler docs](https://docs.python.org/3/library/faulthandler.html)) | Each test runs in a `worker_threads` Worker with `resourceLimits`; `worker.terminate()` stops even a synchronous loop, and the kit falls back to killing a child process if termination does not complete in 2 s ([worker_threads docs](https://nodejs.org/api/worker_threads.html)) | The `testing` package has no per-test timeout and `-timeout` panics the whole binary ([cmd/go docs](https://pkg.go.dev/cmd/go)), so the kit's runner forks itself per test (`tests --one Name`) with a wall-clock kill in the parent |
| Resource limits | `-Xmx` quarter of RAM, clamped 512 MB to 4 GB; job object memory on Windows | Job object or `RLIMIT_AS`; `sys.setrecursionlimit` left default | `resourceLimits.maxOldGenerationSizeMb`; `--max-old-space-size` for the parent | `GOMEMLIMIT` plus job object |
| Starter layout | Flat default-package `.java` files, `data/` folder, `PhaseNTests.java`, `Main.java`, `Datasets.java` | Flat modules, `data/`, `test_phase_n.py`, `main.py`, `datasets.py` | Flat `.ts` modules with explicit `.ts` import extensions (type stripping requires them), `data/`, `phase_n.test.ts`, `main.ts`, `datasets.ts`; no enums, namespaces or decorators in content | One package `main`, `data/`, `phase_n_test.go`, `main.go`, `datasets.go`, `go.mod` with `go 1.27` and no requires |
| Read-only policy | `variant.json.readOnly` plus kit files, restored before every run | Same | Same | Same, plus `go.mod` |
| Calibration benchmark | Sort 2 million longs, 1 million HashMap inserts and lookups, 3 repetitions, median; reference ms recorded on the reference machine | Same algorithm in pure Python (list sort, dict) | Same in TypeScript | Same in Go |
| Known pitfalls | JIT warm-up makes the first test slower; the kit runs a 200 ms warm-up loop before the first test. Antivirus scanning of freshly written class files on Windows adds latency | GIL irrelevant for single-threaded tests; timing noise is higher, so limits need more headroom (6x reference instead of 5x); C-backed operations are fast while pure loops are 20 to 60 times slower than Java ([Benchmarks Game](https://benchmarksgame-team.pages.debian.net/benchmarksgame/fastest/python.html), checked 2026-10-14), so content must be designed per language, not translated literally | Single thread means a busy test blocks timers inside the same worker, which is why the kill lives in the parent; `.tsx` and `enum` are unsupported by type stripping; module resolution needs explicit extensions | Build step 1 to 3 s cold; the engine pre-warms `GOCACHE` by building the sample at health-check time; `go vet` runs by default and content must pass it; module mode offline works because standard library imports never touch the network |
| Editor | `@codemirror/lang-java`; symbols from the JDK `com.sun.source` tree API; optional Eclipse JDT Language Server plugin (needs Java 21 or newer, runs on the bundled runtime) | `@codemirror/lang-python`; symbols from the `ast` module through the toolchain; optional pyright or jedi-language-server plugin | `@codemirror/lang-javascript` with `typescript: true`; symbols from a regex pass; optional typescript-language-server plugin | `@codemirror/lang-go`; symbols from `go/parser` through the toolchain; optional gopls plugin |
| Formatting | None at launch (google-java-format is a separate 2 MB jar; add later behind a flag) | None at launch (`ruff format` is separate) | None at launch (Prettier is separate) | `gofmt`, which ships with Go |

All four CodeMirror packages are official, MIT, and under 70 KB each ([codemirror.net language support](https://codemirror.net/), checked 2026-10-14).

### 4.3 One run protocol

The engine talks to every language the same way.

Input to the runner: `{workDir, outDir, target: "phase:2" | "all" | "main", variant.json, limits:{wallMs, memoryMb, outputBytes}, calibrationFactor}`.

Output: the user's program output streams to the UI as plain text, exactly as today; the kit writes structured results to `{out}/results.jsonl`, one JSON object per line, so user prints can never corrupt parsing:

```
{"protocol":1,"t":"start","runner":"java-kit/2.0","language":"java","factor":1.3}
{"t":"suite","name":"Phase2Tests","tests":11}
{"t":"test","suite":"Phase2Tests","name":"singleHold","status":"pass","ms":11}
{"t":"test","suite":"Phase2Tests","name":"sampleFile","status":"fail","ms":4,"detail":"expected <255049> but was <255048>","at":"Phase2Tests.sampleFile(Phase2Tests.java:118)"}
{"t":"test","suite":"Phase3Tests","name":"oneBusyAccount","status":"timeout","ms":7800,"limitMs":7800}
{"t":"summary","passed":12,"failed":1,"timeouts":1,"compileError":false,"wallMs":9120}
```

`status` is one of `pass`, `fail`, `timeout`, `error` (the harness failed, not the test). The engine's parser in `shared-java/run-protocol` is the only reader. A kit may print the same lines to stdout for humans; the engine ignores stdout for results.

### 4.4 Calibration

Limits in content are written as milliseconds on the reference machine (`timeoutMs` in tests, `timeoutRefMs` in `problem.json`). At run time: `effectiveMs = refMs * factor(language, machine)`, where `factor = clamp(medianBenchmarkMs / referenceBenchmarkMs, 1.0, 4.0)`. The factor is per language because interpreter speed and JIT behaviour differ, and it is stored in `settings` with the toolchain version and a timestamp; it is recomputed when the toolchain changes, when the user asks, and once a month. The kit receives the factor as an argument and applies it to every per-test limit, and the engine applies it to the whole-run cap. Content rule: a limit is at least 5 times the reference solution's time (6 times for Python), and the naive solution must take at least 3 times the limit, so a factor between 1 and 4 cannot flip either outcome. The verify tool checks both ratios and refuses to publish a problem that breaks them.

### 4.5 Editor support

CodeMirror 6 in `packages/editor`: one `EditorState` per open file, the language extension chosen from the variant's language, read-only decorations for protected files, bracket matching, auto-indent, search, a theme matching the current design, and AI-edit decorations that highlight changed lines for a few seconds with an Undo affordance. Formatting is a per-language command declared in `language.json`; only Go has one at launch.

Assistance comes in two tiers (section 2.8). Tier 1 is built in and offline: keyword completion from the language package, project-symbol completion from a symbol list the engine refreshes on every save, snippets, and inline diagnostics mapped from the compiler's output after each save or run through `@codemirror/lint`. Tier 2 is a language server the language package declares in `editor/lsp.json`; the engine downloads it on demand, starts it while a session of that language is open, and bridges it to `@codemirror/lsp-client` over the local socket. If the server is absent or fails, the editor silently stays on tier 1 and the Doctor shows a warning.

### 4.6 Checklist for adding a fifth language (for example Rust or C#)

Files created, all under `languages/<lang>/`:

1. `language.json` with every field in 4.1.
2. `runner/` Gradle subproject implementing `LanguagePackage`, registered through `META-INF/services/si.intervue.engine.spi.LanguagePackage`.
3. `testkit/` emitting the run protocol, with the per-test timeout mechanism that works in that runtime.
4. `calibration/` benchmark plus `reference.json` produced on the reference machine.
5. `setup/` guides per OS and `messages.json` for health check texts.
6. `editor/` pnpm package exporting the CodeMirror extension, a symbol extractor, and `lsp.json` if a language server is offered.
7. `sample/` program for the health check.

Files changed outside the folder: `content/problems/*/variants/<lang>/` for every problem in the catalogue, and `languages` arrays in `pack.json` files. Nothing else.

Files that must not change: anything under `engine/`, `desktop/`, `web/`, `backend/`, `packages/ui`, `sections/`, `shared-java/`. CI has a job that fails a PR labelled `language` if it touches any of those paths.

Release gate: every problem in the current catalogue passes the three-way verification in the new language on Windows and macOS CI runners, the health check passes on both, the native reviewer has signed off, and the language is enabled by flag for testers before it is enabled for everyone.

## 5. Repository layout, architecture and independent delivery

### 5.1 Repository strategy

| Option | For a solo developer now | Pain point that argues for it |
|---|---|---|
| **One monorepo** (recommended) | One checkout, one issue tracker, cross-cutting changes in one PR, shared types without publishing packages | None until a second team owns a part |
| Separate repos for backend, website, app, content | Clear ownership, independent access control | Only when content authors who must not see app code arrive, or a second developer owns one part full time |
| Monorepo for code, separate repo for content | Content can have its own release cycle and contributors without touching code | Worth doing when the first external content author joins |

Recommendation: one monorepo now, with `content/` kept at the top level and self-contained so it can be split out later with `git subtree split --prefix=content` or `git filter-repo --subdirectory-filter content`, both of which preserve history. Split content when the first outside author joins; split the backend only if a second engineer owns it.

### 5.2 Folder structure

Tooling: Gradle (Kotlin DSL) multi-project for all Java; pnpm workspace for all TypeScript. Every folder below says what lives there and who owns changes.

```
intervue/
├── README.md                         # how to run everything locally                              owner: you
├── CONTRIBUTING.md                   # the rules in 5.11                                           owner: you
├── settings.gradle.kts               # auto-includes backend, engine, shared-java/*, languages/*/runner, sections/*/grader
├── build.gradle.kts                  # shared Java toolchain (21), formatting, test config
├── package.json / pnpm-workspace.yaml# workspaces: web, desktop, packages/*, sections/*/ui, languages/*/editor
├── .github/
│   ├── workflows/                    # one path-filtered workflow per deployable (5.8)             owner: you
│   └── CODEOWNERS                    # empty until a second contributor
├── backend/                          # Spring Boot 3, Java 21, hexagonal                           owner: backend
│   ├── src/main/java/si/intervue/backend/
│   │   ├── identity/                 # users, devices, device-auth flow, tokens, sign-out-all
│   │   ├── catalog/                  # packs, problems metadata, versions, free problem pointer
│   │   ├── entitlements/             # grants, terms, device slots, redeem codes, status rules
│   │   ├── payments/                 # PaymentPort + adapters (razorpay/, paddle/, ...), webhooks, promo codes
│   │   ├── delivery/                 # signed URLs, content keys, manifest signing verification
│   │   ├── sync/                     # push, pull, feedback requests, cursors
│   │   ├── ai/                       # gateway: limits, PromptBuilder, ModelClient port, anthropic/ adapter, usage log
│   │   ├── admin/                    # admin endpoints: codes, grants, flags, users (role-gated)
│   │   ├── flags/                    # flag definitions, evaluation, GET /v1/flags
│   │   └── shared/                   # config, security filters, errors, clock, ids
│   ├── src/main/resources/
│   │   ├── db/migration/             # Flyway SQL, forward-only
│   │   └── application*.yml          # profiles: local, staging, prod (secrets from env only)
│   ├── src/test/                     # unit + Testcontainers Postgres integration tests
│   └── Dockerfile
├── engine/                           # Java 21 local core, replaces server/Server.java              owner: app
│   ├── src/main/java/si/intervue/engine/
│   │   ├── api/                      # local HTTP on 127.0.0.1 (JDK httpserver), per-launch token, SSE
│   │   ├── session/                  # create (interview or practice), files, runs, unlock, clock with pause, hints, finish, report
│   │   ├── session/edits/            # AI edit protocol parser, EditApplier (allowed paths, base hash, exact match), snapshots, undo, reset
│   │   ├── content/                  # ContentStore port; FreeProblemSource, PackSource; problem reader
│   │   ├── packs/                    # download, verify signature+hash, decrypt, cache, lock, delete
│   │   ├── runners/                  # RunnerRegistry, ProcessControl (kill tree, limits), protocol parser, calibration
│   │   ├── languages/                # LanguageRegistry via ServiceLoader; LanguagePackage SPI
│   │   ├── sections/                 # SectionRegistry via ServiceLoader; SectionPackage SPI
│   │   ├── sync/                     # outbox, push/pull client, retry policy
│   │   ├── ai/                       # AiClient to backend (SSE); CliModelClient for dev only
│   │   ├── health/                   # HealthCheck SPI, collector, scheduler, redacted report
│   │   ├── store/                    # SQLite repositories, schema migrations
│   │   └── shared/                   # json (Jackson), paths, os detection, ids
│   ├── src/test/                     # unit tests + runner tests against real toolchains
│   └── build.gradle.kts              # produces engine.jar (shadow) consumed by desktop/
├── desktop/                          # Tauri 2 shell: Rust core, React UI in the system webview         owner: app
│   ├── src-tauri/src/                # main.rs, commands/ (engine port+token, OS facts, open-external), engine launcher, keychain, proctor/ (later)
│   ├── src-tauri/capabilities/       # permission sets for the shell, dialog and updater plugins
│   ├── src-tauri/tauri.conf.json     # bundle targets nsis and dmg, resources (jlink runtime, free problem), updater endpoints
│   ├── src/                          # renderer: mounts @intervue/ui App; nothing else
│   ├── resources/                    # jlink runtime (built, git-ignored), free problem, icons, licences
│   └── scripts/                      # jlink.ts, latest.json generator, smoke test
├── web/                              # React + Vite + TypeScript + MUI website                       owner: web
│   ├── src/pages/                    # home, pricing, setup, download, activate, account, store, admin
│   ├── src/features/                 # account/, store/, devices/, redeem/, admin/ (hooks + components)
│   ├── src/api/                      # client generated from packages/api-types
│   ├── src/content/                  # marketing copy, FAQ, setup guides rendered from docs/setup-guides
│   └── public/                       # static assets
├── packages/                         # shared TypeScript                                            owner: app + web
│   ├── ui/                           # Dashboard, Room, Result, Health, Store cards; theme; markdown
│   ├── editor/                       # CodeMirror 6 setup, language mode registry, read-only and AI-edit decorations, autocomplete and lint sources, lsp/ bridge to @codemirror/lsp-client
│   ├── api-types/                    # OpenAPI-generated types for backend v1; versioned with the API
│   └── local-engine-client/          # typed client for the engine's local API
├── shared-java/                      # Java libraries used by engine, backend and content tools      owner: you
│   ├── problem-format/               # Problem, Variant, Pack models; JSON Schemas; reader; validators
│   ├── run-protocol/                 # JSON-lines model and parser for runner output
│   ├── sync-contracts/               # DTOs for push/pull, versioned
│   └── crypto/                       # Ed25519 sign/verify, AES-GCM pack encryption, key wrapping
├── languages/                        # one folder per language package                              owner: per language
│   ├── java/
│   │   ├── language.json             # id, name, versions, toolchain policy (bundled), extensions
│   │   ├── runner/                   # Gradle subproject: JavaLanguagePackage (compile, run, limits)
│   │   ├── testkit/                  # kit/Test.java, Check.java, TestRunner.java (JSON-lines output)
│   │   ├── calibration/              # Bench.java + reference.json (ms on the reference machine)
│   │   ├── setup/                    # guide-windows.md, guide-macos.md, guide-linux.md, messages.json
│   │   └── editor/                   # pnpm package: CodeMirror mode binding, snippets, symbol extractor, lsp.json (Eclipse JDT LS, downloaded on demand)
│   ├── python/                       # same five folders (Phase 4)
│   ├── javascript/                   # same (Phase 5), TypeScript via Node type stripping
│   └── go/                           # same (Phase 6)
├── sections/                         # one folder per section package                               owner: per section
│   └── coding-round/
│       ├── section.json              # id, name, runnerKind, graderKind, uiMode, version
│       ├── schema/                   # problem.json schema for this section
│       ├── grader/                   # Gradle subproject: TestsGrader
│       ├── scoring/                  # Gradle subproject: CodingRoundScore (today's formula)
│       ├── prompts/                  # system.md, interview-mode.md, helpful-mode.md, write-code.md, hint-explain.md, feedback.md
│       ├── ui/                       # pnpm package: session view, result parts, timeline strip
│       └── health/                   # checks this section needs (e.g. sessions folder writable)
├── content/                          # problems and packs; own release cycle                        owner: content
│   ├── problems/<problem-id>/        # everything one problem needs (format in section 7)
│   ├── packs/<pack-id>/pack.json     # the only shared file a problem change may touch
│   ├── free/free.json                # which problem ships in the installer
│   └── tools/                        # Gradle subproject: validate, verify (3-way, all languages), build, sign, publish
├── infra/
│   ├── compose/                      # docker-compose.prod.yml, docker-compose.staging.yml, Caddyfile
│   ├── scripts/                      # provision-vps.sh, backup-db.sh, restore-db.sh, create-buckets.sh
│   └── env/                          # .env.example per environment, never real secrets
├── scripts/                          # dev-up.sh, seed-local.sh, release-*.sh, new-problem.sh, new-language.sh
└── docs/
    ├── adr/                          # architecture decision records, numbered
    ├── contracts/                    # api-v1.yaml, pack-format.md, run-protocol.md, sync-protocol.md, language-contract.md, section-contract.md
    ├── setup-guides/                 # generated from languages/*/setup at build
    ├── runbooks/                     # deploy, rollback, rotate keys, restore backup, handle refund
    └── plan/                         # this document and the planning prompt
```

**Mapping of every file in the prototype**

| Prototype file | New location | Note |
|---|---|---|
| `server/Server.java` routing, static files, http helpers | `engine/api/` | Keeps JDK httpserver; adds a per-launch token instead of the `X-Intervue` header |
| `Server.Sessions` (create, files, unlock, notes, finish, report) | `engine/session/` | `score()` moves to `sections/coding-round/scoring/` |
| `Server.Runner` (compile, run, restore read-only, sections parser) | `languages/java/runner/` and `engine/runners/` | Parser becomes `shared-java/run-protocol` |
| `Server.exec` | `engine/runners/ProcessControl.java` | Adds memory limit flag and per-language kill rules |
| `Server.Claude` (CLI detect, ask) | `engine/ai/CliModelClient.java` | Dev-only adapter |
| `Server.Chat` (chat and feedback prompts) | `backend/ai/PromptBuilder.java` plus `sections/coding-round/prompts/*.md` | Prompt text becomes data; the Write code and hint prompts are new files beside them |
| `Server.Json` | dropped | Jackson in engine and backend |
| `web/index.html`, `web/app.js`, `web/app.css` | `packages/ui/` as React components | Three views become routes; vanilla JS dropped |
| `app.js` `renderMd` | dropped | `markdown-it` with `DOMPurify` in `packages/ui/markdown` |
| `web/vendor/codemirror*.js`, `clike.js`, addons | dropped | CodeMirror 6 in `packages/editor` |
| `web/vendor/*.woff2` | `packages/ui/assets/fonts/` | Same fonts |
| `kit/kit/Test.java`, `Check.java`, `TestRunner.java` | `languages/java/testkit/kit/` | TestRunner emits JSON lines to a results file |
| `problems/<id>/problem.json` | `content/problems/<id>/problem.json` and `variants/java/variant.json` | Split into neutral and Java parts |
| `problems/<id>/brief/*.md`, `walkthrough.md` | same names under `content/problems/<id>/` | Unchanged |
| `problems/<id>/starter/*.java`, `starter/data/*` | `content/problems/<id>/variants/java/starter/` | Data files stay inside the variant |
| `problems/<id>/solution/*.java` | `content/problems/<id>/variants/java/reference/` | |
| `problems/<id>/solution/naive/*.java` | `content/problems/<id>/variants/java/naive/` | |
| `problems/<id>/solution/NOTES.md` | `content/problems/<id>/grading/NOTES.md` | Language-neutral |
| `tools/Verify.java` | `content/tools/` | Extended to every language and to the pack build |
| `run.bat`, `run.sh` | dropped | Installer and `scripts/dev-up.sh` |
| `README.md` | `README.md` and `docs/` | |
| `PLANNING-PROMPT.md` | `docs/plan/` | |
| `sessions/` | user data directory, never in the repo | `%APPDATA%/Intervue` and `~/Library/Application Support/Intervue` |

### 5.3 Application architecture and dependency direction

| Part | Style | Layers (dependencies point downward only) |
|---|---|---|
| Backend | Hexagonal | `api` (controllers, DTOs) -> `application` (use cases per module) -> `domain` (entities, rules) <- `adapters` (JPA repositories, payment providers, Anthropic client, R2 signer). Ports are interfaces in `domain` or `application`; adapters implement them |
| Engine | Hexagonal, small | `api` -> `session`, `packs`, `sync`, `health` (application) -> `content`, `runners`, `store` ports <- `languages/*`, `sections/*`, SQLite, backend client adapters |
| Desktop shell | Thin host | `src-tauri` (Rust: process lifecycle, engine launcher, keychain, updater, OS) -> Tauri commands and events -> `src` renderer mounts the UI package. No business logic |
| UI packages | Component model with feature folders | `pages` -> `features` -> `components` -> `api clients`. State: TanStack Query for server and engine state, React context for session-local state; Redux Toolkit is acceptable if you prefer it, keep one of the two |
| Website | Same as UI packages | |
| Content tools | Pipeline scripts | validate -> verify -> build -> sign -> publish |

Behind interfaces, so each can change alone: `ContentStore` (free folder, encrypted pack, plain folder in dev), `Runner` per language, `PackRepository` and `PackCrypto`, `ModelClient` (Anthropic, CLI, fake for tests), `PaymentPort` (one adapter per provider), `SyncTransport`.

### 5.4 Boundaries and contracts

| Contract | Where it is written | Versioning | Consumers |
|---|---|---|---|
| Backend REST API | `docs/contracts/api-v1.yaml` (OpenAPI), generated types in `packages/api-types` | URL version `v1`; additive changes only within v1; `v2` for breaking changes, both served for 12 months | web, engine |
| Auth and pack delivery | section of `api-v1.yaml` plus `docs/contracts/pack-format.md` | `formatVersion` integer in manifest | engine, content tools |
| Sync protocol | `shared-java/sync-contracts`, `docs/contracts/sync-protocol.md` | `protocolVersion` in every push; server accepts N and N-1 | engine, backend |
| Pack format | `docs/contracts/pack-format.md`, schemas in `shared-java/problem-format` | `formatVersion`; app declares the range it reads | content tools, engine, backend catalog |
| Problem format | JSON Schemas in `shared-java/problem-format` and `sections/*/schema` | `version` field per problem; section schema version | content tools, engine, backend prompt builder |
| Language contract | `docs/contracts/language-contract.md`, `LanguagePackage` SPI | SPI version constant; packages declare the SPI version they implement | engine |
| Section contract | `docs/contracts/section-contract.md`, `SectionPackage` and `SectionUiPackage` | SPI version constant | engine, UI |
| Run protocol | `docs/contracts/run-protocol.md`, `shared-java/run-protocol` | `protocol` field on the first line | every test kit, engine |

Rule: a change inside one part never requires a change in another part unless a contract changes. Contract changes are made additively first (new optional fields), consumers upgraded, then the old shape removed after one compatibility window (12 months for the API, two app minor versions for the SPIs). Every contract change gets an ADR.

### 5.5 Content isolation rule

Every problem lives in `content/problems/<id>/` with everything it needs. Editing, adding or removing a problem may touch that folder and the `pack.json` files that list it, and nothing else. The same rule holds between packs (a pack change touches `content/packs/<pack-id>/` only), between variants of one problem (a Python port touches `variants/python/` only), and between sections (a section change touches `sections/<id>/` and problems of that section only).

Pack manifest, `content/packs/fintech-core/pack.json`:

```json
{
  "formatVersion": 1,
  "id": "fintech-core",
  "version": 3,
  "title": "Fintech core",
  "description": "Seven scenario problems from payments and risk.",
  "sections": ["coding-round"],
  "languages": ["java"],
  "minAppVersion": "1.0.0",
  "problems": [
    { "id": "corridor-router",   "version": 2 },
    { "id": "merchant-suggest",  "version": 1 }
  ],
  "terms": ["month", "quarter"]
}
```

Hashes and the signature are added by the build tool into the built pack, not in the source manifest, so a problem edit never forces a manual manifest edit beyond the version bump.

CI validation (`.github/workflows/content.yml`, job `isolation`):

```bash
changed=$(git diff --name-only "$BASE_SHA" "$HEAD_SHA" -- content/)
for f in $changed; do
  case "$f" in
    content/problems/*/*) problem=$(echo "$f" | cut -d/ -f3); problems="$problems $problem" ;;
    content/packs/*/pack.json) ;;                       # allowed shared file
    content/free/free.json) ;;                           # allowed pointer
    content/tools/*) tools=1 ;;                          # tooling PRs are labelled "content-tools"
    *) echo "FORBIDDEN: $f"; exit 1 ;;
  esac
done
if [ -n "$tools" ] && [ -n "$problems" ]; then echo "Tooling and content in one PR"; exit 1; fi
echo "problems touched: $(echo $problems | tr ' ' '\n' | sort -u | wc -l)"
```

A second job runs `content/tools validate` (schema, required files, every language listed in a pack has a verified variant for every problem) and `content/tools verify` on the problems touched.

### 5.6 Git workflow

- Trunk-based: `main` is always releasable; short-lived branches `feat/<area>-<slug>`, `fix/<area>-<slug>`, `content/<problem-id>`.
- Conventional Commits with the area as scope: `feat(engine): per-class test JVM`, `content(hold-exposure): python variant`.
- Pull requests for everything, squash-merged; `main` protected: CI must pass, linear history, no force push.
- Tags per deployable: `backend-v1.4.0`, `app-v1.2.1`, `web-2026.10.14`, `pack-fintech-core-v3`, `lang-python-v1.0.0`, `section-system-design-v1.0.0`.
- Skip until a second contributor: required reviewers, CODEOWNERS, signed commits, release branches. Keep: CI gates, tags, changelogs generated from commits.
- Commit policy (`CONTRIBUTING.md`, enforced by `.gitignore`): commit code, content, contracts, the plan, the process layer, prompts, learning files, ADRs, runbooks, retros and the status board. Keep local the working documents a phase writes and discards (`docs/plans/`, `docs/research/`, raw plan research, interview notes), build output, user data and every secret.

### 5.7 Versioning and compatibility

Semantic versioning per deployable. The app sends `X-Intervue-App: <version>` and `X-Intervue-Formats: pack=1;sync=1` on every call; the backend answers `426 Upgrade Required` with a reason when the version is below the supported floor.

| App version | Backend API | Pack format | Sync protocol | Language SPI | Section SPI |
|---|---|---|---|---|---|
| 1.0.x (launch) | v1 | 1 | 1 | 1 | 1 |
| 1.1.x (sync + deferred feedback) | v1 | 1 | 1 | 1 | 1 |
| 1.2.x (Python) | v1 | 1 (adds `languages` entries, additive) | 1 | 1 | 1 |
| 2.0.x (new section SPI) | v1 | 2 | 2 | 1 | 2 |

Backend keeps old apps working by serving v1 unchanged, by never removing fields, and by publishing packs in every format version still within the support floor (the build tool can emit `formatVersion` 1 and 2 of the same pack). The app refuses a pack whose `formatVersion` is above its range with "This pack needs app version X; update" and offers the update; a pack below its range is upgraded in place by a reader that understands old versions.

### 5.8 Independent deployment

| Deployable | Trigger paths | CI jobs | Environments | Secrets | Rollback |
|---|---|---|---|---|---|
| Backend | `backend/**`, `shared-java/**` | build, unit, Testcontainers integration, Docker image, deploy to staging on merge, deploy to prod on tag | local (compose), staging (second stack on the VPS, own DB and bucket), prod | GitHub Environment secrets for registry and SSH; runtime secrets in `/etc/intervue/backend.env` on the VPS, root-only | Redeploy previous image tag (`scripts/release-backend.sh rollback`); Flyway migrations are additive so old images run against new schema |
| Website | `web/**`, `packages/**` | lint, typecheck, build, deploy preview per PR, deploy prod on merge | Cloudflare Pages preview and production | Pages token | Pages instant rollback to previous deployment |
| Desktop app | `desktop/**`, `engine/**`, `languages/**`, `sections/**`, `packages/**`, `shared-java/**` | Java tests, TS tests, `cargo fmt --check`, `cargo clippy`, `cargo test`, jlink runtime, `cargo tauri build` on the Windows and macOS runners with the Rust build cache, sign, notarize, sign update artefacts with the updater key, smoke-launch, upload artefacts and `latest.json` to R2 `releases/<channel>/` on tag | channels: `dev` (every merge), `beta` (tag `-beta`), `stable` (tag) | Windows cloud-signing credentials, Apple certificate and password, Apple ID or API key, `TAURI_SIGNING_PRIVATE_KEY`, R2 token | Point the channel's `latest.json` back at the previous version; the updater offers it as an update |
| Language packages | inside the app build | same as app, plus `languages/<lang>/calibration` run on CI reference runner | ship with the app; a flag can hide a language | none | with the app |
| Section packages | inside the app build | same | ship with the app; a flag can hide a section | none | with the app |
| Content packs | `content/**` | isolation, validate, verify (3-way, all declared languages), build, sign, publish to R2 `packs/<id>/<version>/`, update catalog on the backend via admin API | staging bucket on PR label `content-staging`, prod on tag | pack signing private key (content workflow only), R2 token, admin API token | Catalog pointer flips back to the previous version; packs are immutable per version |

### 5.9 Feature flags and staged rollout

Backend table `flags(key, kind, default, rules_json, updated_at)`; `GET /v1/flags` returns the evaluation for the caller (allowlist by user id, percentage by stable hash of user id, app version range, OS). The engine caches `{flags, fetchedAt}` in `settings`, applies them offline until a 7-day stale limit, then falls back to compiled-in defaults. The website reads the same endpoint. A language or section ships dark with `lang.python.enabled=false`; the package is in the installer but hidden. Rollout: allowlist of testers, 10%, 50%, 100%; when a flag has been at 100% for two releases, the flag and the dead branch are removed. Flags never gate security or entitlement checks.

### 5.10 Hotfix path

| Fix | Steps | Time to users (est.) |
|---|---|---|
| Content (a wrong test, a typo in a brief) | Branch, edit the problem folder, bump its version and the pack version, PR, content CI verifies, tag, publish; apps pick up the new pack version at next online start | 30 to 60 minutes of your time, users within a day |
| Backend | Branch, fix, test, PR, merge deploys to staging, smoke, tag deploys to prod | 1 to 2 hours; users immediately |
| App shell or engine | Branch, fix, PR, tag `app-vX.Y.Z`, CI builds and signs both OSes, publish to `stable` | 2 to 3 hours of CI and your time; users at next app start (updater checks on launch and every 4 hours) |
| Language runner | Same as app, because it ships inside the app; if the defect only affects one language, flag that language off from the backend within minutes while the fix builds | Flag: minutes; fix: as app |

### 5.11 Two worked examples

**Opening the system design section.** Folders touched: `sections/system-design/` (new: `section.json`, `schema/`, `grader/` with a `RubricGrader` that scores offline from a self-marked rubric and online from reviewer-model JSON, `scoring/`, `prompts/`, `ui/` with a split text-and-diagram answer view and result parts, `health/` empty), `content/problems/<new design problems>/` and `content/packs/system-design-starter/pack.json`, `docs/contracts/section-contract.md` only if the SPI needs a new optional field (it does not for this section: `runnerKind=none` already exists). Not touched: `engine/` (the registry discovers the package through `ServiceLoader`), `desktop/`, `web/` (the store renders packs from the catalog; the section tab comes from the UI registry's glob import of `sections/*/ui`), `backend/` (section id is a string in content and in flags). The backend's `PromptBuilder` reads `sections/system-design/prompts/feedback.md` from the pack content, so no code path is added.

**Adding Go.** Folders touched: `languages/go/` (new: `language.json`, `runner/` with compile `go vet` + `go build`, run `go test -json`-free custom kit runner, process control, `GOFLAGS=-mod=mod GOPROXY=off GOTOOLCHAIN=local`, `testkit/` with `kit.go` emitting the run protocol, `calibration/`, `setup/`, `editor/`), `content/problems/*/variants/go/` for every problem in the catalogue, `pack.json` files to add `"go"` to `languages`. Not touched: `engine/` (discovery by `ServiceLoader`), `desktop/` (toolchain download is data in `language.json`: URL, hash, size, install path), `web/`, `backend/`, `shared-java/`, `sections/`. The health check list for Go comes from the package itself and is collected by the shell's health framework.

If either example had required a shell edit, the design would be wrong. The two generic mechanisms that make this hold are `ServiceLoader` discovery in the engine and glob-based registration in the UI, both written once.

### 5.12 CONTRIBUTING rules

1. One part per pull request: backend, engine, desktop, web, one language, one section, or content. Mixed PRs are rejected by CI labels.
2. A change inside a part never edits another part. If you need to, you need a contract change: write the ADR first, make the change additive, then upgrade consumers.
3. Content PRs touch only `content/problems/<id>/` and `pack.json`; CI enforces it. Bump the problem version on every edit.
4. New language or section: copy `scripts/new-language.sh` or `scripts/new-section.sh` output, fill the five folders, never edit `engine/`, `desktop/` or `web/`.
5. Secrets never enter the repo. `.env.example` files list every variable with a fake value.
6. Every runner, grader and scoring rule has a test against a real toolchain in CI; a test kit change must keep the run protocol byte-compatible or bump the protocol version.
7. Prompts are data in `sections/*/prompts/`; code never embeds prompt text.
8. Database changes are additive Flyway migrations; never edit a shipped migration; the engine's SQLite migrations follow the same rule.
9. Conventional Commits with the area scope; squash merge; tags per deployable.
10. Before releasing the app: health check green on a clean Windows VM and a clean macOS machine, update from the previous version tested, free problem session completed end to end.
11. For AI coding agents: read `docs/contracts/` for the part you touch, run that part's tests, never rename contract fields, never change `formatVersion` or SPI versions without an ADR.

## 6. Setup guide and environment health check

### 6.1 First-run flow

Target time from download to first session: under 5 minutes on Windows and macOS with a normal connection, with no account.

| Step | Windows | macOS | Linux (Phase 6, unsigned) |
|---|---|---|---|
| Download | `Intervue-Setup-1.0.0.exe` from the website, about 50 MB (section 3.2); installs the WebView2 runtime first if the machine lacks it | `Intervue-1.0.0-arm64.dmg` or `-x64.dmg`, about 50 MB; the site picks by browser hint and lets the user switch | `.AppImage` and `.deb` |
| Installer warning | SmartScreen may show "Windows protected your PC" until the signing identity has reputation; the download page shows the exact dialog and the "More info, Run anyway" path, and the fix (reputation) is in section 3 | Gatekeeper opens a notarized app without a warning; an unnotarized build is blocked, so every release is notarized | "Untrusted application" dialogs vary; the guide shows `chmod +x` and `--appimage-extract` fallbacks |
| First launch | Doctor screen runs the blocking checks (6.4), 10 to 20 seconds; Java sample compiles and runs; calibration runs once (about 15 seconds) | Same; the first launch also asks nothing about permissions because no capture or microphone is used | Same |
| Free problems | The dashboard shows the two free problems as "Free, built in", with "Start the clock" enabled without sign-in | Same | Same |
| Sign-in | Optional banner: "Sign in to sync progress and unlock the assistant". Device flow opens the browser | Same | Same |
| Store | Pack cards link to the website; after purchase the app refreshes entitlements and downloads | Same | Same |

### 6.2 Setup guide per language

**Java (bundled).** No installation. What can still go wrong and the fix:

| Symptom | Cause | Fix shown |
|---|---|---|
| "Bundled runtime missing files" | Antivirus quarantined `runtime/bin/java.exe` or a DLL | Restore from quarantine, allowlist the Intervue folder, reinstall; a "Repair" button reinstalls the runtime from the installer cache |
| "Sample program did not compile" | Corporate policy blocks executables in `%LOCALAPPDATA%` (AppLocker, Smart App Control) | Install per-machine into Program Files via the installer's "all users" option, or ask IT to allowlist the signed publisher |
| Runs are slow and phase 3 times out on the reference | Laptop in power saver mode, or a very slow machine | Calibration factor is shown; "Re-run calibration while plugged in" |
| "Port in use" | Another local service on the engine port | The engine picks a free port automatically; the message appears only if every port in its range is taken |

**Python (Phase 4), written in full as the model for the others.**

Required: CPython 3.12 or newer, 64-bit, with `python3` runnable from the app. The app downloads a standalone build on demand by default (6.3). This guide covers the manual route when the download is blocked.

Windows:
1. Open https://www.python.org/downloads/windows/ and download the 64-bit installer for the latest 3.12 or 3.13 release.
2. Run it. Tick "Add python.exe to PATH". Click "Install Now".
3. Open a new Command Prompt and run `python --version`. Expect `Python 3.12.x` or `3.13.x`.
4. In Intervue, open Doctor, choose Python, click "Use system Python". The check compiles and runs the sample.
Common failures: the Microsoft Store stub opens when typing `python` (turn off "App execution aliases" for python.exe in Windows Settings); `python` is 32-bit (uninstall, install the 64-bit build); antivirus blocks `python.exe` on first run (allow it).

macOS:
1. Install from https://www.python.org/downloads/macos/ (universal2 installer), or `brew install python@3.12`.
2. In Terminal run `python3 --version`.
3. In Intervue, Doctor, Python, "Use system Python".
Common failures: the system `python3` is the Xcode Command Line Tools shim and prompts to install tools (accept, or install from python.org); Homebrew path not on PATH in the app's environment (Intervue searches `/opt/homebrew/bin` and `/usr/local/bin` explicitly).

Linux:
1. `sudo apt install python3 python3-venv` (Debian and Ubuntu) or `sudo dnf install python3` (Fedora).
2. `python3 --version`.
Common failures: distro Python older than 3.12 (use the app's downloaded build instead).

How to confirm it worked: Doctor shows "Python 3.12.6 at <path>, sample compiled and ran in 180 ms, calibration factor 1.3". Then start the free problem in Python.

JavaScript/TypeScript and Go guides follow the same template: required version, per-OS steps, confirmation line, common failures. They live in `languages/<lang>/setup/` and are generated into the website at build.

### 6.3 Bundled, downloaded or user-installed toolchains

| Language | Policy | Download size (est., verified figures in section 4) | Licence | Updates | Support load |
|---|---|---|---|---|---|
| Java | Bundled jlink runtime with `jdk.compiler` | 40 to 60 MB compressed inside the installer | GPLv2 with Classpath Exception (Temurin) | With the app | Lowest |
| Python | Downloaded by the app on demand from your CDN (python-build-standalone mirror), hash-verified; system Python as fallback | 25 to 45 MB per OS | PSF licence, redistributable | Independent of the app; `language.json` lists versions and hashes | Low |
| JavaScript/TypeScript | Downloaded on demand (official Node binaries mirrored); system Node as fallback | 25 to 50 MB per OS | MIT | Same | Low; TypeScript via Node's built-in type stripping, so no `tsc` |
| Go | Downloaded on demand (official archive mirrored); system Go as fallback | 70 to 100 MB per OS | BSD-3 | Same; `GOTOOLCHAIN=local` prevents auto-download | Medium: first build needs a warm cache, handled by a warm-up step |

Default policy: bundle only what the app itself needs (Java); download everything else on demand into the app's data folder, verified by SHA-256 from `language.json`, mirrored on your CDN so the links never rot; allow a user-installed toolchain as an explicit fallback with a version check. Acceptable friction: one in-app download before the first session in a new language, with size and progress shown.

### 6.4 Health check design

Two runs: the full Doctor before the first session and on demand from the dashboard; a fast subset (marked F) before every session, under 2 seconds. Checks are declared by language and section packages and collected by the shell's health framework; the shell contributes only the OS, disk, port and network checks.

| Check | Declared by | What it tests and how | Pass / fail | Message on fail | Fix | Blocking? | F |
|---|---|---|---|---|---|---|---|
| OS and architecture | shell | `os.name`, version, arch against the support matrix | Windows 10 2004+, macOS 13+, x64 or arm64 | "Intervue supports Windows 10 version 2004 and later and macOS 13 and later. This machine: ..." | Link to supported systems | Blocking | F |
| Bundled runtime intact | Java package | Files present, `java -version` output matches the embedded version, hash of `lib/modules` | Exact version match | "The bundled Java runtime is damaged (likely antivirus). Repair?" | One-click Repair reinstalls from installer cache | Blocking | F |
| Toolchain present and version | each language | Run `<tool> --version` for compiler and runtime, parse version, compare with `minToolchain` | Both found, version at or above minimum | "Python 3.9 found at ...; Intervue needs 3.12 or newer" | "Download recommended version" or guide link | Blocking for that language | F |
| Compile and run sample | each language | Compile and run `sample/` program through the real runner, measure ms | Exit 0, expected output, under 10 s | "The sample program failed: <first error line>" | Guide link to the matching failure row | Blocking for that language | |
| Calibration | each language | Run `calibration/` benchmark 3 times, median, divide by reference ms, clamp 1.0 to 4.0 | Factor computed | "This machine is 2.6x slower than our reference; time limits are scaled accordingly" | Re-run while plugged in | Warning | |
| Sessions folder writable and disk space | shell | Create and delete a temp file in the data folder; free space | Writable, over 1 GB free | "Intervue cannot write to <path>" or "Only 300 MB free" | Open folder settings; choose another location | Blocking | F |
| Local port free and not blocked | shell | Bind on 127.0.0.1 in the port range; the UI calls `GET /local/ping` | Bind and ping succeed | "A firewall or antivirus is blocking Intervue's local connection" | Allowlist instructions per product | Blocking | F |
| Encoding and line endings | each language | Compile and run a sample containing UTF-8 text and a CRLF file; compare output | Byte-exact expected output | "Text output is garbled on this machine" | Automatic: the runner forces UTF-8 flags; message only if that fails | Warning | |
| Packs present and valid | shell (pack client) | For each downloaded pack: signature, hash, decryptable with the cached key for the signed-in user, `formatVersion` in range, languages and sections available | All valid | "Pack <name> is damaged. Re-download?" or "needs app 1.3" | Re-download; update app | Blocking for that pack | F |
| App version supported | shell | Compare with `GET /v1/version-floor` (cached) | Above the floor | "This version is no longer supported. Update to continue syncing" | Update button | Warning offline, blocking for sync | F |
| Network, sign-in, entitlement freshness, last sync | shell | Reach backend, token valid, entitlements fetched within 24 h, outbox size | All fresh | "Offline: practice works; assistant and sync paused" | Retry; sign in | Warning | F |
| AI assistant reachable | shell | `GET /v1/ai/usage` succeeds and allowance remains | Reachable with allowance | "Assistant unavailable: <reason>. Sessions run without Ask and Write code" | Retry; view usage | Warning, shown before the clock starts | F |
| Language server for the chosen language | each language with `lsp.json` | Server files present and hash-verified, starts and answers `initialize` within 20 s | Started | "IntelliSense unavailable for Java; basic completion is active" | Download; retry | Warning | |
| Hints present for every phase of installed problems | coding-round section | Each problem has `hints/phase<N>.md` with three levels | All present | "Problem <name> has no hints for phase 2; pack needs an update" | Update pack | Warning (Practice only) | |
| Claude CLI route (developer mode only, hidden otherwise) | shell | CLI on PATH, `claude --version` succeeds, a one-token probe is answered, local window budget remaining versus `ai.cliMinRemainingPercent` | Present, signed in, above threshold | "CLI route off: <not found, signed out, or 38% of the window budget left>; assistant calls go to the backend" | Sign in to the CLI; raise the threshold; wait for the window to reset | Warning | F |
| Section-specific | each section | For example the coding round needs a terminal-safe work dir; system design needs nothing | Declared by the package | Declared by the package | Declared by the package | As declared | as declared |

Classification rule: a check is blocking when a session could not be completed or scored correctly without it; everything about network, sync and AI is a warning, because offline practice must still work.

### 6.5 Diagnostics

"Copy report" on the Doctor screen produces a plain-text block: app version, OS and arch, every check with status and measured values, calibration factors, toolchain versions and paths with the user name replaced by `~`, pack ids and versions with status, last sync time, last 20 engine log lines with paths redacted, and a support id. Never included: tokens, refresh tokens, device keys, email, any session code, chat text, notes, file contents, or full paths with the user name. The same report, minus logs, is attached automatically to feedback sent from the app.

### 6.6 Feeding the dashboard and keeping guides current

Health results are stored in `health_results` and summarised on the dashboard as one line per language ("Java ready, 1.3x calibration", "Python not set up"). Warnings appear as a banner with the fix link. Support reads the copied report. Setup guides live beside the language package that they describe, so a toolchain version bump in `language.json` and the guide change land in the same folder and the same PR; the website regenerates its setup pages from `docs/setup-guides/` on every build, and the Doctor's fix links point at the guide section matching the failing check id.

## 7. Content system

### 7.1 Problem format on disk

```
content/problems/<problem-id>/
├── problem.json            # language-neutral: identity, taxonomy, phases, follow-ups, versions
├── brief/
│   ├── intro.md
│   ├── phase1.md … phaseN.md
├── hints/
│   ├── phase1.md … phaseN.md   # three sections each: "## Level 1" where to look, "## Level 2" the approach, "## Level 3" concrete steps, no code
├── walkthrough.md          # shown after finish, language-neutral; code shown via {{snippet:<lang>:<name>}} tags
├── grading/
│   └── NOTES.md            # grader notes for the reviewer model, language-neutral
├── snippets/               # optional: per-language code snippets referenced by walkthrough.md
│   ├── java/*.java
│   └── python/*.py
└── variants/
    ├── java/
    │   ├── variant.json    # readOnly[], main, tests per phase, data files, toolchain min version, verifiedAt
    │   ├── starter/        # flat default-package files plus data/
    │   ├── reference/      # files that overlay starter/ to make every phase pass
    │   └── naive/          # files that overlay starter/: passes all phases but the last, times out there
    ├── python/             # same four entries
    ├── javascript/
    └── go/
```

`problem.json`:

```json
{
  "version": 3,
  "formatVersion": 1,
  "id": "hold-exposure",
  "section": "coding-round",
  "title": "Hold Exposure",
  "summary": "A risk service totals the authorisation holds on each account...",
  "difficulty": "Easy",
  "techniques": ["intervals", "sorting"],
  "domain": "fintech",
  "minutes": 60,
  "phases": [
    { "id": 1, "title": "Fix the bug",      "brief": "brief/phase1.md", "hints": "hints/phase1.md", "weight": 20, "allOrNothing": true,  "timeoutRefMs": 30000 },
    { "id": 2, "title": "Peak and breach",  "brief": "brief/phase2.md", "hints": "hints/phase2.md", "weight": 30, "allOrNothing": false, "timeoutRefMs": 30000 },
    { "id": 3, "title": "Busy books",       "brief": "brief/phase3.md", "hints": "hints/phase3.md", "weight": 20, "allOrNothing": false, "timeoutRefMs": 40000 }
  ],
  "followUps": ["Why did the original code compile without a warning..."],
  "languages": ["java"]
}
```

`variants/java/variant.json`:

```json
{
  "language": "java",
  "minToolchain": "21",
  "main": "Main",
  "readOnly": ["Phase1Tests.java", "Phase2Tests.java", "Phase3Tests.java", "Datasets.java", "data/holds.csv"],
  "tests": { "1": "Phase1Tests", "2": "Phase2Tests", "3": "Phase3Tests" },
  "verifiedAt": "2026-10-14",
  "verifiedWith": "temurin-21.0.4",
  "referenceMs": { "Phase3Tests.oneBusyAccount": 1239, "Phase3Tests.manySmallAccounts": 508 }
}
```

Per-test limits stay in the test files as today (`timeoutMs`), interpreted as milliseconds on the reference machine and scaled by the calibration factor (section 4). `referenceMs` is written by the verify tool and used to check the five-times headroom rule.

**Pack build and publish.** `content/tools build fintech-core` reads `pack.json`, copies each listed problem at its version, validates schemas, runs verify for every declared language, writes `manifest.json` with per-file SHA-256, encrypts the content folder into `content.enc` with a fresh content key, signs the manifest with the publisher Ed25519 key, uploads `pack-fintech-core-3.drp` to R2, and registers `{packId, version, contentKey, sha256, minAppVersion}` with the backend admin API, which stores the content key for wrapping per device. The source manifest never contains keys or hashes.

### 7.2 Taxonomy and filters

| Axis | Values | Source |
|---|---|---|
| Section | coding-round, dsa-quick, system-design, lld, ai-ml | `problem.json.section` |
| Technique | the 18 NeetCode 150 topic groups below, plus sorting, hashing, caching | `problem.json.techniques[]` |
| Difficulty | Easy, Medium, Hard | `problem.json.difficulty` |
| Domain | fintech, e-commerce, logistics, social, streaming, healthcare, developer-tools, gaming, education, travel, other | `problem.json.domain` |
| Language | languages with a verified variant | `problem.json.languages[]`, validated against `variants/` |
| Pack | the packs listing the problem | derived from `pack.json` files |

Filters combine with AND across axes and OR within an axis. The dashboard groups by technique by default, with difficulty and domain as chips.

### 7.3 Coverage map

Target catalogue: 10 problems in the base app (2 free plus a launch pack of 8, ADR 0001), 24 at launch after Phase 1ae (ADR 0007; every problem names the two or three NeetCode 150 questions it covers in `covers[]`, and the 150 questions themselves ship as the DSA section in Phase 1ad), 36 by the end of Phase 3, 60 by the end of the first year. Every problem exercises one primary technique and usually a secondary one. Counts below are planned, not written; the three existing problems are marked.

| NeetCode 150 topic group | Planned scenario problems (domain) | Easy / Medium / Hard | Pack and phase |
|---|---|---|---|
| Arrays and hashing | Duplicate order detector (e-commerce); Playlist dedupe and frequency (streaming) | 1 / 1 / 0 | Launch pack A; Phase 3 |
| Two pointers | Delivery window merger (logistics) | 0 / 1 / 0 | Phase 3 |
| Sliding window | Rate limiter audit (developer tools); Stream buffer health (streaming) | 1 / 1 / 0 | Launch pack A; Phase 3 |
| Stack | Build-log bracket checker (developer tools); Undo history (education) | 1 / 1 / 0 | Launch pack B; Phase 3 |
| Binary search | Shift roster lookup (healthcare); Version bisector (developer tools) | 1 / 1 / 0 | Launch pack B; Phase 3 |
| Linked list | Playlist editor (streaming) | 0 / 1 / 0 | Phase 3 |
| Trees | Org chart permissions (social); Category tree pricing (e-commerce) | 0 / 1 / 1 | Launch pack B; Phase 3 |
| Tries | **Merchant Suggest (fintech, existing)**; Command palette (developer tools) | 0 / 0 / 2 | Launch pack A; Phase 3 |
| Heap and priority queue | ER triage queue (healthcare); Matchmaking lobby (gaming) | 0 / 1 / 1 | Launch pack B; Phase 3 |
| Backtracking | Shift assignment solver (healthcare); Loot table generator (gaming) | 0 / 1 / 1 | Phase 3; Phase 7 |
| Graphs | **Corridor Router (fintech, existing, free)**; Friend suggestion (social) | 0 / 2 / 0 | Free; Launch pack A |
| Advanced graphs | Dependency installer order (developer tools); Cheapest flight with stops (travel) | 0 / 1 / 1 | Phase 3; Phase 7 |
| 1-D dynamic programming | Coupon stacking (e-commerce); Streaming bitrate ladder (streaming) | 0 / 1 / 1 | Launch pack B; Phase 7 |
| 2-D dynamic programming | Warehouse path with obstacles (logistics); Text diff for code review (developer tools) | 0 / 1 / 1 | Phase 3; Phase 7 |
| Greedy | Courier load balancer (logistics); Ad slot scheduler (streaming) | 0 / 1 / 1 | Launch pack A; Phase 7 |
| Intervals | **Hold Exposure (fintech, existing, free)**; Meeting room booker (education) | 1 / 1 / 0 | Free; Phase 3 |
| Math and geometry | Delivery zone geofence (logistics); Game camera clamp (gaming) | 1 / 1 / 0 | Phase 3; Phase 7 |
| Bit manipulation | Feature flag bitsets (developer tools); Permission masks (social) | 1 / 1 / 0 | Launch pack B; Phase 7 |

Base app and launch (Phases 1a to 1c): free Hold Exposure and Corridor Router (ADR 0001) plus the launch pack of 8 (Merchant Suggest, Duplicate order detector, Rate limiter audit, Courier load balancer, Friend suggestion, Playlist dedupe, Build-log bracket checker, Shift roster lookup). That is 10. Phase 1ae adds 14 more from the rows below for the launch catalogue of 24. Phase 2 adds pack B of 6 (Org chart permissions, ER triage queue, Coupon stacking, Feature flag bitsets, Command palette, Meeting room booker) for 16. Phase 3 adds 20 across every group (36). Phase 7 adds the remaining hard problems (60).

### 7.4 Authoring pipeline

| Step | Who | Output | Check |
|---|---|---|---|
| 1. Idea | you, from the coverage map cell | one-paragraph scenario, technique, domain, difficulty | fits an empty cell; original, not a known interview question |
| 2. Design | you with AI draft | `problem.json`, brief outline, the planted bug, the feature, the scale workload | the bug is realistic and compiles; the scale phase needs the technique, not micro-optimisation |
| 3. Starter code | AI draft, you edit | `variants/java/starter/` with Main, data files, Datasets, Phase tests | reads like production code someone else wrote; tests are read-only and deterministic |
| 4. Reference | AI draft, you verify | `variants/java/reference/` | every test green |
| 5. Naive | AI draft, you verify | `variants/java/naive/` | phases 1 to N-1 green, last phase TIMEOUT, and the naive time is at least 3x the limit |
| 6. Walkthrough, hints and grading notes | AI draft from the design, you edit | `walkthrough.md`, `hints/phase<N>.md` with three levels each, `grading/NOTES.md` | notes list the common assistant mistakes, as today; level 3 of a hint never contains code and never names the exact fix |
| 7. Review checklist | you | PR checklist ticked | names and domain consistent; no copied text; limits at 5x reference; follow-ups answerable aloud |
| 8. Verify | CI | `content/tools verify <id>` | starter fails, reference passes, naive times out; `referenceMs` recorded; every phase has a hints file with three non-empty levels and no code fence |
| 9. Port | AI draft per language, you run | `variants/<lang>/` | same three-way verification per language; a native reviewer reads the port before the language ships |
| 10. Beta | two testers per pack | session recordings and feedback | no test flakiness across two machines; time budget realistic |

**Where AI drafts and where it is checked.** Steps 2 to 6 and 9 are drafted by Claude from a fixed authoring prompt that includes the problem format, two existing problems as examples, and the coverage cell. Nothing drafted ships without the three-way verification passing on a real toolchain and without you reading the brief and the walkthrough. The authoring prompt is kept in `content/tools/prompts/author.md` and versioned.

**Authoring workbench (later phase).** A page in the admin area: pick a coverage cell, run the authoring prompt, show the drafted folder as a diff, run verify in a container, and on approval commit the folder to a branch and open the PR. This is the "single place where we prompt, review and place" you asked for; it writes folders, never anything else.

### 7.5 Content for non-code sections

```
content/problems/<id>/            # section: system-design
├── problem.json                  # section, techniques (e.g. caching, sharding), difficulty, domain, minutes, parts[]
├── brief/intro.md, part1.md …    # the prompt as an interviewer would give it, revealed step by step
├── hints/part1.md …              # same three-level format; shown in Practice sessions
├── rubric.json                   # criteria: id, text, points, evidence hints; self-markable offline
├── model-answer.md               # shown after finish
├── grading/NOTES.md              # for the reviewer model
└── variants/                     # none; or "answerFormat": "text+diagram"
```

Authoring: same pipeline without steps 3 to 5 and 8; verification is a rubric review by you and a dry run with the reviewer model on a model answer and on a weak answer, checking that the scores order correctly. Grading without a runner: offline, the user ticks the rubric after reading the model answer and the score is marked "self-assessed"; online, the reviewer model returns rubric scores as JSON validated against `rubric.json`, and the result page shows both.

## 8. Payments and licensing

### 8.1 End-to-end purchase flow

You chose not to integrate a gateway yet. The flow below is built so that the gateway is one adapter behind a port, and the payment page exists from day one.

1. **Buy on the website.** The store page lists packs with the terms from the `plans` table (monthly and quarterly at launch) in INR for Indian visitors and USD otherwise, detected by IP and overridable. "Buy" creates an `order` (`pending`, plan, amount, currency, promo code if any) and opens `/checkout/<orderId>`, a page the website owns. The page asks the `PaymentPort` for a payment session and renders whatever the active adapter returns.
2. **Adapters behind `PaymentPort`.** The port has four operations: `createPaymentSession(order)`, `handleWebhook(rawBody, headers)`, `refund(order)`, `status(orderId)`. Adapters: `MockPaymentAdapter` renders a card-like form that always succeeds in dev and in staging, and can be switched to "fail" or "pending" for testing; `ManualUpiAdapter` (optional, zero fee) shows your UPI id and a QR with the order id as the note, and the order stays `pending` until you confirm receipt in the admin page, which activates the entitlement; a real gateway adapter (Razorpay, Cashfree, Dodo or another) replaces the mock without touching the checkout page, the order model or entitlements. Promo codes are validated by the backend against a `promo_codes` table before the session is created, so they work with every adapter.
3. **Entitlement stored on the server.** Whatever the adapter, the result is one domain event, `OrderPaid(orderId, providerRef)`, idempotent on the provider's event id or on the admin's confirmation id. The entitlements module then writes `grantType=purchase`, `planId`, `term`, `pricePaid`, `startsAt=now`, `expiresAt=startsAt+term`, `status=active`, `deviceLimit=account default`.
4. **Redeem codes.** Admin page: "Generate codes" with pack, term, count, expiry, note; codes are 20-character random strings stored hashed. `POST /v1/redeem {code}` from the website or the app creates an entitlement with `grantType=redeem` and marks the code used. One use per code; the admin can revoke.
5. **App signs in** (device flow in 2.3) and fetches entitlements; the pack card shows "Download".
6. **Download** through signed URLs, verify, store encrypted, wrap the content key for this device.
7. **Offline afterwards** until `expiresAt`; the app checks online at least every 24 hours when it can.

| Situation | Behaviour |
|---|---|
| Buys on the website before installing | Entitlement exists; the download page is shown right after checkout; the first app sign-in downloads the pack |
| Same account on a second machine | Device flow succeeds only if active devices are below the limit; otherwise the app shows the device list from the website with "sign out" buttons and an "Add a device slot" link |
| Device limit hit | No tokens issued; nothing downloaded; existing devices unaffected |
| Term ends | Entitlement `expired`; app locks and deletes the pack content at the next online check; history kept; renewal re-activates with a new term |
| Renewal before expiry | New `expiresAt = old expiresAt + term` so no days are lost |

Entitlements are per pack and cover every language variant and every section in the pack.

### 8.2 Provider comparison for a seller in India

Facts checked 2026-10-14; fees exclude the provider's GST on fees where applicable.

| Option | Fees | India buyers (UPI, INR) | International buyers | Individual seller allowed | Tax handling | Payouts | Webhooks, coupons, subscriptions | Source |
|---|---|---|---|---|---|---|---|---|
| Razorpay (gateway) | 2% domestic plus 18% GST on the fee; international cards up to 3% | Yes, UPI, cards, netbanking | **Cards require a registered business with a website; individuals are routed to PayPal or bank transfer** | Yes for domestic | You handle GST and invoices | T+2 to your INR account | All three; offers not on international currency | [razorpay.com/pricing](https://razorpay.com/pricing/), [international payments docs](https://razorpay.com/docs/payments/payments/international-payments/) |
| Stripe India | 2% domestic, 3% international if invited | Yes if invited | Yes if invited | Invite-only for all new Indian accounts | You handle | INR | Yes | [support.stripe.com](https://support.stripe.com/questions/stripe-accounts-are-invite-only-in-india) |
| Dodo Payments (merchant of record, India-founded) | 4% + $0.15 on Indian cards and UPI; 4% + $0.40 US cards; +1.5% international; +0.5% subscriptions; $1 per refund; $30 per dispute | Yes, UPI and cards | Yes | Yes, with government ID | They are the seller of record and handle GST, VAT and sales tax | Twice a month to a USD, GBP or EUR wallet; INR payouts discontinued; $5 fee under $1,000 | Yes, all three | [dodopayments.com/pricing](https://dodopayments.com/pricing), [payout docs](https://docs.dodopayments.com/features/payouts/payout-structure) |
| Polar.sh (merchant of record) | 5% + $0.50; Pro $20 a month for 3.8% + $0.40; +1.5% non-US cards | Cards only through Stripe checkout; UPI not available | Yes | Yes, via Stripe Connect Express, India supported | Handled through Stripe Tax | Stripe payout fees passed through | Yes | [polar.sh fees](https://polar.sh/docs/merchant-of-record/fees) |
| Paddle (merchant of record) | 5% + $0.50 | Cards; UPI not offered | Yes | Not verified | Handled | Monthly, wire or Payoneer, $100 minimum | Yes | [paddle.com/pricing](https://www.paddle.com/pricing) |
| Lemon Squeezy | 5% + $0.50 + 1.5% non-US | Cards | Yes | Indian sellers must take PayPal payouts; the product is being folded into Stripe Managed Payments, which does not support India | Handled | PayPal | Yes | [lemonsqueezy.com/blog/2026-update](https://www.lemonsqueezy.com/blog/2026-update) |
| PayPal | 4.40% plus fixed fee, plus 3% currency conversion | International only | Yes | Yes | You handle | Automatic daily to INR bank | Limited | [paypal.com/in merchant fees](https://www.paypal.com/in/webapps/mpp/merchant-fees) |

**8.2b Indian gateways for the domestic adapter**, checked 2026-10-14. All are domestic-only for an individual; international buyers still need a merchant of record or Razorpay with a registered business.

| Gateway | Listed fee | Individuals | Notes | Source |
|---|---|---|---|---|
| Cashfree | 1.95% to 2.0% plus 18% GST; a 0% offer up to ₹20 lakh GMV for new merchants from 21 July 2026 | The FAQ says an individual may register with a personal PAN, but also that the business must be registered in India; confirm with their sales team | Zero setup fee; UPI, cards, netbanking, wallets | [cashfree.com/payment-gateway-charges](https://www.cashfree.com/payment-gateway-charges) |
| PhonePe Payment Gateway | "1.99% FREE*" limited offer, zero setup and annual fee | Not stated | No per-method breakdown on the page | [business.phonepe.com/payment-gateway](https://business.phonepe.com/payment-gateway) |
| PayU India | 2% domestic; 3% international, Amex and EMI; plus 18% GST | Not stated | No setup or annual fee | [payu.in/pricing](https://payu.in/pricing/) |
| Instamojo | Lite plan: 10% + ₹3 on digital products | Not stated | Too expensive for this | [instamojo.com/pricing](https://www.instamojo.com/pricing/) |

**Recommended setup, given that you will integrate a gateway later.** Ship the payment page with `MockPaymentAdapter` in development and staging. For the first real sales, `ManualUpiAdapter` costs nothing to run and lets a stranger pay before any gateway approval exists; you confirm receipts in the admin page. When you choose the gateway, the shortlist is: **Dodo Payments** if you want one merchant of record for India and abroad and accept USD payouts; **Razorpay** for Indian buyers once you have a registered business and GST, which also unlocks international cards there; **Cashfree** for domestic-only at 1.95% to 2% if it accepts you as an individual, with PhonePe as the second domestic option (8.2b). Whichever you pick is one adapter class and one webhook route.

What you must verify yourself before the first gateway sale: the chosen provider's individual onboarding documents and approval time; payout currency and conversion markup; whether it issues GST-compliant invoices to Indian buyers; the GST treatment of your sale to a foreign merchant of record if you go that way, which two vendors describe in opposite ways and no official source settles (section 8.4).

### 8.3 Pricing, refunds and piracy

Anchors from competitors, verified 2026-10-14: Hello Interview charges ₹3,199 a month, ₹6,719 a year and ₹22,799 lifetime to Indian visitors ($47, $95, $319 in the US); NeetCode Pro is $119 a year; LeetCode Premium is $35 a month or $159 a year in USD everywhere ([hellointerview.com/premium](https://www.hellointerview.com/premium), [neetcode.io/pro](https://neetcode.io/pro), [leetcode.com/subscribe](https://leetcode.com/subscribe/)). Nothing found offers a desktop app with offline practice.

Your numbers: monthly ₹1,500 and quarterly ₹3,500, no annual term yet because the catalogue is small, AI caps on every plan, prices configurable and consistent for the user. Checked against the anchors: Hello Interview charges Indian visitors ₹3,199 a month, so ₹1,500 is half the nearest comparable and above LeetCode's $35 only in USD terms. Both plans stay profitable at the AI cap (section 9.3).

| Plan (launch) | INR | USD | AI caps | Notes |
|---|---|---|---|---|
| Monthly, per pack | ₹1,500 | $19 | 300 message-equivalents a month (Ask 1, Write code 3, hint explanation 1), 60 a session, 15 written reviews a month; authored hints are free | Fixed term, no auto-renew; renewal extends from the current expiry |
| Quarterly, per pack | ₹3,500 (₹1,167 a month, 22% off) | $45 | 900 message-equivalents a quarter, 60 a session, 45 reviews a quarter | Same |
| Annual | Not offered | Not offered | | Revisit at 36 problems; section 9.3 shows why a low annual price is dangerous |
| All-access bundle | Phase 3 | | | Only when three or more packs exist |
| Lifetime | No | | | A growing catalogue makes it a loss |

**Configurable and consistent.** Prices live in the `plans` table (section 2.6), never in code or in the front end. Each plan version has `effectiveFrom` and `effectiveTo`; the admin page creates a new version rather than editing an old one. The website store, the checkout page and the app's pack cards all read `GET /v1/catalog`, so a user sees one price everywhere, and an entitlement records `planId` and `pricePaid`, so a renewal inside an active term keeps the price the user bought at. Regional lists are set by hand per currency, not converted.

Refunds: 7 days, no questions, once per account per pack, through the adapter (`refund(order)`, or a manual transfer for UPI orders confirmed in the admin page); the resulting event sets the entitlement to `refunded`, the app locks and deletes the pack at its next online check, and history is kept. After 7 days, refunds only for defects. India's e-commerce rules require refunds within a reasonable period and grievance acknowledgement within 48 hours and resolution within a month ([Consumer Protection (E-Commerce) Rules 2020](https://prsindia.org/files/bills_acts/bills_parliament/2021/Consumer%20Protection%20(E-Commerce)%20Rules,%202020.pdf), checked 2026-10-14); the support page states both.

What limits piracy realistically for offline content: a one-device default, server-checked entitlements every 24 hours when online, packs encrypted at rest with device-wrapped keys, frequent content updates that stale copies miss, fair INR pricing, and the walkthrough and feedback living in the account rather than in the files. What does not work: pretending a signed-in owner cannot extract the problem on their screen.

### 8.4 What you need legally before the first payment

Not legal or tax advice. Ask a chartered accountant who handles software exporters and OIDAR questions, and a lawyer for the privacy policy and terms.

- **Entity**: individual until revenue justifies more, as you chose. The merchant of record contracts with you as an individual.
- **GST**: services registration threshold is ₹20 lakh aggregate turnover, and inter-state supplies of services below that threshold are exempt from mandatory registration (CGST s.22 and Notification 10/2017-Integrated Tax, read from gazette copies, checked 2026-10-14). If you sell through a merchant of record, your customer is the merchant of record; whether that is an export of services that stays zero-rated, and whether you need registration to file a letter of undertaking, is exactly the question for the accountant. The rate on IT services is 18% if and when you sell direct.
- **Income tax**: foreign receipts in USD need the purpose code and your bank's FIRC or e-FIRA; keep the payout statements.
- **DPDP Act 2023**: the Rules were notified on 13 November 2025 and the substantive duties take effect about 18 months later (G.S.R. 846(E), checked 2026-10-14). Build now what they require: an itemised consent notice at sign-up, a way to withdraw consent and delete the account, breach notification to users without delay and to the Board within 72 hours, access logs kept one year, and answers to data requests within 90 days.
- **Terms and privacy policy**: what is stored (code, chat, timeline), for how long (life of the account, deletable), the AI provider as a processor, the refund policy, the device policy.
- **Payment provider terms**: read the merchant of record's prohibited content list and chargeback rules.

## 9. Economics

All prices checked 2026-10-14 and cited. Token figures are estimates built from what the prototype actually sends, measured on disk; assumptions are stated next to each number. INR comparisons use an assumed ₹85 per USD for budget comparison only; I did not verify the exchange rate, so apply the day's rate.

### 9.1 AI cost per message, per session, per active user

Model: `claude-sonnet-5-5` at $2.00 per million input tokens, $10.00 per million output tokens, cache reads $0.20 per million, cache writes 1.25x input price for the 5-minute TTL and 2x for the 1-hour TTL (Anthropic pricing as cached in the Claude API reference used for this plan, dated 2026-10-06; confirm at [anthropic.com/pricing](https://www.anthropic.com/pricing) before launch). Batch processing is 50% off every token for jobs that can wait.

What one chat message sends, from the prototype's prompt builder and the measured file sizes (section 0), at an assumed 3.5 characters per token for Java and Markdown:

| Part | Size (measured or est.) | Tokens (est.) | Changes |
|---|---|---|---|
| Rules and mode block | 1.5 KB | 430 | Never within a session |
| Visible brief | 2.5 KB | 710 | At each phase unlock (2 to 3 times) |
| Read-only files (tests, Datasets, data) | 11 to 14 KB, 65 to 71% of the project | 3,700 | Never |
| Editable files | 6 to 7 KB | 1,900 | On most messages |
| History, last 16 turns capped | 11 KB average over a 15-message session (est.: 2 KB per exchange) | 3,100 | Grows append-only |
| New message | 0.2 KB | 60 | Every message |
| Reply plus adaptive thinking at low effort | 1.8 KB visible plus thinking (est. 200 tokens) | 700 output | Every message |

Per message and per session (15 messages, 3 phase unlocks, 1 feedback call):

| | Without caching | With caching, prefix order: rules, brief, read-only files, history, then editable files and the new message last |
|---|---|---|
| Input billed at full price per message | 9,900 tokens, $0.0198 | 1,960 tokens (editable files and message), $0.0039 |
| Cache reads per message | 0 | 7,900 tokens average, $0.0016 |
| Cache writes | 0 | History delta 600 tokens per message at 1-hour TTL, $0.0024; prefix written 3 times per session, 4,840 tokens, $0.0194 each |
| Output per message | 700 tokens, $0.0070 | $0.0070 |
| **Per message** | **$0.027** | **$0.015** |
| Chat per session (15 messages) | $0.40 | $0.22 plus $0.06 prefix writes = $0.28 |
| Write code call (same input, est. 1,800 output tokens of edits and note) | $0.038 | $0.03 |
| Hint explanation (Practice only; same shape as a chat message) | $0.027 | $0.015 |
| Feedback call (est. 11,500 input tokens: brief, grader notes, timeline with edit and hint events, full chat capped, notes, diff; 3,000 output tokens at medium effort) | $0.053 | $0.053 |
| **Per session, Ask only (15 messages)** | **$0.45** | **$0.33** |
| **Per session, mixed (15 Ask, 4 Write code, 2 hint explanations)** | **$0.67** | **$0.49** |

Medium versus low effort on the feedback call differs by roughly 1,000 output tokens, about $0.01 per session, which is why section 2 asks for medium there and low for chat.

Per active user per month (est.): 4 mixed sessions a month gives **$1.95 with caching, $2.70 without**; Ask-only use would be $1.30 and $1.80. The rest of this section uses the mixed figure. Free users are capped (9.6) so that a free user costs at most about $0.50 a month at the cap and about $0.08 a month on average (assumption: one in five free users uses the assistant, averaging $0.40).

### 9.2 Cost model: app-heavy versus website-heavy

Both paths send AI calls through your backend, so AI cost is identical. The difference is where code runs and what is served.

| Cost line | App-heavy (recommended) | Website-heavy (in-browser practice with containers) | Source |
|---|---|---|---|
| Backend VPS | Hetzner CPX12, 1 vCPU, 2 GB: €11.99 a month excl. VAT ($14.09); CPX22, 2 vCPU, 4 GB: €19.99 ($23.59). The Indian equivalent, E2E Networks E1LC-2.6GB at ₹894 plus GST, is in 9.2b | Same plus execution hosts | [hetzner.com/cloud](https://www.hetzner.com/cloud/regular-performance/), [e2enetworks.com/pricing](https://www.e2enetworks.com/pricing) |
| Database | Postgres in Docker on the VPS: $0; Neon pay-as-you-go at scale (storage $0.35 per GB-month, compute $0.106 per CU-hour) | Same | [neon.com/pricing](https://neon.com/pricing) |
| Storage and CDN | Cloudflare R2: $0.015 per GB-month, egress free, 10 GB free; installers, packs and toolchains fit in about 3 GB | Same, plus the web app's assets, which are small | [developers.cloudflare.com/r2/pricing](https://developers.cloudflare.com/r2/pricing/) |
| Website hosting | Cloudflare Pages: free | Free | [Pages limits](https://developers.cloudflare.com/pages/platform/limits/) |
| Code execution | On the user's machine: $0 | Fly.io shared-cpu-1x 1 GB machine $6.70 a month always-on, or about $0.0093 an hour billed per second (est. from the monthly price); one machine per concurrent session | [docs.fly.io/about/pricing](https://docs.fly.io/about/pricing) |
| Payment fees | Dodo: 4% + $0.15 India, 4% + $0.40 US, +1.5% international, +0.5% subscriptions | Same | [dodopayments.com/pricing](https://dodopayments.com/pricing) |
| Signing and developer accounts | Apple $99 a year; IV certificate $129 a year; cloud signing subscription not verified | Same if the app exists; $0 for a website-only product | Section 3 |
| CI | GitHub Free 2,000 minutes; macOS $0.062 a minute beyond, about $4 a month at two releases | Lower | [GitHub Actions pricing](https://docs.github.com/en/billing/reference/actions-minute-multipliers) |
| Domain | Cloudflare Registrar .com $10.46 a year | Same | Cloudflare Registrar search, checked 2026-10-14 |
| Email | Resend free to 3,000 a month, then $20 | Same | [resend.com/pricing](https://resend.com/pricing) |
| Monitoring | Sentry Developer free; Team $26 a month annual. PostHog free to 1M events | Same | [sentry.io/pricing](https://sentry.io/pricing/), [posthog.com/pricing](https://posthog.com/pricing) |

Fixed monthly cost and variable cost per monthly active user (MAU), estimates in USD, assuming 5% of MAU pay, 4 sessions a month for paying users, free users as in 9.1, caching on:

| MAU | App-heavy fixed | App-heavy variable per MAU | Website-heavy fixed | Website-heavy variable per MAU |
|---|---|---|---|---|
| 100 | $38 (CPX12 $14, signing $19, CI $4, domain $1) | $0.17 (AI: 0.95 × $0.08 + 0.05 × $1.95) plus payment fees | $62 (adds one execution box, CPX22 $24) | $0.17 plus about $0.19 of execution (4 session-hours a month per paying user at $0.0093, spread over MAU) |
| 1,000 | $48 (CPX22 $24, signing, CI, domain) | $0.17 | $120 (adds about $72 of execution capacity for peak concurrency of roughly 10 sessions) | $0.17 plus $0.19 |
| 10,000 | $160 (CPX22, Neon about $25, Resend $20, Sentry $26, PostHog usage, signing, CI) | $0.17 | $560 (adds about $370 of per-second execution for 40,000 session-hours a month, plus $30 of egress) | $0.17 plus $0.19 |

In INR at the assumed rate the app-heavy fixed cost is about ₹3,200 a month at 100 MAU and ₹4,100 at 1,000 MAU, inside your ₹3k to 10k budget. The website-heavy path at 1,000 MAU is about ₹10,200 before any engineering time for sandbox hardening.

### 9.2b The same app-heavy path on Indian and India-billed providers

Prices from section 3.4, checked 2026-10-14. GST at 18% is included on INR items; foreign items (Apple, the signing certificate, GitHub macOS minutes, Sentry) converted at the assumed ₹85 per USD for comparison only.

| Line | 100 MAU | 1,000 MAU | 10,000 MAU |
|---|---|---|---|
| Backend VM, E2E Networks | E1LC-2.6GB ₹894 + GST = ₹1,055 | E1LC-2.6GB ₹1,055 | C3.8GB ₹3,422 + GST = ₹4,038 |
| Postgres | On the VM, ₹0 | On the VM, ₹0 | DigitalOcean Managed Postgres Bangalore $15.15, about ₹1,290 (or Supabase Mumbai Pro $25) |
| Object storage and CDN, Cloudflare R2 and Pages | ₹0 (inside the free 10 GB) | ₹0 | About ₹100 (storage past 10 GB; egress free) |
| Email, Zoho ZeptoMail | ₹0 (first 10,000 free) | ₹150 (one credit) | ₹600 (about 40,000 emails) |
| Monitoring and analytics | Sentry free, Umami and Uptime Kuma on the VM: ₹0 | ₹0 | Sentry Team $26, about ₹2,210 |
| Domain | ₹80 | ₹80 | ₹80 |
| Signing and developer accounts | ₹1,615 | ₹1,615 | ₹1,615 |
| CI macOS minutes | ₹340 | ₹340 | ₹340 |
| **Fixed total** | **about ₹3,100** | **about ₹3,250** | **about ₹10,300** |
| Variable per MAU (AI plus payment fees), unchanged from 9.2 | $0.17, about ₹14 | ₹14 | ₹14 |

The Indian-hosted path costs about the same as the Hetzner draft at launch and gives three times the RAM; the saving is not in rupees but in INR invoices with GST and no monthly foreign card charge. Half of the launch bill is signing and developer accounts, which no hosting choice can move.

### 9.3 Break-even

Fixed cost at launch about $38 a month (section 9.2; the Indian-hosted variant in 9.2b is similar). Contribution per paying user per month = price per month net of fees minus AI. Typical mixed AI use is $1.95 a month; the plan caps bound the worst case at about $5.30 (300 message-equivalents spent as Ask, which is the costliest mix per equivalent, plus 15 reviews).

| Plan | Monthly equivalent | Net of a 4% + $0.15 gateway fee | Contribution at typical AI ($1.95) | Contribution at the AI cap ($5.30) | Paying users to cover $38 (typical) |
|---|---|---|---|---|---|
| ₹1,500 a month ($17.65 at the assumed ₹85) | $17.65 | $16.79 | $14.84 | $11.49 | 3 |
| ₹3,500 a quarter ($41.18) | $13.73 | $13.13 | $11.18 | $7.83 | 4 |
| $19 a month (international) | $19.00 | $18.09 | $16.14 | $12.79 | 3 |
| $45 a quarter (international) | $15.00 | $14.35 | $12.40 | $9.05 | 4 |
| For comparison: ₹1,999 a year, rejected | $1.96 | $1.87 | negative | negative | never |

With the manual UPI adapter the gateway fee is zero and each contribution rises by about $0.70. Even at the AI cap every launch plan keeps more than 60% of its price, so the caps protect against abuse rather than against ordinary heavy use.

Sensitivity:

| Ask messages per session (with 4 Write code calls and 2 hint explanations) | AI per session (cached) | AI per paying user per month at 4 sessions |
|---|---|---|
| 10 | $0.41 | $1.65 |
| 15 | $0.49 | $1.95 |
| 25 | $0.64 | $2.55 |

| Conversion from free to paid | MAU needed for 3 monthly buyers | MAU needed for 4 quarterly buyers |
|---|---|---|
| 2% | 150 | 200 |
| 5% | 60 | 80 |
| 10% | 30 | 40 |

### 9.4 Does the app cost less to operate than the website?

Yes, and the reason is specific: the app moves code execution and most of the serving to the user's machine, so your variable cost is almost only AI and payment fees, while website practice adds a per-session container bill and the security and operations work that comes with running untrusted code. What the app does not save is AI cost, which is the same in both paths, and it adds fixed costs the website would not have: signing, developer accounts, two operating systems to test and support. At 1,000 MAU the difference is about $70 a month plus the engineering time; the real argument for the app is decisions 9 and 12, not money.

### 9.5 Marginal cost of a language and of a section

| Addition | Your time (est.) | Running cost |
|---|---|---|
| A language, full port of a 36-problem catalogue, verified three ways per problem | Package 40 hours; ports about 3 hours each with AI drafting and your verification, 108 hours; a paid native reviewer, roughly ₹15,000 to 30,000 (est., not quoted); total 150 hours or about 11 weeks at 14 hours a week | Toolchain downloads on R2 with free egress: about $0; CI minutes: a few dollars a month |
| A section, for example system design, with 15 problems | Package 60 to 80 hours; content 4 hours a problem, 60 hours; total about 140 hours | Reviewer-model grading about $0.05 a session; no runner cost |

### 9.6 Usage caps and abuse controls that protect the AI bill

| Control | Free | Paid |
|---|---|---|
| Assistant message-equivalents (Ask 1, Write code 3, hint explanation 1; authored hints free) | 30 a month, 15 a session | Monthly plan 300 a month; quarterly plan 900 a quarter; 60 a session on both; values live on the plan, not in code; the header shows equivalents used and Write code calls left |
| Edits per Write code call | At most 8 edits and 200 KB of output; larger replies are cut with "ask for a smaller change" | Same |
| Message length | 4,000 characters | 4,000 characters |
| Written feedback | 1 a month | Monthly plan 15 a month; quarterly 45 a quarter; 1 free re-run per session |
| Concurrency | 1 call at a time per user | 1 |
| Where prompts are built | Backend only; the app sends files and history, never instructions | Same |
| Identity | Email verified before any AI call; one account per device per month at sign-up; sign-up rate limit per IP | Same |
| Anomaly alerts | A user above 3x the median daily tokens is flagged in the admin page; three flags pause AI for that account pending review | Same |
| Model spend ceiling | A monthly spend limit at the provider plus a backend circuit breaker at 120% of the forecast that degrades to "assistant temporarily unavailable" | Same |
| Deferred feedback | Queued jobs can run through the Batch API at 50% off when the user has opted for "within an hour" | Same |

## 10. Phased roadmap

Assumptions: 14 hours a week on average (your range is 8 to 20), AI-assisted coding, content drafted by AI and verified by you. Every "weeks" figure is an estimate under those assumptions; at 8 hours a week nearly double them, at 20 hours take about 70% of them.

| Phase | Goal | In | Explicitly out | Content per language | Paid for | Exit criteria | Weeks (est.) | Main risk |
|---|---|---|---|---|---|---|---|---|
| 0. Validation | Confirm people want this and will pay | Landing page with waitlist and the improved prototype zip; 10 conversations with target users; 3 testers complete a prototype session; pricing survey; choose the provider and start Apple enrolment and the Windows certificate (validation takes days to weeks); repo skeleton | Any new product code beyond the skeleton | Java: the existing 3 | Nothing | 25 waitlist sign-ups or 5 explicit "would pay" (Definitely or Probably) at ₹1,500 a month or ₹3,500 a quarter; 3 recorded sessions with notes | 2 | Talking to the wrong users |
| 1a. Base app (decision 11, first prompt) | A working desktop app with 10 problems, no account | A two to three week Rust and Tauri learning sprint (`docs/learning/phase-1a.md`) ending in a shell that launches the prototype; engine rewrite (SQLite, run protocol, health framework, calibration); Interview and Practice session types; authored hints with three levels per phase and the Hint control; the AI edit applier with snapshots, undo, reset and the assisted flag, tested with a fake edit source; editor tier 1 (autocomplete from project symbols, inline compiler diagnostics); React UI port; Tauri 2 shell in Rust with the bundled runtime, unsigned dev builds; the Java language package and the coding-round section package; 10 problems from a local content folder with hints: the 3 existing plus 7 from the coverage map | Backend, accounts, packs, encryption, payments, AI calls (Ask, Write code, hint explanation), sync, website, signing, updater, language servers | Java: 10 (2 free, 8 for the launch pack) | Nothing yet | Full Interview and Practice sessions on both OSes from a zip; Doctor green; 10 problems verified with hints; a tester completes a session | 8 to 10, of which 2 to 3 are the learning sprint | Rust on the critical path; mitigated by the thin shell and the Electron fallback if the sprint overruns by more than a week |
| 1ab. Live assistant, scoring rule, room refresh (added 2026-10-14 after the first hands-on run) | The assistant works on the developer's machine through the CLI route, AI use no longer blocks solved, and the room follows a guided flow | `AiClient` with `CliModelClient` behind developer mode for Ask, Write code, hint explanation and the written review; point-and-explain prompts with recorded-transcript tests; score and dashboard without the assisted gate (ADR 0006); guided set-up screen, phase overview and intro cards, Guide tab with the passing pill, end-session dialog; new visual tokens, dashboard cards | Backend route, accounts, caps, usage log, remaining-allowance rule, comprehension questions, per-phase clocks | Java: 10 | Nothing yet | Ask, Write code, undo and the review work end to end on one machine; a passing Interview session with AI edits counts as solved; both session types hand-checked in the new room in both themes | 1 to 2 | Prompt rules that leak solutions; mitigated by one recorded-transcript test per rule |
| 1ac. Shell, theme, tags, re-plan (added 2026-10-15, ADR 0007) | Home dashboard, a fixed side menu whose Practice group reaches every section, one section page with a filter bar and a grouped list, the purple palette, tags on every problem | Side menu and registry by section id; Home with continue, tracks and recent results; section page with filters; `tokens.css` from `docs/design/palette.md`; `tags[]` in `problem.json`; walkthrough practice links become in-app `dsa://` chips; Java and Windows first in this plan | DSA content, new problems, backend, any language but Java | Java: 10 | Nothing yet | Every page but the room uses the window's width with the menu; tags on cards, set-up, answer and result; no external NeetCode or LeetCode links in walkthroughs | 1 to 2 | Scope creep into the room; mitigated by the room being out of scope |
| 1ad. DSA section (pulled forward from old Phase 7) | The NeetCode 150 questions in Java inside the app, by topic group, with a solving page | `sections/dsa-quick/`, `content/dsa/` with 150 one-phase questions written in batches, DSA page, cross-links both ways with the coding-round problems | Other languages, new coding-round problems | Java: 10 plus 150 DSA questions | Nothing yet | 150 questions verify three ways; a DSA session works end to end; every `dsa://` chip opens its question | 6 to 8 | Authoring volume; mitigated by batches of 10 to 20 per PR |
| 1ae. Catalogue to 24 in Java | Launch catalogue of 24 scenario problems, each covering two or three NeetCode questions | 14 new problems from the coverage map; `covers[]` on every problem, validated both ways; launch pack file | Other languages, workbench | Java: 24 | Launch pack (22 paid, 2 free) | 24 problems verify three ways; every problem has two or three `covers[]` ids | 5 to 6 | Phase-3 timing flakes; mitigated by the 5x headroom rule |
| 1af. Java IntelliSense (moved up from Phase 2, ADR 0009) | The editor behaves like an IDE in Java sessions | The Eclipse JDT Language Server as a downloadable editor plugin declared in `languages/java/editor/lsp.json`, started by the engine per session, bridged through `@codemirror/lsp-client`: completion with signatures and auto-import, hover, diagnostics, go-to-definition; Doctor check; built-in completion as the fallback | Other languages' servers, formatting, refactorings | Java: 24 | Nothing yet | A `HashMap` typed without an import compiles after completion; a project class completes and jumps to its definition; the fallback is unchanged when the server is off | 3 to 4 | First-start time and download size; mitigated by the 3 s fallback |
| 2. Catalogue to 60 (was 4; content only, no backend) | Second content wave | 36 more coding-round problems in Java, all with `covers[]` | Other languages | Java: 60 | New packs | 60 problems verified | 10 | Content quality at volume |
| 3. Website and installers (was 1c; ADR 0010: no store or sign-in yet; unsigned Windows installer until the certificate exists) | First paying user | Website (landing, setup guides, download; pricing, sign-in, store and account pages arrive with Phase 5); jlink runtime, Tauri bundler configuration, updater plugin with signed `latest.json` channels; Doctor completion and copy-report; the free problems and the developer-route assistant in the installer; beta of five testers from the download page. Windows signing through `signCommand` and macOS notarization land when the certificate and a macOS machine exist; until then the Windows installer ships unsigned and the macOS CI job is allowed to fail | Store, sign-in, payment, sync, dashboard, Python, Linux, macOS as a gate | Java: 60 | Nothing yet | Five testers install from the website and finish a session; crash-free week; update from 0.x to 0.x+1 works on Windows | 3 to 4 | Signing and notarization friction; mitigated by shipping unsigned first and signing every build once the certificate arrives |
| 4. System design section (was 5, then 3; no backend) | First no-code section | Section package with rubric grader and reviewer model, 15 design problems, result parts | | | System design pack | Offline self-marking and online reviewer scores agree in ordering on a test set | 8 | Reviewer-model grading consistency |
| 5. Accounts, packs, payment page, AI gateway, admin (was 1b; no credential needed, ADR 0011) | Buy, download, practise, chat and get feedback through the backend; the launch of paid content | Backend identity with device flow, catalog with the `plans` table, entitlements, redeem codes, promo codes, `PaymentPort` with the mock adapter and the manual UPI adapter, signed URLs and keys, encrypted packs, AI gateway with Ask, Write code and hint-explanation endpoints, caching and message-equivalent caps, tested against a recorded model client (the provider key is Phase 8), minimal admin page; engine pack client and AI client wired to the edit applier, with the developer-mode route between the Claude CLI and the backend (2.5); E2E Networks hosting with staging | Real gateway adapter, provider key, signing (all Phase 8), website beyond activate and checkout, sync, deferred feedback | Java: 10 | Launch pack, monthly and quarterly, paid by UPI and confirmed by you until a gateway is live | Redeem and UPI orders produce entitlements; pack locks and deletes correctly; Write code applies and undoes edits end to end; caps enforced per channel; staging and production deploy from CI | 5 | Entitlement and key handling bugs; mitigated by tests on expiry, revoke, refund and device limit |
| 6. Sync, deferred feedback (reads from the Phase 5 backend) | Same state on every device, AI review for offline sessions | Push and pull, outbox, feedback requests, device management in the app, pack B of 6, promo-code reporting, pack update flow; the Eclipse JDT Language Server as a downloadable editor plugin behind a flag through `@codemirror/lsp-client` | Website dashboard | Java: 16 | Launch pack and pack B | Two devices show identical history; an offline session gets feedback after reconnecting; 15 problems live | 6 | Sync edge cases eating the schedule; mitigated by append-only design |
| 7. Website dashboard and content growth (reads from the Phase 5 backend) | Website shows history, scores and feedback; catalogue to 36; authoring workbench | Read-only dashboard, feedback requests from the website, all-access bundle, the chosen gateway adapter if not already live, authoring workbench in admin | Website practice | Java: 36 | Packs, bundle | 36 problems verified; workbench produces a problem end to end | 10 | Content quality at volume |
| 8. Credentials: payment gateway, provider key, signing (ADR 0011) | Money and the assistant flow through real providers and the installers are signed | The chosen gateway adapter behind `PaymentPort` with webhooks and refunds; the AI provider key on the gateway, the developer route demoted to development only; Windows signing through `signCommand` on CI, macOS notarization; the CA review of ADR 0002 closed | New product work | Java: 60 | Gateway payments | A stranger pays through the gateway; a signed-in user's Ask runs on the provider key with caps enforced; a signed installer and a notarized `.dmg` install clean | 3 | Provider onboarding and certificate issuance; mitigated by the mock, manual UPI and unsigned paths staying live until each lands |
| 9. Python (was 8; decision 7) | Full catalogue in Python | Language package, kit, calibration, downloads, setup guide, port of 36 problems, native reviewer, flag rollout | Any other language | Java 36, Python 36 | Nothing new; packs now cover two languages | Three-way verification green for 36 problems on both OSes; reviewer sign-off | 11 | Timing noise in Python phase 3 tests |
| 10. JavaScript/TypeScript, website practice decision (was 9) | Full catalogue in TypeScript; decide on in-browser practice | Language package, port of 36, Node 26 LTS; a spike running the engine in a container to price option (b) | In-browser practice itself | 36 in three languages | None new | Verification green; a written go or no-go on website practice with measured cost | 10 | Type stripping limits in content |
| 11. Go and Linux (was 10) | Fourth language; Linux build | Language package, port of 36, Linux AppImage and deb unsigned, Linux health checks | | 36 in four languages | None new | Verification green on three OSes | 10 | Build cache and first-run latency |
| 12. Industry mode (2.7) | Proctored assessments for companies | Organisations, invites, assessment section, native proctor addon, integrity report, company dashboard, legal review | Webcam | | Seats and assessments | One paying company pilot | 14 | Legal and platform constraints |

Phases 1a to 2 are the base product: the app, the assistant, the shell, the DSA section, the catalogue, IntelliSense and the catalogue to 60, about 40 to 50 weeks at 14 hours a week with a usable app after 1a. Then the website and installers (3) so testers can get it, the system design section (4), and only then the backend with accounts, packs, the payment page, the AI gateway and admin (5), on the mock and manual UPI adapters and a recorded model client (ADR 0010). Sync (6) and the website dashboard (7) read from that backend and follow it; then one phase (8) for everything that needs a credential: the real payment gateway, the provider key, signing and notarization (ADR 0011); the other languages are last. Each later language package brings its own language server plugin with it. Each phase runs from its prompt in `docs/prompts/` through the pipeline in section 15.

### Release and update plan

| Part | How it ships | Cadence |
|---|---|---|
| App | Tauri updater plugin reading signed `latest.json` files on R2; channels `beta` and `stable`; install on next start, forced update only when the backend raises the version floor | Every 2 to 4 weeks; hotfix any time |
| Language and section packages | Inside the app installer; enabled by flag per package; dark until the catalogue is ported | With the app |
| Content packs | Immutable versions on R2; the catalog points to the current version; the app downloads in the background and swaps at next start; `minAppVersion` prevents a pack landing on an app that cannot read it | Any time; content CI publishes on tag |
| Backend | Blue-green on one VPS with two compose services behind Caddy; additive migrations; API v1 kept for 12 months after v2 | Continuous |
| Website | Cloudflare Pages on merge | Continuous |
| Compatibility | The backend accepts the two most recent sync protocol versions and serves every pack format still inside the app version floor; the app refuses packs above its format range and offers the update | Reviewed at every app minor release |

## 11. What you need to learn

Ordered by when you need it. "Minimum" is what the plan actually requires, not mastery.

| When | Skill | Why it matters here | Minimum to know | One good way to learn it |
|---|---|---|---|---|
| Phase 0 | Product discovery and pricing | You pick the first 15 problems and the price from what users say | How to run a 20-minute user interview without leading; how to read a pricing survey | "The Mom Test" (Fitzpatrick) and five interviews this month |
| Phase 1 | React with TypeScript, TanStack Query, MUI | The app UI and the website | Components, hooks, query caching, forms, routing | Rebuild the prototype's three views in React as the learning project; the official React docs "Learn" track |
| Phase 1a | Rust basics and Tauri 2 | The shell, engine launcher, keychain, later the updater and native checks | Ownership and borrowing, `Result` and `?`, structs, enums and traits, Cargo and modules, `std::process::Command`, Tokio basics; Tauri commands and events, capabilities and permissions, the shell plugin, bundle resources, `tauri.conf.json` | The official Rust book chapters 1 to 10, the Tauri 2 quick start, then the sprint exercise in `docs/learning/phase-1a.md`: a window that launches the prototype's `Server.java` and shows its dashboard |
| Phase 1 | Desktop packaging and signing | Users see a warning or they do not | Developer ID, notarization, hardened runtime; Windows code-signing certificate types and the hardware or cloud key rule; SmartScreen reputation | Apple's "Notarizing macOS software before distribution"; Microsoft's SmartScreen reputation page; sign a hello-world app on both OSes before touching Intervue |
| Phase 1 | Auth and security | Device flow, tokens, signed URLs, encryption at rest | RFC 8628 device authorization, JWT lifetimes, refresh token rotation, Ed25519 signatures, AES-GCM, X25519 key wrapping, presigned URLs | Read RFC 8628 once; implement the flow against your own backend in a spike; OWASP cheat sheets for JWT and key storage |
| Phase 1 | Payments | Webhooks create entitlements | Hosted checkout, webhook signature verification, idempotency, refunds and disputes, test mode | Your provider's webhook docs and a sandbox integration with replayed events |
| Phase 1 | Tax and compliance basics | So the accountant's questions make sense | GST threshold and export treatment at a glance, what a merchant of record changes, DPDP consent and breach duties | One meeting with a CA with the questions from section 8.4 written down |
| Phase 1 | Analytics and error tracking | You cannot fix what you cannot see | Sentry in four runtimes; event naming for PostHog; funnel from download to first session | Set both up on the prototype port in the first week of Phase 1 |
| Phase 1a | Editor assistance in CodeMirror 6 | Autocomplete, inline diagnostics, AI-edit decorations | `@codemirror/autocomplete` sources, `@codemirror/lint`, decorations and state fields; the JDK `com.sun.source` tree API for symbols | The CodeMirror 6 system guide and the autocomplete and lint examples; a javac tree-walker that lists class and method names |
| Phase 1b | Structured model output for edits | The Write code channel must return edits the engine can apply | Anthropic structured outputs or a strict JSON-lines format in the prompt, validation, retries on malformed output | Anthropic docs on structured outputs; write the edit protocol parser with golden files first |
| Phase 2 | Sync design | Decision 3 | Append-only logs, idempotent upserts, cursors, last-writer-wins | Read "Designing Data-Intensive Applications" chapter 5 on replication; your protocol in 2.2 is a subset |
| Phase 2 | Language servers | Java IntelliSense as a plugin | The LSP lifecycle (initialize, didOpen, didChange, completion), `@codemirror/lsp-client` transports, starting the Eclipse JDT Language Server with a workspace folder | The LSP specification overview; the lsp-client README; run jdt.ls by hand against the prototype folder |
| Phase 2 | Support | Users will write to you | A support mailbox, a template for the copy-report, a public status page | Set up the mailbox and the templates before launch |
| Phase 3 | Marketing and content distribution | Nobody finds a desktop app by accident | A landing page that converts, a launch post, short videos of a session, where Indian and global engineers read | "Traction" (Weinberg) for channel selection; post where your interviews said users read |
| Phase 4 | Python to review ports | You said reading level is none | Reading idiomatic Python, the standard library equivalents of Java collections, pytest-free testing style, timing noise | Port one problem yourself, slowly, with the official tutorial beside you; then review AI ports |
| Phase 4 | Sandboxing and process isolation | Per-language limits | Job Objects, prlimit, killing process trees, memory caps per runtime | Microsoft's Job Objects docs; implement the wrapper once in the engine and test it with a fork bomb and a memory hog |
| Phase 5 | TypeScript as content | You read it already | Node's type-stripping limits, explicit import extensions, worker threads | nodejs.org/api/typescript and worker_threads docs |
| Phase 6 | Go to review ports | Reading level none | Packages, slices and maps, error handling, `go vet`, module mode offline | "A Tour of Go" plus porting one problem yourself |
| Phase 9 | Native Windows and macOS APIs from Rust | Proctoring | The `windows` crate and `objc2` bindings, unsafe blocks kept small and wrapped, the APIs in 2.7 | Build the display-count check as the first `proctor` command |

Each phase has a file in `docs/learning/` with the minimal concepts, the official sources, an hour estimate and a tutor prompt you can paste into an AI assistant to learn exactly that much.

**Outsource or buy instead of learning:** tax filings and the GST question (chartered accountant); terms and privacy policy (lawyer, template-based); logo and visual identity if the MUI defaults feel generic (a designer for a day); native review of Python, TypeScript and Go ports (a paid reviewer per language before each language ships); code-signing hardware and notarization are tooling, not learning; copywriting for the landing page if conversion is poor after two iterations.

## 12. Risks and blockers

Ranked by likelihood times impact. Likelihood and impact are my judgement, not measurements.

| Rank | Risk | Likelihood | Impact | Mitigation |
|---|---|---|---|---|
| 1 | Your time: 8 to 20 hours a week against a Phase 1 of 200 to 250 hours including the Rust learning sprint | High | High | Fixed cut order in section 10; ship Windows before macOS if behind; AI-assisted coding; no feature outside the phase table |
| 1b | Rust learning on the critical path of Phase 1a | Medium | High | Time-boxed sprint with a concrete exit (a shell that launches the prototype); the tutor prompt in `docs/learning/phase-1a.md`; fallback to Electron after three weeks with the UI and engine untouched |
| 2 | Content quality and volume multiplied by languages: 36 problems times 4 languages is 144 verified variants | High | High | The three-way verify tool as a hard gate; authoring workbench; port in bulk per language; a paid native reviewer; never release a language early |
| 3 | Timing flakiness of per-test limits across machines and languages | High | Medium | Calibration factor per language; 5x and 6x headroom rules enforced by verify; per-class and per-test process isolation; result page marks a timeout within 20% of the limit as "borderline" so support can act |
| 4 | Setup friction and support load across two OSes and four toolchains | Medium | High | Bundled Java; downloaded toolchains with hashes; the Doctor with one-click fixes; the copy-report; guides generated from the package |
| 5 | Platform policies: Gatekeeper, notarization, SmartScreen | Medium | High | Notarize every build; sign every build with one identity from day one; submit to Microsoft's analysis portal; document the warning on the download page; test on clean machines before each release |
| 6 | Antivirus false positives on the installer, the bundled runtime, downloaded toolchains and freshly compiled class files | Medium | Medium | Sign all executables inside the runtime; per-machine install option; submit false-positive reports before launch; Doctor "Repair" for a quarantined runtime; downloaded toolchains come from your CDN with hashes |
| 7 | Payments: provider onboarding delay, payout friction, GST position unclear | Medium | High | Start onboarding in Phase 0; `PaymentPort` so the provider can change; CA consulted before the first sale; redeem codes let beta run before payments work |
| 8 | Auth and entitlement bugs locking out paying users | Medium | High | Offline grace is intentional: locally cached signed entitlements are honoured until `expiresAt`; the backend never deletes history; a support tool to re-issue entitlements; integration tests for the device flow and expiry paths |
| 9 | Sync bugs and data loss | Medium | High | Append-only events; sessions immutable after finish; the device keeps its data even after a successful push; nightly database backups to R2 with a tested restore |
| 10 | AI provider terms and pricing changes | Low to medium | Medium | `ModelClient` behind an interface; usage caps per user; margin checked monthly in section 9's model; a second provider adapter is a week of work |
| 11 | Running untrusted code in several languages on the user's machine | Medium | Medium | It is the user's own code on their own machine; still cap memory, kill process trees, keep the work dir separate from pack content and keys, never run code with elevated rights; for website practice, containers only |
| 12 | Content piracy and pack extraction | Medium | Low to medium | Section 8.3: device limit, server checks, encryption at rest, price, update cadence; walkthroughs and feedback bound to the account |
| 13 | Competition: web platforms adding "AI interview practice" | Medium | Medium | The desktop and offline angle is uncontested as of 2026-10-14; the proctoring mode is a second moat; speed of content matters more than features |
| 14 | Employment and tax constraints | Low (you report no restriction) | High | Keep the CA meeting in Phase 0; keep the product work off employer devices and time |
| 15 | macOS access for testing | Medium | Medium | CI macOS runners for builds and notarization; a borrowed Mac or a rented cloud Mac for a release checklist run |
| 16 | Download size on slow connections | Low with Tauri (about 50 MB) | Low | Per-arch builds; toolchains downloaded only when a language is chosen; the WebView2 bootstrapper only where the runtime is missing |
| 17 | Proctoring mode legal exposure and detection arms race | Medium (later) | High (later) | Consent and notice by design; integrity report with raw events rather than verdicts; lawyer review before the first company; detection from enumeration, not screenshots |
| 18 | AI edits damaging a user's work or trust (wrong file, lost typing, half-applied change) | Medium | High | The applier's rules in 2.8: allowed paths, base hash, exact match, per-edit snapshots, one-click undo, reset from source; every rule has a test; edits never touch read-only files or the pack source |
| 19 | Assistant use blurring the product's promise of a realistic rehearsal | Medium | Medium | Point-and-explain rules keep the assistant from handing over solutions in Interview sessions; the AI edit count is shown on the result page; the reviewer scores control of the AI from edit events (ADR 0006) |
| 20 | Language servers heavy or flaky on low-end laptops | Medium | Low | Optional download, started only during a session, heap capped, silent fallback to built-in completion, warning-level health check |

## 13. First two weeks

Assumes about 2 hours on weekdays and 4 to 5 hours on each weekend day, roughly 14 hours a week, starting Wednesday 15 October 2026. The two weeks end with something a real user can try: a landing page with a waitlist, the improved prototype downloadable as a zip, three outside testers who have each completed a session and sent notes, and the provider and signing applications in flight.

| Day | Date | Tasks (about 2 h weekdays, 4 to 5 h weekends) | Output |
|---|---|---|---|
| 1 | Wed 15 Oct | Register the domain; create the GitHub organisation and the empty monorepo with the folder skeleton from 5.2 and `CONTRIBUTING.md`; move the prototype into `docs/plan/` as the baseline | Repo exists; domain owned |
| 2 | Thu 16 Oct | Start Apple Developer Program enrolment from the Apple Developer app; order the individual-validation code-signing certificate and begin identity validation; write the gateway shortlist with the questions from 8.2 so the choice is ready when you want it | Two applications in flight; shortlist written |
| 3 | Fri 17 Oct | Write the landing page copy: the promise, three screenshots of the prototype, "join the waitlist", a short form asking role, target companies, language, and what they would pay | Copy drafted |
| 4 | Sat 18 Oct | Build the landing page in React + Vite + MUI, deploy to Cloudflare Pages with the waitlist form posting to a sheet or a tiny backend; add PostHog page events | Landing page live |
| 5 | Sun 19 Oct | Package the improved prototype: run `java tools/Verify.java`, zip it with a one-page "how to run" for JDK users, upload to R2, link from the landing page as "early prototype for engineers with a JDK" | Downloadable prototype |
| 6 | Mon 20 Oct | Write the interview script (8 questions, 20 minutes) and the recruiting message; post in two places your target users read; message 15 people directly | 15 outreach messages |
| 7 | Tue 21 Oct | Book interviews; prepare the pricing survey with three price points in INR and USD; book the CA meeting with the section 8.4 questions | Calendar filled |
| 8 | Wed 22 Oct | Interviews 1 and 2; notes in `docs/discovery/` | 2 interview notes |
| 9 | Thu 23 Oct | Interviews 3 and 4; send the prototype zip to the three most engaged people with a request to complete one session and reply with the copied result page and three sentences | 4 notes; 3 testers started |
| 10 | Fri 24 Oct | Interviews 5 and 6; CA meeting if booked; record decisions in `docs/adr/0001-payments-mock-then-gateway.md` and `0002-entity-and-gst.md` | 6 notes; two ADRs |
| 11 | Sat 25 Oct | Learning sprint day 1, from `docs/learning/phase-1a.md`: install Rust and the Tauri 2 prerequisites, work through the Rust book chapters 1 to 4, build the Tauri quick-start app | Rust toolchain working; first Tauri window on your machine |
| 12 | Sun 26 Oct | Engineering spike 2: device authorization flow end to end in a throwaway Spring Boot app plus a CLI client, including refresh rotation and revoke; write the flow into `docs/contracts/api-v1.yaml` | Working device flow spike |
| 13 | Mon 27 Oct | Interviews 7 and 8; collect tester results; fix anything in the prototype that blocked a tester (content or runner) | 8 notes; tester fixes |
| 14 | Tue 28 Oct | Interviews 9 and 10; write the Phase 0 review: waitlist count, "would pay" count, price sensitivity, tester notes, go or adjust decision for Phase 1 scope; decide the free problem and pack A list from the coverage map | Phase 0 review document; Phase 1 scope frozen |

If the certificate is still pending on day 14, Phase 1 engineering starts anyway; signing is the last two weeks of Phase 1, not the first. Payments need no approval to start: the mock and manual UPI adapters are built in Phase 1 and the gateway adapter is added when you choose one.

## 14. The five decisions, as they stand

1. **Payment route: decided.** A payment page with a mock gateway behind `PaymentPort`, a manual UPI adapter for the first real sales, and a gateway adapter when you choose one (section 8.1). Nothing to approve before Phase 1.
2. **Windows signing route: default taken.** Individual-validation certificate with the CA's cloud signing from CI; the USB token on a self-hosted runner remains the fallback. Say so if you prefer the token.
3. **Phase 1 scope: default taken.** The cut order in section 10 applies if the target is at risk: macOS three weeks after Windows, launch pack of 5 to 7, admin as SQL and a CLI. With the Rust sprint, the first sale is month 4 to 5 at 14 hours a week or month 3 at 20.
4. **Pricing: decided.** Monthly ₹1,500 and quarterly ₹3,500 with AI caps, no annual term, prices as configurable plan versions shown identically on the website and in the app (sections 8.3 and 9.3). International list $19 and $45.
5. **Desktop shell: decided, Tauri 2 in Rust.** Chosen on 2026-10-14 for the small download and for native work in one toolchain, accepting a two to three week Rust learning sprint at the start of Phase 1a. The UI stays React and TypeScript. The shell stays free of logic (section 5), so Electron remains the fallback if the sprint overruns.

6. **Launch content and order of work: decided.** A base working app with 10 problems first (Phase 1a), then accounts, packs and the payment page (1b), then website, installers and launch (1c), each run from its prompt file through the pipeline in section 15.
7. **Assistant and sessions: decided (section 2.8).** Two session types, Interview and Practice; two assistant channels, Ask and Write code, available in both; an Interview session with AI edits applied at a run or at finish is recorded as "assisted", which no longer changes the label, solved or the best score (ADR 0006, 2026-10-14); in Interview sessions the assistant follows the point-and-explain rules of 2.8; Practice has three-level authored hints with an online "explain further", no clock limit by default and no proctoring; edits apply immediately with undo on the session's copy and the source is never changed; Write code costs three message-equivalents; the assistant edits the whole project unless the user names a file or a method; the reviewer sees edit events. Editor: built-in autocomplete and compiler diagnostics at launch, language-server IntelliSense as per-language plugins from Phase 2.

8. **Claude CLI for your own testing: decided (section 2.5).** In developer mode the engine routes assistant calls to the Claude CLI with Sonnet 5.5 at low effort when the CLI is present, signed in and above a configurable remaining-allowance threshold (default 50%), otherwise to the backend. End-user builds never use the CLI. How the remaining allowance is read is a research item; until verified, a local window budget stands in for it.

Everything else in this plan runs on the defaults stated in place, and each default is marked where it sits.

## 15. Engineering process: prompts, gates, review and the agent team

The first version of this plan had CI gates and contributing rules but no prompt library, no review plan and no agent workflow. This section adds them, and the files it names now exist in the repository.

### 15.1 One prompt per phase

`docs/prompts/` holds one file per roadmap phase, each with the same sections: context to read, goal, in scope, out of scope, constraints, exit criteria, gates, deliverables, report. Phase 1 is split into three prompts so that a base working app exists before accounts, packs or payments: `phase-1a-base-app.md` (Rust learning sprint, engine, React UI, Tauri shell, 10 problems from a local folder, no backend), `phase-1b-accounts-packs-ai.md`, `phase-1c-website-installers-launch.md`. Phases 2 to 9 have prompts derived from the roadmap table; each is edited before it runs if the plan has moved. The prompt is the contract for that run.

### 15.2 The pipeline that runs a prompt

`/phase <file>` (a project skill in `.claude/skills/phase/`) executes `docs/process/pipeline.md`: diagnose, research, plan, design review, test-first implementation, simplify, code review, gates, report. Four narrow subagents in `.claude/agents/` do the read-only steps and return fixed-shape reports: `status-diagnoser`, `researcher` (Context7), `design-reviewer`, `qa-gatekeeper`. Implementation stays with the main agent so you can watch and interrupt.

| Your idea | How it maps |
|---|---|
| Planner with superpowers | The main agent uses the superpowers `brainstorming`, `writing-plans`, `executing-plans` and `test-driven-development` skills ([github.com/obra/superpowers](https://github.com/obra/superpowers), checked 2026-10-14) |
| Research with Context7 | The `researcher` subagent, with the Context7 MCP server added at user scope ([context7.com/docs](https://context7.com/docs/resources/all-clients)) |
| Code reviewer with the review skill | `/code-review` at high effort in step 7, findings fixed or reasoned in the PR |
| Code simplifier | `/simplify` in step 6, before review, tests kept green |
| gstack | Optional and later: single skills such as `/qa` for browser QA or `/ship`; not its planning or review skills, because superpowers already runs that workflow and two frameworks triggering on the same words fight ([github.com/garrytan/gstack](https://github.com/garrytan/gstack), checked 2026-10-14) |
| Diagnose current status | `status-diagnoser`, which also keeps `docs/status.md` honest |

### 15.3 Gates and definition of done

`docs/process/definition-of-done.md` lists every automated gate with its command, rule and the phase in which it is added: warnings as errors, Spotless and Prettier, tests with an 80% coverage floor on domain and application packages, ArchUnit tests that prove the layer rules of section 5.3, strict TypeScript and ESLint with complexity and size limits, content isolation and three-way verification including the hints check, the edit-applier rule tests of section 2.8, one part per PR, secrets scanning, the installer smoke test. The checklist half lives in `.github/PULL_REQUEST_TEMPLATE.md`.

### 15.4 Code standards for AI-written code

`docs/process/code-standards.md` and the summary in `CLAUDE.md` set the rules you asked for: small and plain over clever, standard library first, no one-implementation abstractions, methods under about 40 lines and files under about 400, names in the domain's words, comments that explain why on non-obvious blocks and nowhere else, a two to four line purpose header per non-trivial file, a test for every non-trivial branch and every money or security path, formatting delegated entirely to tools so no review time is spent on it.

### 15.5 Review plan

`docs/process/review.md`: design review before code, AI code review on every PR, your self-review with the template before merge, a monthly simplification pass, and a required second reviewer once a contributor joins. Findings close by a fix or a written reason, never by silence, and no gate is ever marked green without having run.

### 15.6 What to set up before Phase 1a

1. Install superpowers and add Context7 with your key, using the commands in `docs/process/pipeline.md`. Skip gstack for now.
2. Create the GitHub repository from `D:\projects\AICoding\intervue`, protect `main` with required CI, and label PRs by part.
3. On day 1 of Phase 1a let the pipeline build the Gradle and pnpm skeleton with the gates marked Phase 1a already wired, so every later PR is checked from the first commit.
4. Add the pre-commit hook that runs `spotlessApply` and `prettier --write` once those tools exist.
5. Read `CLAUDE.md` and the process documents once yourself, do the learning sprint in `docs/learning/phase-1a.md`, then run `/phase docs/prompts/phase-1a-base-app.md`.

### 15.7 Learning per phase

`docs/learning/` has one file per phase with four parts: the minimal concepts for that phase and nothing more, why each matters in Intervue, the official sources, and a tutor prompt written for your background that you paste into an AI assistant. The prompt asks the tutor to explain each concept in two sentences with a Intervue example, check your understanding with one question before moving on, and finish with a 30-minute exercise that produces something the phase needs.

