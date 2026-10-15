# 0002: Entity and GST: individual and unregistered until a CA review

Date: 2026-10-14. Status: accepted (interim; to be superseded after the CA review).

## Context

PLAN.md 8.4 lists what is needed before the first payment: entity, GST, income-tax records for foreign receipts, DPDP duties, terms and privacy policy. The questions for a CA are in `docs/discovery/ca-questions.md`. There is no CA yet. No real money is taken until Phase 1c (ADR 0001); the mock adapter takes none.

## Decision

Operate as an individual. Do not register for GST now: turnover is zero and the services threshold is ₹20 lakh. Before the first real payment, whether through manual UPI or a gateway, get a CA's answers to `ca-questions.md` and record them in an ADR that supersedes this one. Until then, keep every payout statement and bank record from day one.

## Consequences

- Nothing blocks Phase 1a or 1b engineering.
- The first real sale in Phase 1c is blocked on the CA review; plan to book a CA during Phase 1b.
- If a merchant of record is chosen, the export-of-services and LUT question decides whether registration is needed, so the gateway choice and the CA review should happen together.
- DPDP items (consent notice, account deletion, breach notice) are engineering work in Phase 1b regardless of this ADR.
