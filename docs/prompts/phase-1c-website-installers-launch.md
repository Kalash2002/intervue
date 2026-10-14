# Phase 1c: website, signed installers, launch

## Context to read first

`CLAUDE.md`; `docs/plan/PLAN.md` sections 1 (website versus app), 3.2 (packaging, signing, updater), 3.4 (infrastructure), 5.8 (deployment), 6.1 to 6.6 (first run, setup guides, Doctor, diagnostics), 10 (Phase 1 exit); `docs/process/*`.

## Goal

A stranger finds the website, downloads a signed installer for Windows or macOS, completes the free problem without an account, buys the launch pack through the payment page, and the app updates itself.

## In scope

- Website (React, Vite, MUI) on Cloudflare Pages: landing, pricing from `GET /v1/catalog`, setup guide pages generated from `docs/setup-guides/`, download page with OS detection and the SmartScreen and Gatekeeper notes, sign-in, activate, account with devices, store with the checkout page, redeem, admin link.
- Installers: jlink runtime build script, Tauri bundler configuration for NSIS and DMG per architecture with the WebView2 bootstrapper option, Windows signing with the IV certificate through the CA's cloud signing CLI in `bundle.windows.signCommand`, macOS signing and notarization from the Apple environment variables, the updater plugin with the `tauri signer` key pair and signed `latest.json` files on `beta` and `stable` channels on R2, version floor from the backend.
- Doctor: the remaining checks (app version supported, network and sync warnings), copy-report with redaction, Repair for a damaged runtime.
- Launch content: the launch pack of 9 problems built, signed and published; the free problem in the installer.
- Analytics events for the funnel from download to first session; Sentry in all four runtimes.
- Beta with five testers through redeem codes, then launch.

## Out of scope

Session sync, deferred feedback, website dashboard, real gateway adapter, Python, Linux.

## Constraints

CLAUDE.md boundaries. The website never holds secrets. Signing credentials live only in GitHub Environment secrets. Every release is signed with the same identity. The `researcher` subagent checks the current Tauri 2 bundler, signing, notarization and updater options, the tauri-action GitHub Action, and Cloudflare Pages deployment before configuration is written.

## Exit criteria

1. Website live on the domain with every page; Lighthouse performance above 90 on the landing page.
2. Signed Windows installer and notarized macOS DMG from a tag; both start on clean machines; the macOS build shows no Gatekeeper warning; the Windows dialog, if any, matches the download page's explanation.
3. An update from the previous version installs on both operating systems through the updater.
4. Five beta testers complete a session each and at least one buys through the payment page.
5. Time from download to first session under 5 minutes on both operating systems, measured.
6. All automated gates green including the app smoke test on both runners.

## Gates

All rows in `docs/process/definition-of-done.md`.

## Deliverables

Code; `docs/runbooks/release-app.md` with the exact signing and notarization steps; `docs/setup-guides/java.md`; launch checklist completed; `CHANGELOG.md`; `docs/status.md`; the Phase 1 retrospective in `docs/retro/phase-1.md`.

## Report

As in the pipeline, plus funnel numbers from the beta and the first sale if it happened.
