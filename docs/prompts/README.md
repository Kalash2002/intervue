# Phase prompts

One file per roadmap phase (PLAN.md section 10), run with `/phase docs/prompts/<file>.md`. Phase 1 is split into three prompts so that a base working app exists first.

| File | Phase | Delivers |
|---|---|---|
| `phase-1a-base-app.md` | 1a | Rust and Tauri learning sprint, then the base working desktop app: engine, React UI, Tauri shell, Interview and Practice sessions, hints, the AI edit applier with undo, editor autocomplete and diagnostics, 10 problems from a local content folder, no account, no backend |
| `phase-1b-accounts-packs-ai.md` | 1b | Backend, device sign-in, catalog and plans, entitlements, redeem codes, payment page with mock and manual UPI adapters, encrypted packs, AI gateway with Ask, Write code and hint explanation |
| `phase-1c-website-installers-launch.md` | 1c | Website, signed installers for Windows and macOS, updater, Doctor, beta and launch |
| `phase-2-sync-feedback.md` | 2 | Session sync, deferred AI feedback, device management, pack B, promo reporting, Java language-server plugin |
| `phase-3-dashboard-content.md` | 3 | Website dashboard, catalogue to 36, authoring workbench, gateway adapter if still pending |
| `phase-4-python.md` | 4 | Python language package and full port |
| `phase-5-typescript.md` | 5 | JavaScript/TypeScript package and port; website-practice decision |
| `phase-6-go-linux.md` | 6 | Go package and port; Linux builds |
| `phase-7-dsa-section.md` | 7 | DSA quick section; catalogue to 60 |
| `phase-8-system-design.md` | 8 | System design section |
| `phase-9-industry-mode.md` | 9 | Proctored assessments for companies |

Every prompt has the same sections: Context to read, Goal, In scope, Out of scope, Constraints, Exit criteria, Gates, Deliverables, Report. Edit a prompt before running it if the plan has changed; the prompt is the contract for that run. Before each phase, do the learning in `docs/learning/<phase>.md`; its tutor prompt is written for your background.
