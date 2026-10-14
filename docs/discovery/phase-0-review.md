# Phase 0 review

Date: 2026-10-14. Covers PLAN.md section 13 so far. Sources: live waitlist KV, `docs/discovery/` (local notes), the founder's pricing decision. No numbers below are estimated; where there is no data, it says so.

## Results against the exit criteria

| Exit criterion (PLAN.md section 10) | Target | Actual | Met |
|---|---|---|---|
| Waitlist sign-ups | 25 | 1, created right after the first deploy and likely a test entry | No |
| Explicit "would pay" | 5 | 0 from real users (the one entry picked ₹1,500 a month) | No |
| Testers completed a prototype session, with notes | 3 | 0 | No |
| User interviews | 10 | 0 (script ready) | No |
| Pricing survey responses | n/a | 0 (survey ready, self-serve) | No |

What is ready: landing page live at https://intervue-web.pages.dev with waitlist and PostHog; prototype zip with RUN.md, all three problems verified (starter fails, reference passes, naive times out in phase 3); interview script, pricing survey and CA questions written.

## Price sensitivity

No market data yet. Prices are a founder decision, consistent with PLAN.md 8.3: **₹1,500 a month ($19)** and **₹3,500 a quarter ($45)**. The survey asks both prices plus a price ceiling in bands, so the first 10 to 20 responses will show whether ₹1,500 is above most people's ceiling.

The old exit metric was "would pay at ₹499". It no longer matches the price, so it becomes **5 "Definitely" or "Probably" answers at ₹1,500 a month or ₹3,500 a quarter**.

## Tester findings

None yet: no outside tester has run the prototype. Internal checks only: the zip unzips and runs on Windows with JDK 21, `sh run.sh` starts the server, `Verify` passes. Known friction: `run.sh` is not executable after unzipping (RUN.md says `sh run.sh`), and the prototype needs a JDK, which limits testers to engineers who have one.

## Decisions taken

1. Prices: ₹1,500 monthly, ₹3,500 quarterly per pack (ADR 0001).
2. Launch offer: **two free problems** instead of one. The pair is Hold Exposure (easy) and Corridor Router (medium), both already verified; confirmed by the founder on 2026-10-14.
3. Payments: mock and manual UPI first, gateway later (ADR 0001).
4. Entity and GST: individual, no GST registration for now, CA review before the first real payment (ADR 0002).

## Recommendation: go, with adjustments

Start Phase 1a, but keep validating in parallel and hold a checkpoint.

- **Why go:** the first 2 to 3 weeks of Phase 1a are the Rust and Tauri learning sprint, which is needed whatever the demand turns out to be. Waiting for data first would only delay it.
- **Why adjust:** none of the demand evidence exists yet, so the expensive parts (engine rewrite, 7 new problems) should wait for a checkpoint.
- **Phase 1a scope change:** 10 problems as **2 free plus a launch pack of 8** (was 1 free plus 9).
- **Keep running during the sprint:** post the survey link and recruiting message, aim for 10 interviews and 3 testers.
- **Checkpoint at the end of the learning sprint (about 2026-11-19):** at least 25 sign-ups or 5 "would pay" at the new prices, and 3 tester sessions. If not met, adjust before the engine rewrite: talk to different users, or test a lower price.
