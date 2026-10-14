# Phase 9: industry mode, proctored assessments for companies

## Context to read first

`CLAUDE.md`; `docs/plan/PLAN.md` section 2.7 in full (signals, detection design, build notes, legal), 10 (Phase 9 row), 12 (risk 17); `docs/plan/research/proctoring.md`.

## Goal

A company can invite a candidate to a proctored assessment in the app and receive an integrity report built from enumerated evidence, with consent recorded before monitoring starts.

## In scope

Backend: organisations, members, assessments, invites, integrity reports, company dashboard pages on the website; `desktop/src-tauri/src/proctor/` Rust modules for Windows (`windows` crate) and macOS (`objc2` and `core-graphics` bindings) exposing window enumeration with styles and capture affinity, display count, process list with code signatures, remote-session flags, behind safe wrappers; a `proctor` Tauri command and background sampler every few seconds writing `proctorWarning` and `proctorViolation` events; the `assessment` section package wrapping the coding round as an Interview-type session only (no hints, Write code allowed or disabled per the company's assessment settings, assisted flag reported), with consent screen, kiosk mode, content protection and the risk score; legal review completed before the first customer.

## Out of scope

Webcam, audio, keystroke biometrics; any detection claimed as a verdict rather than evidence.

## Constraints

Consent and notice before any monitoring, per PLAN.md 2.7; raw events in the report, never a silent verdict; hypervisor signals are warnings, not violations; process matching by signature, not name.

## Exit criteria

1. A pilot company runs an assessment end to end and receives the report.
2. Detection of a capture-excluded overlay window, a second display, and a remote session is demonstrated on Windows and macOS test machines and recorded.
3. Lawyer review recorded in `docs/legal/`.

## Gates, deliverables, report

As in the process documents; ADRs for the native addon approach and the consent model.
