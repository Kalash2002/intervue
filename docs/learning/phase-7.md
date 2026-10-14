# Learning for Phase 7: DSA quick section and catalogue to 60

Time (est.): 8 hours.

## Minimal concepts

| Concept | Why it matters in Dryrun | Source |
|---|---|---|
| Java `ServiceLoader` and `META-INF/services` for plugin discovery | A new section registers itself with no engine edit | JDK `ServiceLoader` docs |
| Glob imports in Vite (`import.meta.glob`) for UI registries | A new section's React package appears in navigation with no shell edit | Vite features page, glob import |
| Designing a scoring rule as a pure function with a golden-file test | Correctness 80 and time 20 for single-phase problems | PLAN.md 2.6; your coding-round scoring tests as the model |
| Authoring short problems: one technique, one planted bug, one scale test, 20 minutes | A DSA pack of 30 | PLAN.md 7.4 |
| Tracking porting cost per problem | Deciding how much content the four-language rule can afford | A simple spreadsheet from the phase retrospective |

## Tutor prompt

```
You are my tutor for plugin-style extension in a Java and React codebase. I am a backend engineer (Java, Spring Boot) who knows React, building Dryrun, where a new "section" (a content type with its own grader, scoring rule and session UI) must appear in the app by adding one folder, never by editing the engine or the shell. I have about 8 hours.

Teach me only: Java ServiceLoader with META-INF/services and how a Gradle build includes every sections/*/grader subproject automatically; Vite's import.meta.glob to build a React registry of section UI packages; writing a scoring rule as a pure function with golden-file tests; the failure modes of plugin systems (two plugins with the same id, version skew between a plugin and its SPI); how to author a 20-minute single-phase problem with one planted bug and one scale test; how to track porting cost per problem so content volume stays affordable.

Method: two-sentence explanation, one minimal example tied to Dryrun (the coding-round section as the reference, the new DSA quick section as the exercise), one common mistake, then one question to check I understood before moving on. Keep answers short. At the end give me a 30-minute exercise: the section.json and the registration files for the DSA section without touching any shell file, and a checklist of what I should be able to do without notes.

Do not teach me OSGi, dynamic class loading at runtime, or micro-frontends.
```
