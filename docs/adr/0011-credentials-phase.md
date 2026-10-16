# 0011: everything that needs a credential is one phase after the dashboard

Date: 2026-10-16. Status: accepted; refines 0010.

## Context

Phase 5 in ADR 0010 mixed work that can be built and tested with no outside dependency (device sign-in, admin, packs and entitlements, redeem codes, the payment page on the mock and manual UPI adapters of ADR 0001, the AI gateway's endpoints and caps against a recorded model client) with work that is blocked until the owner obtains something: the real payment gateway adapter, the AI provider key on the gateway, the Windows signing certificate and macOS notarization. The blocked work would stall the whole phase.

## Decision

Phase 5 keeps only what needs no credential. The credential work becomes Phase 8, after sync (6) and the website dashboard (7): the real payment gateway adapter behind `PaymentPort`, the provider key on the AI gateway with the developer route demoted to development only, Windows signing through `signCommand` and macOS notarization on CI. Until Phase 8, paid content is sold by redeem codes and manual UPI confirmed by the owner, the assistant for end users runs on the gateway only once a key exists, and installers ship unsigned. The other languages become Phases 9 to 11, industry mode 12.

## Consequences

- `phase-1b-accounts-packs-ai.md` is edited before it runs to exclude the real gateway adapter and any provider key; `phase-1c-website-installers-launch.md` already ships unsigned.
- A new prompt `phase-8-credentials.md` is written when Phase 8 is near; its exit criteria are the ones removed from 1b and 1c (a stranger pays through the gateway, signed installer and notarized `.dmg`, the assistant for a signed-in user on the provider key).
- PLAN.md section 10 and `docs/prompts/README.md` carry the order.
