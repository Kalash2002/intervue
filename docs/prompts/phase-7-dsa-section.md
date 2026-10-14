# Phase 7: DSA quick section and catalogue to 60

## Context to read first

`CLAUDE.md`; `docs/plan/PLAN.md` sections 2.6 (section interfaces and the DSA row), 5.11 (the section worked example), 7.3 (remaining coverage), 10 (Phase 7 row); `docs/contracts/section-contract.md`; `sections/coding-round/` as the reference.

## Goal

A second section opens without any shell change, and the coding-round catalogue reaches 60 problems in all four languages.

## In scope

`sections/dsa-quick/`: `section.json`, schema for single-phase problems, tests grader reuse through the SPI, scoring rule correctness 80 and time 20, session UI without a file tree, result parts; a DSA pack of 30 short problems in four languages; 24 more coding-round problems ported to four languages.

## Out of scope

Changes under `engine/`, `desktop/`, `web/`, `backend/`; CI fails the PR if touched.

## Constraints

Section discovery through `ServiceLoader` and the UI glob only. Content isolation rule. Porting cost is tracked per problem in `docs/retro/phase-7.md`.

## Exit criteria

1. The section appears in navigation, dashboard, store and filters with zero shell edits (CI path check proves it).
2. 60 coding problems and 30 DSA problems verified in four languages.
3. A full DSA session works offline with score and walkthrough.

## Gates, deliverables, report

As in the process documents; ADR if the section SPI needed an additive field.
