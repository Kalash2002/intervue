# Learning for Phase 1c: website, signed installers, launch

Time (est.): 15 hours.

## Minimal concepts

| Concept | Why it matters in Intervue | Source |
|---|---|---|
| Vite React site deployed on Cloudflare Pages with preview deployments | The website | Cloudflare Pages framework guide for Vite |
| jlink: modules, `--add-modules`, `--strip-debug`, `--compress`, platform-specific images | The bundled Java runtime inside the installer | JDK jlink man page |
| Tauri 2 bundler: targets NSIS and DMG, `bundle.resources`, WebView2 bootstrapper option, per-arch builds | Producing the installers | Tauri 2 "Distribute" section, Windows installer and macOS bundle pages |
| Windows code signing: what a certificate proves, why the key must be in hardware or a cloud HSM, SmartScreen reputation, `signCommand` | The Windows installer without scary dialogs over time | PLAN.md 3.2; Microsoft SmartScreen reputation page; your CA's cloud signing CLI docs; Tauri Windows signing page |
| Apple: Developer ID certificate, Hardened Runtime, notarization with `notarytool`, stapling, environment variables Tauri reads | The macOS installer opens without a Gatekeeper block | Apple "Notarizing macOS software before distribution"; Tauri macOS signing page |
| Tauri updater: `tauri signer generate`, `latest.json` shape per platform, signatures, channels | Self-update from R2 | Tauri updater plugin page |
| GitHub Actions: matrix over Windows and macOS runners, environment secrets, caching Rust and Gradle, releasing on tags | CI that builds, signs and publishes | GitHub Actions docs: workflows, matrix, encrypted secrets, caching |
| Funnel analytics and error tracking | Knowing where testers drop off and what crashes | PostHog product analytics quick start; Sentry Java, Rust and browser SDK quick starts |

## Tutor prompt

```
You are my tutor for shipping a signed desktop app and a small website. I am a backend engineer (Java, Spring Boot) who knows React, has just learned enough Rust for a thin Tauri 2 shell, and is launching Intervue: a Tauri app bundling a jlink Java runtime, with a Vite React website on Cloudflare Pages. I have about 15 hours.

Teach me only: deploying a Vite React site on Cloudflare Pages with preview deployments; building a jlink runtime image that includes jdk.compiler and keeping it small; configuring the Tauri 2 bundler for NSIS and DMG per architecture with bundle resources and the WebView2 bootstrapper; Windows code signing with an individual-validation certificate whose key lives in a cloud HSM, driven from CI through Tauri's signCommand, and how SmartScreen reputation builds; Apple Developer ID signing, Hardened Runtime, notarization and stapling through Tauri's environment variables on a GitHub macOS runner; the Tauri updater plugin with its signing key pair and the latest.json file per platform on a static host; a GitHub Actions workflow with a Windows and macOS matrix, environment secrets, Rust and Gradle caching, and a release on tag; a product analytics funnel from download to first session; Sentry in Java, Rust and the browser.

Method: two-sentence explanation, one minimal example tied to Intervue, one common mistake (for example signing with different identities, or forgetting to staple), then one question to check I understood before moving on. Keep answers short and correct me with reasons. At the end give me a 30-minute exercise: a written release runbook for one tag from commit to users on both operating systems, and a checklist of what I should be able to do without notes.

Do not teach me App Store or Microsoft Store distribution, Linux packaging, or marketing. Prices and policies change: when one matters, tell me to check the official page.
```
