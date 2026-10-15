# 0007: Java and Windows first; DSA and the catalogue before accounts

Date: 2026-10-15. Status: accepted.

## Context

The roadmap in PLAN.md section 10 ported the catalogue to Python, TypeScript and Go (Phases 4 to 6) before the DSA section (Phase 7), and Phase 1c shipped signed installers for Windows and macOS. After the Phase 1ab hand test the owner changed the launch: one language, Java; a DSA section with the NeetCode 150 questions inside the app so that walkthroughs stop sending users to other sites; a larger launch catalogue whose problems each exercise two or three of those questions; a Home dashboard and a side menu (`docs/design/home-and-practice.md`). Signing certificates, an AI API key and a macOS machine are not available yet (PLAN.md 12, risks on signing and notarization).

## Decision

The first product ships with Java only, on Windows. Phases 1ac (shell, theme, tags), 1ad (DSA section) and 1ae (catalogue to 24 Java problems) run before 1b. Python, TypeScript, Go and Linux move after the sections (new Phases 6 to 8). macOS stays in the CI matrix on every push, marked allowed-to-fail, and is hand-tested when a machine is available; the `.dmg` is not a launch gate. Phase 1b keeps the developer route as its only AI route until the API key exists; Phase 1c ships an unsigned Windows installer first and adds signing when the certificate arrives.

## Consequences

- PLAN.md sections 1, 7.3 and 10 and `docs/prompts/README.md` follow this order; the language picker stays hidden while one language exists.
- Phase 1c's exit criteria lose "on both OSes"; the macOS and signing criteria move to the phase in which the certificate and the machine arrive.
- The content isolation rule and pack format are unchanged; the DSA set adds a second content root (`content/dsa/`) in Phase 1ad, under the same problem format with one phase.
- Economics in section 9 are unchanged: every language was already marginal cost, not launch scope.
