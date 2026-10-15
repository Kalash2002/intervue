# Phase prompts

One file per roadmap phase (PLAN.md section 10), run with `/phase docs/prompts/<file>.md`. Phase 1 is split so that a base working app exists first. Since 2026-10-15 the first product is Java only on Windows (ADR 0007, written by Phase 1ac): the DSA section and the catalogue growth come right after the shell, and the other languages and Linux follow the sections. File names keep their original phase numbers; the table gives the order.

| File | Phase | Delivers |
|---|---|---|
| `phase-1a-base-app.md` | 1a | Rust and Tauri learning sprint, then the base working desktop app: engine, React UI, Tauri shell, Interview and Practice sessions, hints, the AI edit applier with undo, editor autocomplete and diagnostics, 10 problems from a local content folder, no account, no backend |
| `phase-1ac-shell-theme-tags.md` | 1ac | Home dashboard and a Practice group in a fixed left menu, one section page with a filter bar and grouped list (`docs/design/home-and-practice.md`), full-width pages, the dark grey and purple palette from `docs/design/palette.md`, problem tags on cards, set-up, answer and result pages, `dsa://` chips in walkthroughs, and the Java and Windows first re-plan (ADR 0007) |
| `phase-1ad-dsa-section.md` | 1ad | DSA section: NeetCode 150 questions in Java under `content/dsa/`, listing by topic group, solving page, cross-links to the coding-round problems |
| `phase-1ae-catalogue-java.md` | 1ae | Scenario problems written in NeetCode roadmap order from `content/dsa/NEETCODE-150.md`, each covering the next two or three questions of a group until all 150 are covered (about 50 problems); the first 24 are the launch catalogue |
| `phase-1b-accounts-packs-ai.md` | 1b | Backend, device sign-in, catalog and plans, entitlements, redeem codes, payment page with mock and manual UPI adapters, encrypted packs, AI gateway with Ask, Write code and hint explanation |
| `phase-1c-website-installers-launch.md` | 1c | Website, signed installers for Windows and macOS, updater, Doctor, beta and launch |
| `phase-2-sync-feedback.md` | 2 | Session sync, deferred AI feedback, device management, pack B, promo reporting, Java language-server plugin |
| `phase-3-dashboard-content.md` | 3 | Website dashboard, catalogue to 36, authoring workbench, gateway adapter if still pending |
| `phase-7-dsa-section.md` | 4 (was 7) | Catalogue to 60; the DSA section itself moved to 1ad, so this prompt shrinks to the remaining coding-round problems when it is next edited |
| `phase-8-system-design.md` | 5 (was 8) | System design section |
| `phase-4-python.md` | 6 (was 4) | Python language package and full port |
| `phase-5-typescript.md` | 7 (was 5) | JavaScript/TypeScript package and port; website-practice decision |
| `phase-6-go-linux.md` | 8 (was 6) | Go package and port; Linux builds |
| `phase-9-industry-mode.md` | 9 | Proctored assessments for companies |

Every prompt has the same sections: Context to read, Goal, In scope, Out of scope, Constraints, Exit criteria, Gates, Deliverables, Report. Edit a prompt before running it if the plan has changed; the prompt is the contract for that run. Before each phase, do the learning in `docs/learning/<phase>.md`; its tutor prompt is written for your background.
