# Learning for Phase 9: industry mode, proctored assessments

Time (est.): 25 hours.

## Minimal concepts

| Concept | Why it matters in Dryrun | Source |
|---|---|---|
| Rust `unsafe` boundaries: calling C APIs through the `windows` crate and `objc2`, wrapping each call in a safe function with a stated invariant | Every proctoring signal is an OS call | The Rust book chapter 19 on unsafe; `windows` crate docs; `objc2` docs |
| Windows APIs from PLAN.md 2.7: `EnumWindows` and extended styles, `GetWindowDisplayAffinity`, `EnumDisplayMonitors`, `SM_REMOTESESSION`, process snapshots, hypervisor checks and their false positives | Detecting overlays, second screens, remote control, VMs | Microsoft Learn pages cited in PLAN.md 2.7 and `docs/plan/research/proctoring.md` |
| macOS APIs: `CGWindowListCopyWindowInfo`, screen capture permission preflight, `NSWorkspace.runningApplications`, `NSScreen.screens`, process lists for daemons | Same signals on macOS | Apple documentation pages cited in the research notes |
| Tauri window controls: content protection, kiosk and fullscreen, always on top, focus events | Lockdown and protection of question text | Tauri 2 window API docs |
| Detection from enumeration rather than screenshots; matching processes by signature not name; evidence rather than verdicts | The design that survives evasion tools | PLAN.md 2.7 |
| Consent and notice: India's DPDP Act sections 5 and 6, GDPR lawful basis in recruitment, data minimisation, retention | Monitoring candidates legally | Act text and the Rules; GDPR Recital 43 and Article 88; a lawyer before the first customer |
| Organisations, seats and invites in the backend | The B2B model | Your own backend as the model; PLAN.md 2.7 business shape |

## Tutor prompt

```
You are my tutor for native desktop monitoring APIs from Rust and for the consent rules around them. I am a backend engineer (Java, Spring Boot) with React and enough Rust for a thin Tauri 2 shell, building Dryrun. Companies will use it for proctored coding assessments; the app must detect overlay windows excluded from screen capture, second displays, remote control sessions, known helper processes and virtual machines on Windows and macOS, record evidence rather than verdicts, and obtain consent first. I have about 25 hours.

Teach me only: how to call Windows APIs from Rust through the windows crate and macOS APIs through objc2 and core-graphics inside small unsafe blocks wrapped in safe functions with stated invariants; the specific calls for window enumeration with extended styles and display affinity, monitor count, remote session flags, process snapshots and code signature checks on Windows, and window lists, screen capture permission preflight, running applications and process lists on macOS; why hypervisor checks give false positives on normal laptops; Tauri 2 window content protection, kiosk mode and focus events; designing detection from enumeration rather than screenshots, matching processes by signature, and scoring risk as weighted evidence; what India's DPDP Act requires for notice and consent and why GDPR treats consent in recruitment as weak; how to structure organisations, seats and invites in a Spring Boot backend.

Method: two-sentence explanation, one minimal example tied to Dryrun (the display-count check as the first command, an overlay detection rule), one common mistake (for example trusting process names, or sampling the UI thread), then one question to check I understood before moving on. Keep answers short. At the end give me a 30-minute exercise: the Rust function signatures and safe wrappers for the display-count and window-enumeration checks on Windows, and a checklist of what I should be able to do without notes.

Do not teach me kernel drivers, webcam analysis, or anything that bypasses operating system permissions. Flag any API whose availability depends on the OS version and tell me to verify it.
```
