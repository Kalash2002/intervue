# Phase 4: Python language package and full port

## Context to read first

`CLAUDE.md`; `docs/plan/PLAN.md` sections 4.1, 4.2 (Python column), 4.3, 4.4, 4.6 (checklist), 6.2 (Python setup guide), 6.3 (downloaded toolchain policy), 10 (Phase 4 row); `docs/contracts/language-contract.md`; `languages/java/` as the reference implementation.

## Goal

Every problem in the catalogue has a verified Python variant, the Python package passes the health checks on Windows and macOS, and the language ships dark behind a flag before general release.

## In scope

`languages/python/`: `language.json` (python-build-standalone 3.14 downloads with hashes), runner (compileall then the kit runner), test kit with child process per test and `faulthandler` backstop, calibration benchmark, setup guide, editor package with a symbol extractor and `lsp.json` for pyright or jedi-language-server as the optional plugin; `variants/python/` for every problem with starter, reference, naive and data; hints are language-neutral and already exist; verify green for all; native reviewer sign-off recorded; flag rollout to testers then everyone.

## Out of scope

Any change under `engine/`, `desktop/`, `web/`, `backend/`, `shared-java/`, `sections/`. CI fails the PR if touched.

## Constraints

Standard library only in content; Python-specific headroom of 6x; timing noise measured on both operating systems before limits are finalised.

## Exit criteria

1. Three-way verification green for the whole catalogue on Windows and macOS CI.
2. Doctor passes for Python on both operating systems from a clean machine with the downloaded toolchain and with a user-installed Python.
3. The run protocol parser needs no change.
4. Reviewer sign-off; flag at 100%.

## Gates, deliverables, report

As in the process documents; ADR recording the toolchain policy for Python.
