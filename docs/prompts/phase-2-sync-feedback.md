# Phase 2: sync and deferred AI feedback

## Context to read first

`CLAUDE.md`; `docs/plan/PLAN.md` sections 2.2 (sync protocol, conflict rules, deferred feedback), 1 (offline versus online table), 10 (Phase 2 row); `docs/process/*`; `docs/contracts/`.

## Goal

Every device shows the same history and a session done offline receives AI feedback after the app reconnects. (Java IntelliSense moved to `phase-1af-java-intellisense.md`, ADR 0009.)

## In scope

Sync push and pull with the outbox and cursors, including edit and hint events and the `assisted` and `session_type` fields; `sync-contracts` versioning; deferred feedback requests and delivery by pull; device management screen in the app; pack B of 6 problems with hints to reach 16; promo code reporting; pack update flow (background download, swap at next start); Doctor copy-report polish; website activation of feedback requests is out, the app only.

## Out of scope

Website dashboard, Python, new sections, real gateway adapter unless already chosen.

## Constraints

Sessions are immutable after finish; events append-only; notes last-writer-wins. Sync must never delete local data on a failed push. Tests cover duplicate pushes, out-of-order batches, and a device that was offline for a month.

## Exit criteria

1. Two devices show identical history after both sync.
2. An offline session gets feedback within one minute of reconnecting.
3. Pack update swaps without disturbing a running session.
4. 16 problems live and verified with hints; all gates green.
5. With the flag on, Java completion, hover and diagnostics come from the language server; with it off or the server missing, tier 1 completion works and the Doctor shows the warning.

## Gates, deliverables, report

As in `docs/process/definition-of-done.md` and the pipeline; ADR for the sync protocol version 1; `docs/contracts/sync-protocol.md` finalised.
