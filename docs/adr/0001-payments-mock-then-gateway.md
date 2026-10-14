# 0001: Payments: mock and manual UPI now, gateway later; launch prices and two free problems

Date: 2026-10-14. Status: accepted.

## Context

PLAN.md 8.1 puts payments behind a `PaymentPort` so the gateway is one adapter; 8.2 compares providers and none is chosen; 8.3 sets per-pack plans. Phase 0 produced no market data yet (`docs/discovery/phase-0-review.md`), so prices are a founder decision rather than a measured one. There is no CA yet (ADR 0002), which argues against taking real card payments soon.

## Decision

Phase 1b builds the checkout page against `PaymentPort` with `MockPaymentAdapter` (dev and staging) and `ManualUpiAdapter` (first real sales, confirmed in the admin page); a gateway adapter is added in a later ADR when a provider is chosen. Launch prices per pack are ₹1,500 ($19) a month and ₹3,500 ($45) a quarter, with no annual or lifetime plan, stored in the `plans` table, never in code. At launch two problems are free in every language instead of one: Hold Exposure and Corridor Router.

## Consequences

- No gateway contract, KYC or fees before there is demand; switching to a gateway touches only a new adapter.
- Manual UPI means hand-confirming each order; acceptable at Phase 1 volume, a reason to add the gateway once orders exceed a few a week.
- Two free problems: the installer bundles both (larger by a few kB), the catalog's free pointer becomes a list, and the Phase 1a content target is 2 free plus 8 in the launch pack. PLAN.md sections 1, 7.2, 10 and the Phase 0 exit metric are updated to match.
- Prices change only with a new plan version (`effectiveFrom`), as PLAN.md 8.3 requires.
