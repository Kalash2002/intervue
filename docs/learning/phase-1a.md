# Learning for Phase 1a: base working app

Time (est.): 45 to 55 hours, of which the Rust and Tauri sprint is 25 to 30. Do the sprint first; it has a hard exit: a Tauri window that starts the prototype's `Server.java` and shows its dashboard.

## Minimal concepts

| Concept | Why it matters in Dryrun | Source |
|---|---|---|
| Rust: ownership, borrowing, lifetimes at the level of "why does this not compile", `String` versus `&str` | Every shell function touches paths, process handles and strings; this is where beginners stall | The Rust Programming Language, chapters 1 to 4 and 10 |
| Rust: `Result`, `Option`, the `?` operator, `thiserror` | The shell's rule is `Result` everywhere and no `unwrap` in handlers | Book chapter 9 |
| Rust: structs, enums, `match`, traits, modules and Cargo | Reading Tauri's API and organising `src-tauri/src/` | Book chapters 5 to 8 |
| Rust: `std::process::Command`, spawning and killing a child, reading its stdout | Starting the Java engine and detecting its ready line | `std::process` docs |
| Tauri 2: commands and `invoke`, events, the capabilities and permissions model, plugins (shell, dialog), `tauri.conf.json`, bundle resources, dev versus build | The entire shell is these pieces | Tauri 2 quick start, "Calling Rust from the frontend", "Capabilities", shell plugin page, bundler configuration |
| React with TypeScript: hooks, TanStack Query, React Router, controlled forms; CodeMirror 6 state, extensions, `@codemirror/autocomplete` sources, `@codemirror/lint`, decorations | Porting the three prototype views to `packages/ui` and `packages/editor`, plus autocomplete, inline diagnostics and AI-edit highlights | react.dev "Learn", TanStack Query quick start, CodeMirror 6 system guide and the autocomplete and lint examples |
| Java: the `com.sun.source` tree API to list classes and methods; applying exact-match text edits safely (hashing, atomic writes, snapshots) | The symbol extractor for completion and the AI edit applier | JDK `javax.tools` and `com.sun.source` docs; your own tests for the applier rules in PLAN.md 2.8 |
| Java: `sqlite-jdbc`, forward-only migrations, `ProcessHandle.descendants()`, `ProcessBuilder`, reading JSON lines with Jackson | The engine's store, run control and protocol parser | sqlite-jdbc README, JDK docs for `ProcessHandle`, Jackson streaming docs |
| Gradle multi-project with Kotlin DSL, Spotless, JaCoCo, ArchUnit | The gates and the layer rules | Gradle multi-project guide, ArchUnit user guide sections 1 to 4 |
| pnpm workspaces, Vite, ESLint flat config, Prettier | The TypeScript side of the monorepo | pnpm workspaces page, Vite guide, typescript-eslint getting started |

## Sprint plan (Rust and Tauri, 25 to 30 hours)

1. Hours 1 to 8: Rust book chapters 1 to 6, typing every example, plus the first ten Rustlings exercises.
2. Hours 9 to 14: chapters 7 to 10; write a command-line program that starts `java -version`, captures its output and returns a `Result`.
3. Hours 15 to 20: Tauri 2 quick start; add a command that returns the OS name; add the shell plugin and start a child process from a button.
4. Hours 21 to 30: the exit exercise: a Tauri app that starts `prototype/server/Server.java` with `--no-open --port 4499`, waits for "Dryrun is running", loads `http://127.0.0.1:4499` in the webview, and kills the process on exit.

## Tutor prompt

```
You are my tutor for Rust and Tauri 2, and for the rest of what my first engineering phase needs. I am a backend engineer (Java, Spring Boot, about two years) who knows JavaScript, React, Redux and MUI and has never written Rust. I am building Dryrun, a desktop app for AI-assisted coding-interview practice: a thin Tauri 2 shell in Rust that launches a Java engine process and shows a React UI in the system webview. I have 45 to 55 hours for this phase, about 14 a week.

Part 1, Rust and Tauri (25 to 30 hours). Teach me only: ownership and borrowing well enough to fix my own compile errors; String versus &str; Result, Option and the ? operator; structs, enums, match, traits, modules and Cargo; std::process::Command to spawn, read stdout from, and kill a child process; Tauri 2 commands and invoke, events, the capabilities and permissions model, the shell plugin, bundle resources, tauri.conf.json, dev versus build. My exit exercise is a Tauri app that starts a Java program with "java server/Server.java --no-open --port 4499", waits for the line "Dryrun is running" on stdout, loads http://127.0.0.1:4499 in the window, and kills the process when the window closes.

Part 2, the rest (20 to 25 hours). Teach me only: React hooks and TanStack Query for a three-view app (dashboard, session room, result page); CodeMirror 6 setup with read-only files, an autocomplete source fed by a symbol list, inline diagnostics through the lint extension, and temporary decorations that highlight lines an AI edit changed; the JDK com.sun.source tree API to extract class and method names from Java files; applying exact-match text edits safely in Java (content hashing, atomic writes, snapshots for undo); sqlite-jdbc with forward-only migrations in Java; ProcessBuilder and ProcessHandle.descendants() for killing a process tree; parsing JSON lines with Jackson; Gradle multi-project basics with Spotless and JaCoCo; ArchUnit rules that forbid a domain package from importing adapters; pnpm workspaces with Vite and typescript-eslint.

Method: for each concept, give a two-sentence explanation, one minimal example tied to Dryrun (the engine launcher, the session room, the run protocol parser), one common mistake, and then ask me one question to check I understood before moving on. When I paste a compile error, explain the cause in two sentences and show the smallest fix. Keep answers short. At the end of each part, give me a 30-minute exercise and a checklist of what I should be able to do without notes.

Do not teach async Rust beyond what Tauri commands need, macros, lifetimes beyond the basics, or any UI framework other than React. If an API may have changed since your training, say so and tell me to check the current docs.
```

## Checklist before running `/phase docs/prompts/phase-1a-base-app.md`

- The exit exercise app runs on your machine and the prototype dashboard appears in the Tauri window.
- You can explain why `String` and `&str` differ and when `?` can be used.
- You can add a Tauri command and call it from React.
- You have written one ArchUnit test and one Vitest test in a scratch project.
