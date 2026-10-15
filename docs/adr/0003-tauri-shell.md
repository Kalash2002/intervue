# 0003: Tauri 2 desktop shell, engine started from Rust

Date: 2026-10-14. Status: accepted.

## Context

PLAN.md 3.3 chose Tauri 2 over Electron for the desktop shell: a small download, the system webview, and a signing and updater story that fits Windows and macOS. The shell must stay thin (CLAUDE.md): it starts the Java engine, hands the UI the engine's port and token, and holds no domain logic. Phase 1a research (Tauri 2.12.3) found that capabilities restrict only what the frontend may call, and that the shell plugin's kill-on-exit covers only processes spawned from JavaScript.

## Decision

The shell is a Tauri 2 app in `desktop/`. Its Rust code starts the engine with `std::process::Command` (`runtime/bin/java -jar engine.jar` from the bundled resources), creates the per-launch token and passes it in `INTERVUE_ENGINE_TOKEN`, waits for the engine's ready line (which carries only the port), and gives port and token to the UI through one `invoke` command. It keeps the child handle and kills the engine on `RunEvent::Exit`. The React UI from `packages/ui` runs unchanged in the webview. Capabilities grant the frontend nothing beyond the core defaults and that command.

### Deviation from the Phase 1a prompt

The prompt says the engine is started "through the shell plugin". It is started from Rust with `std::process::Command` instead, because spawning from Rust needs no frontend permission, so the attack surface is smaller, and because the plugin's automatic cleanup would not cover a Rust-spawned child anyway. The shell plugin is not added.

## Consequences

- No shell permission in the capabilities file; the frontend cannot start processes.
- The shell must kill the engine on every exit path; a test covers the launcher's argument and environment building, and the exit path is checked by hand on both OSes.
- If the Rust learning curve stalls Phase 1a by more than a week, PLAN.md 3.3's Electron fallback still applies.
