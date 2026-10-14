# Phase 3: website dashboard, catalogue to 36, authoring workbench

## Context to read first

`CLAUDE.md`; `docs/plan/PLAN.md` sections 1, 7.3 (coverage map), 7.4 (authoring pipeline and workbench), 8.2 (gateway choice), 10 (Phase 3 row); `docs/process/*`.

## Goal

The website shows a user's history, scores and feedback; the catalogue reaches 36 problems; new problems are produced through the authoring workbench; the chosen payment gateway adapter is live if not already.

## In scope

Read-only dashboard on the website from synced data; feedback requests from the website; all-access bundle plan; the gateway adapter behind `PaymentPort` with its webhook; the authoring workbench in the admin area (pick a coverage cell, draft with the authoring prompt, show the folder diff, run verify in a container, commit to a branch, open the PR); 21 new problems across every NeetCode group from the coverage map.

## Out of scope

Website practice, new languages, new sections.

## Constraints

Content isolation rule enforced by CI; every new problem verified three ways; the workbench writes folders only.

## Exit criteria

1. Dashboard matches the app for the same account.
2. 36 problems verified and published across packs and bundle.
3. Workbench produces one problem end to end with a human approval step.
4. Gateway adapter processes a real test-mode order and refund.

## Gates, deliverables, report

As in the process documents; ADR for the gateway chosen.
