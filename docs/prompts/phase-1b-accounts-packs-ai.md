# Phase 1b: accounts, packs, payment page, AI gateway

## Context to read first

`CLAUDE.md`; `docs/plan/PLAN.md` sections 2.3 (auth and pack delivery), 2.5 (AI gateway), 2.6 (entities), 3.1 (backend stack, Indian hosting), 8.1 (purchase flow with `PaymentPort`), 8.3 (plans and caps), 9.6 (caps and abuse controls); `docs/process/*`; the Phase 1a contracts in `docs/contracts/`.

## Goal

A signed-in user can buy a pack on a payment page (mock or manual UPI), download it encrypted, open it offline, and use the AI assistant and written feedback through the backend within their plan's caps.

## In scope

- Backend (Spring Boot, hexagonal): identity with email magic link and Google sign-in, device authorization flow with rotating refresh tokens, sign-out everywhere; catalog with the `plans` table and `GET /v1/catalog`; entitlements with terms, expiry, device limit; redeem codes and promo codes; `PaymentPort` with `MockPaymentAdapter` and `ManualUpiAdapter`, orders, `OrderPaid` event, webhook route skeleton for a future gateway adapter; pack delivery with signed R2 URLs, content keys wrapped per device, entitlement blobs signed with Ed25519; AI gateway with `ModelClient`, `AnthropicModelClient` (`claude-sonnet-5-5`, effort low for chat, edits and hint explanations, medium for feedback), the three channel endpoints of PLAN.md 2.5 (`/v1/ai/chat`, `/v1/ai/edit` streaming the edit protocol of 2.8 with validation and a retry on malformed output, `/v1/ai/hint-explain`), prompt caching order, per-plan caps in message-equivalents (Ask 1, Write code 3, hint 1), usage log with edits returned and applied; admin page: codes, grants, order confirmation, plan versions, users.
- Engine: pack client (download, verify, decrypt, lock, delete), entitlement cache enforcement offline, device key stored in the OS keychain by the Rust shell and handed to the engine over the local socket, `AiClient` to the backend, the Phase 1b health checks (packs valid, sign-in state, entitlement freshness, AI reachable).
- Content tooling: `content/tools build|sign|publish` producing `.drp` packs with manifest signature and encrypted content, registering versions with the backend.
- Infrastructure: Docker Compose for the backend and Postgres on one E2E Networks VM, Caddy for TLS, staging stack, Flyway migrations, backups to R2, Sentry, Uptime Kuma.
- Engine: `AiClient` with two routes behind one interface: the backend (default for every build) and `CliModelClient` (developer mode only), chosen per call by the rule in PLAN.md 2.5: CLI present, signed in, and above `ai.cliMinRemainingPercent` of the local window budget, else backend; every call logs its route; a usage-limit or sign-in error from the CLI switches the session to the backend. The Doctor gets the developer-only CLI check from PLAN.md 6.4.
- Engine and UI: `AiClient` feeds `/v1/ai/edit` streams into the Phase 1a `EditApplier`, replacing the fake source; sign-in flow in the app, pack cards with download and lock states, the assistant panel with Ask and Write code controls, Interview and Helpful rules, "explain this hint" in Practice, written feedback on the result page including edit and hint events, counters in the header for message-equivalents and Write code calls left.

## Out of scope

Real gateway adapter, website beyond the activate and checkout pages needed here, session sync, deferred feedback for offline sessions, signing and notarization, updater, Python.

## Constraints

CLAUDE.md boundaries. The API key never leaves the backend. Prompts are read from `sections/coding-round/prompts/`. Everything that touches money or entitlements has tests covering expiry, revocation, refund and device limit. Use the `researcher` subagent with Context7 for Spring Security, the Anthropic Java SDK, Flyway and the R2 presigning API. The researcher must also answer two questions about the Claude Code CLI from its official documentation: whether any non-interactive command or output field reports the remaining usage allowance, and whether `claude -p` accepts an effort setting; if either is unavailable, keep the local window budget and the default effort, and record both answers in `docs/research/phase-1b.md`.

## Exit criteria

1. Device flow works from the app on both operating systems; sign-out everywhere kills other devices within 15 minutes.
2. A redeem code and a manual UPI order each produce an entitlement; the pack downloads, verifies, decrypts, opens offline, locks at expiry and is deleted at the next online check with history intact.
3. Ask, Write code, hint explanation and feedback stream through the backend with cache reads visible in the usage log; a Write code reply applies edits end to end, undo works, the session shows assisted after the next run; caps block the 301st message-equivalent with the right message, and a Write code call consumes three.
4. Admin page performs every listed action; no admin action is possible without the admin role.
4b. With developer mode on, the route selector is exercised for every branch (no CLI, signed out, under threshold, over threshold, CLI error mid-session, developer mode off) and the logs show the route taken; with developer mode off, no code path can reach the CLI.
5. All Phase 1a and 1b gates green, including `gitleaks`.
6. Staging and production stacks deploy from CI on merge and tag; a backup restores on staging.

## Gates

Rows marked Phase 1a and 1b in `docs/process/definition-of-done.md`.

## Deliverables

Code and tests; `docs/contracts/api-v1.yaml`, `pack-format.md`, `auth-and-delivery.md`; ADRs for the payment approach (mock now, gateway later), the key wrapping scheme, the hosting choice; runbooks for deploy, rollback, restore, rotate keys; `CHANGELOG.md`; `docs/status.md`.

## Report

As in the pipeline, plus the measured AI cost per message and per session from the usage log compared with PLAN.md 9.1.
