# Phase prompts

One file per roadmap phase (PLAN.md section 10), run with `/phase docs/prompts/<file>.md`. Phase 1 is split so that a base working app exists first. Since 2026-10-15 the first product is Java only on Windows (ADR 0007), and since 2026-10-16 the base product comes first (ADR 0009, 0010): after the catalogue come IntelliSense and the catalogue to 60, then the website and installers, then the system design section, then the backend with accounts, packs, payment, the AI gateway and admin on mock adapters, then sync and the website dashboard, then one phase for everything that needs a credential (the real payment gateway, the provider key, signing: ADR 0011), and the other languages last. File names keep their original phase numbers; the table gives the order.

| File | Phase | Delivers |
|---|---|---|
| `phase-1a-base-app.md` | 1a | Rust and Tauri learning sprint, then the base working desktop app: engine, React UI, Tauri shell, Interview and Practice sessions, hints, the AI edit applier with undo, editor autocomplete and diagnostics, 10 problems from a local content folder, no account, no backend |
| `phase-1ac-shell-theme-tags.md` | 1ac | Home dashboard and a Practice group in a fixed left menu, one section page with a filter bar and grouped list (`docs/design/home-and-practice.md`), full-width pages, the dark grey and purple palette from `docs/design/palette.md`, problem tags on cards, set-up, answer and result pages, `dsa://` chips in walkthroughs, and the Java and Windows first re-plan (ADR 0007) |
| `phase-1ad-dsa-section.md` | 1ad | DSA section: NeetCode 150 questions in Java under `content/dsa/`, listing by topic group, solving page, cross-links to the coding-round problems |
| `phase-1ae-catalogue-java.md` | 1ae | Scenario problems written in NeetCode roadmap order from `content/dsa/NEETCODE-150.md`, each covering the next two or three questions of a group until all 150 are covered (about 50 problems); the first 24 are the launch catalogue |
| `phase-1ad-fixes-dsa.md` | 1ad fixes | The DSA hand-test fixes: no assistant in DSA rooms but hints in both session types, LeetCode-shaped statements, answers from brute force through better to optimal with a figure, `java.util` imports in starters and auto-import on completion |
| `phase-1af-java-intellisense.md` | 1af | The Eclipse JDT language server as a downloadable plugin: completion with signatures and auto-import, hover, diagnostics, go-to-definition; built-in completion as the fallback |
| `phase-7-dsa-section.md` | 2 (was 7) | Catalogue to 60, content only |
| `phase-1c-website-installers-launch.md` | 3 (was 1c) | Website (landing, set-up guides, download), unsigned Windows installer, updater, Doctor, five testers; the store, sign-in and the paid pack move to Phase 5 (edit the prompt before running it) |
| `phase-8-system-design.md` | 4 (was 8) | System design section, no backend |
| `phase-1b-accounts-packs-ai.md` | 5 (was 1b; edit it first to drop the real gateway adapter and the provider key, ADR 0011) | Backend, device sign-in, catalog and plans, entitlements, redeem codes, payment page with mock and manual UPI adapters, encrypted packs, AI gateway with Ask, Write code and hint explanation |
| `phase-2-sync-feedback.md` | 6 (was 2) | Session sync, deferred AI feedback, device management, pack B, promo reporting (the language server moved to 1af) |
| `phase-3-dashboard-content.md` | 7 (was 3) | Website dashboard, catalogue to 36, authoring workbench, gateway adapter if still pending |
| `phase-8-credentials.md` | 8 | To be written when Phase 8 is near: the real payment gateway adapter, the provider key on the AI gateway, Windows signing and macOS notarization, the CA review (ADR 0011) |
| `phase-4-python.md` | 9 (was 4) | Python language package and full port |
| `phase-5-typescript.md` | 10 (was 5) | JavaScript/TypeScript package and port; website-practice decision |
| `phase-6-go-linux.md` | 11 (was 6) | Go package and port; Linux builds |
| `phase-9-industry-mode.md` | 12 | Proctored assessments for companies |

Every prompt has the same sections: Context to read, Goal, In scope, Out of scope, Constraints, Exit criteria, Gates, Deliverables, Report. Edit a prompt before running it if the plan has changed; the prompt is the contract for that run. Before each phase, do the learning in `docs/learning/<phase>.md`; its tutor prompt is written for your background.
