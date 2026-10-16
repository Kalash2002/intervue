# 0010: base product, then website and installers, then system design, then the backend

Date: 2026-10-16. Status: accepted; supersedes the order in 0009 (same day, before any phase ran under it).

## Context

ADR 0009 completed the Java system before accounts but still left the website and installers behind accounts and payment. The owner's order is: a properly working app first, then the website and the installer so people can get it, then the system design section, and only then the backend with admin and authentication, the payment gateways and the API key, which are not available yet.

## Decision

After the base product (1a to 1af and the catalogue to 60) come: the website and installers (Phase 3, unsigned Windows installer, the updater on the app's own key, website landing, download and set-up guides, no store and no sign-in), the system design section (Phase 4), then accounts, packs, the payment page and the AI gateway with admin and authentication (Phase 5). Session sync and deferred feedback (Phase 6) and the website dashboard (Phase 7) come right after Phase 5 because both read from that backend; they cannot precede it. Python, TypeScript, Go and Linux are Phases 8 to 10, industry mode 11.

## Consequences

- Phase 3 is the old 1c without the store, sign-in, account pages and the paid launch pack; those move into Phase 5, which becomes the launch of paid content.
- Until Phase 5 the installer ships the free problems and the developer-route assistant only; the Doctor and the updater are complete, so a tester can install and stay current.
- PLAN.md section 10, `docs/prompts/README.md` and `PROMPTS.md` carry this order; prompt files keep their names, and `phase-1c-website-installers-launch.md` is edited before it runs to drop the store and sign-in.
