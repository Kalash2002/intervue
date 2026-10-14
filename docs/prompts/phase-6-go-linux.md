# Phase 6: Go package, port, Linux builds

## Context to read first

`CLAUDE.md`; `docs/plan/PLAN.md` sections 4.2 (Go column), 4.6, 5.11 (the Go worked example), 6.1 (Linux first run), 10 (Phase 6 row); `docs/contracts/language-contract.md`.

## Goal

Every problem has a verified Go variant; the app ships unsigned Linux builds with Linux health checks.

## In scope

`languages/go/` with offline module mode, pre-warmed build cache, fork-per-test kit runner, `gofmt` formatting, calibration, setup guide, editor with `lsp.json` for gopls as the optional plugin; `variants/go/` for the catalogue; Tauri bundler targets AppImage and deb; Linux checks in the shell's health framework (write access, port, `prlimit`); Linux CI runner for verification.

## Out of scope

Signing on Linux; engine or backend changes beyond adding the Linux OS entry to the support matrix data file.

## Constraints

Content passes `go vet`; `GOTOOLCHAIN=local`, `GOPROXY=off`; standard library only.

## Exit criteria

1. Verification green for the catalogue on Windows, macOS and Linux CI.
2. Doctor passes for Go from clean machines on all three.
3. Linux AppImage and deb start on Ubuntu LTS and Fedora current.

## Gates, deliverables, report

As in the process documents.
