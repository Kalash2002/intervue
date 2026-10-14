# Learning for Phase 1b: accounts, packs, payment page, AI gateway

Time (est.): 25 hours.

## Minimal concepts

| Concept | Why it matters in Dryrun | Source |
|---|---|---|
| Spring Security 6: filter chain, stateless JWT resource server, session cookies for the website, roles | Backend auth for app and website; the admin role | Spring Security reference, "Servlet Applications" overview and the OAuth2 resource server JWT section |
| OAuth 2.0 device authorization grant | How the app signs in through the browser without a password field | RFC 8628, sections 3.1 to 3.5 |
| Access tokens of 15 minutes, rotating refresh tokens, reuse detection, token generation for sign-out everywhere | PLAN.md 2.3 token design | OWASP cheat sheets for JSON Web Tokens and session management |
| Ed25519 signatures, AES-256-GCM, X25519 key agreement, as library calls not mathematics | Signed manifests and entitlement blobs, encrypted packs, keys wrapped per device | Java `Signature` with EdDSA, `Cipher` AES/GCM/NoPadding, `KeyAgreement` X25519 in the JDK docs |
| OS keychain from Rust and passing a secret to a child process over a local socket | The device secret never touches disk in plaintext | The keychain crate's README; Tauri shell plugin docs |
| S3-compatible presigned URLs with Cloudflare R2 | Short-lived pack downloads | Cloudflare R2 presigned URL docs; AWS SDK for Java v2 presigner |
| Webhooks: signature verification, idempotency keys, replay safety | Future gateway adapter and the manual UPI confirmation path | Any gateway's webhook docs plus PLAN.md 8.1 |
| Hexagonal architecture in Spring: ports in `domain` and `application`, adapters outside, tested with ArchUnit | The backend layout | PLAN.md 5.3; ArchUnit layered architecture rule |
| Anthropic Messages API through the Java SDK: streaming, system prompt, prompt caching with cache_control, usage fields, effort, refusal stop reason | The AI gateway and its cost log | Anthropic docs: Messages, streaming, prompt caching; the Java SDK README |
| Getting reliable structured output for edits: a strict JSON-lines format described in the prompt, server-side validation, one retry on malformed output, size caps | The Write code channel must emit edits the engine can apply | Anthropic docs on structured outputs and prompting for consistent formats; PLAN.md 2.8 edit protocol |
| Docker Compose on a VM, Caddy for TLS, Flyway migrations, `pg_dump` backups and restore | Hosting on E2E Networks | Compose docs, Caddy getting started, Flyway quick start |

## Tutor prompt

```
You are my tutor for the backend and security concepts of my second engineering phase. I am a backend engineer (Java, Spring Boot, about two years, REST and SQL, Docker basics) building Dryrun, a desktop app with a Spring Boot backend. In this phase the app signs in through a device authorization flow, downloads encrypted content packs with keys wrapped per device, buys through a payment page with a pluggable gateway port, and calls the Anthropic API through my backend with per-plan caps. I have about 25 hours.

Teach me only: Spring Security 6 for a stateless JWT API plus cookie sessions for a website and an admin role; the OAuth 2.0 device authorization grant (RFC 8628) and how I would implement it with 15-minute access tokens and rotating refresh tokens with reuse detection; Ed25519 signing, AES-256-GCM and X25519 key wrapping as JDK library calls, with the mistakes that break them (nonce reuse, unauthenticated data); storing a secret in the OS keychain from Rust and passing it to a child process over a local socket; presigned URLs for Cloudflare R2 with the AWS SDK for Java; webhook signature verification and idempotency; hexagonal layering in Spring with ports and adapters; the Anthropic Messages API from Java with streaming, prompt caching order and usage accounting; getting a model to return code edits in a strict JSON-lines format that my server validates, with one retry on malformed output and a size cap; Docker Compose with Caddy and Flyway on one VM, with backup and restore.

Method: two-sentence explanation, one minimal example tied to Dryrun (the device flow for the desktop app, the pack key wrapping, the chat endpoint), one common mistake, then one question to check I understood before moving on. Keep answers short and correct me with reasons. At the end give me a 30-minute exercise: a sequence diagram of my device flow with token lifetimes, and a checklist of what I should be able to do without notes.

Do not teach me OAuth flows other than device authorization, cryptography mathematics, Kubernetes, or any payment gateway's specific API. If an SDK method may have changed, say so and tell me to check the current docs.
```
