# Learning per phase

One file per phase. Each has four parts: the minimal concepts for that phase and nothing more, why each matters in Dryrun, the official sources to read, and a tutor prompt written for your background. Paste the prompt into an AI assistant (Claude, or any model) at the start of the phase and work through it before running `/phase`.

Your background, as every prompt states it: backend engineer with about two years of Java and Spring Boot, comfortable with REST, SQL and Docker basics, knows JavaScript, React, Redux and MUI, can read TypeScript, no Rust, Python or Go yet, working part-time with 8 to 20 hours a week.

| File | Phase | Hours (est.) | Headline skills |
|---|---|---|---|
| `phase-0.md` | 0 | 6 | User interviews, pricing survey, questions for the accountant, signing enrolment, repository protection |
| `phase-1a.md` | 1a | 50 to 60 | Rust basics, Tauri 2, React with TypeScript for the UI port, CodeMirror autocomplete and lint, the javac tree API, safe text edits, SQLite in Java, process control, run protocol, architecture tests |
| `phase-1b.md` | 1b | 27 | Spring Security, device authorization flow, tokens, signatures and encryption at rest, presigned URLs, webhooks, Anthropic API with structured edit output, Compose deployment |
| `phase-1c.md` | 1c | 15 | Vite site on Cloudflare Pages, jlink, Tauri bundler, Windows signing, Apple notarization, updater keys, GitHub Actions matrix, analytics |
| `phase-2.md` | 2 | 10 | Append-only sync, outbox, idempotency, cursors, conflict tests, the Language Server Protocol for the Java plugin |
| `phase-3.md` | 3 | 10 | Dashboards with TanStack Query, gateway webhooks, containerised verification, authoring prompts |
| `phase-4.md` | 4 | 20 | Reading Python, subprocess and faulthandler, timing noise, standalone builds |
| `phase-5.md` | 5 | 12 | TypeScript as content, worker threads, type stripping limits, container cost spike |
| `phase-6.md` | 6 | 20 | Reading Go, modules offline, go vet, Linux packaging, prlimit |
| `phase-7.md` | 7 | 8 | Plugin discovery with ServiceLoader, UI registries, scoring rules, short-problem authoring |
| `phase-8.md` | 8 | 8 | Rubric design, model-as-grader with structured output, calibration on a test set |
| `phase-9.md` | 9 | 25 | Windows and macOS APIs from Rust, unsafe boundaries, content protection and kiosk, consent under DPDP and GDPR |

Rules of thumb: learn only what the phase needs, build the exercise at the end of each file, and stop when the checklist is ticked. Everything else is learned while doing the phase with the agents.
