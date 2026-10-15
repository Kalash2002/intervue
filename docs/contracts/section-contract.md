# Section contract, SPI_VERSION 1

What a section package provides: how a kind of interview round is graded, scored and shown (PLAN.md 2.6). Same versioning rule as the language contract: packages declare `spiVersion`; additive changes only without an ADR.

## Packaging

- `sections/<id>/section.json`: `id`, `name`, `version`, `spiVersion`, `runnerKind`, `graderKind`, `uiMode`, and since 2026-10-15 the optional `description` (one paragraph shown under the title of the section's page; additive, SPI version unchanged). `name` is the menu entry and page title.
- A Java implementation of `SectionPackage`, registered in `META-INF/services/si.intervue.engine.sections.SectionPackage`. The interfaces live in `si.intervue.engine.sections`.
- `schema/`: the JSON Schema for this section's `problem.json`.
- `prompts/`: `system.md`, `interview-mode.md`, `helpful-mode.md`, `write-code.md`, `hint-explain.md`, `feedback.md`. Prompt text lives only here, never in code.
- `ui/`: the pnpm package exporting one `SectionUi` (`packages/ui/src/section.ts`): `id`, `title` and `description` from `section.json`, `Room` (the session view) and `Result` (the result-page parts). The list of `SectionUi` lives in `@intervue/sections` (`packages/sections`), not in the shell, and Gradle includes and bundles every `sections/*/java` project by discovery (ADR 0008), so a new section edits neither `desktop/` nor `engine/`. It does need, besides its own folder and one line in `packages/sections/src/index.ts`, a dependency in `packages/sections/package.json` and the root `pnpm-lock.yaml`; `.github/workflows/parts.yml` has no part label that allows both `sections/` and `packages/`, and none that allows the lockfile. The app lists every `SectionUi` in that package in menu order and looks sections up by `id`; a section's problems are those whose `problem.json.section` equals it.

## Interface

```java
public interface SectionPackage {
  int SPI_VERSION = 1;
  SectionDefinition definition();
  ContentSchema schema();                         // validates a problem folder of this section
  Grader grader();                                // run results + content -> phase results
  ScoringRule scoring();                          // phase results + timeline + session type -> score
  List<ResultPart> resultParts();                 // what the result page shows, in order
  List<HealthCheck> healthChecks();
  PromptTemplates prompts();
  Set<AssistantChannel> channels();               // ASK, WRITE_CODE
  boolean supportsHints();                        // true: hints/phaseN.md required by the schema
}
```

## The coding round (Phase 1a)

| Item | Value |
|---|---|
| `runnerKind` / `graderKind` / `uiMode` | `code-tests` / `tests` / `editor` |
| `channels()` | `ASK`, `WRITE_CODE` (enabled on the developer-mode CLI route from Phase 1ab; for end users from Phase 1b) |
| `supportsHints()` | `true` |
| Interview score | Correctness 70 by phase weights (an all-or-nothing phase scores only when all its tests pass; others in proportion), time 10, rhythm 20; the prototype's formula with weights read from `problem.json` |
| Practice score | Correctness and phase results only, labelled Practice; never counts as solved |
| Assisted | An Interview session with `aiEditsActive > 0` at any run or at finish; recorded and shown as an AI edit count; changes neither the label, solved nor the best score (ADR 0006) |

## The DSA section (Phase 1ad)

| Item | Value |
|---|---|
| `id` / `name` | `dsa-quick` / "DSA" |
| `runnerKind` / `graderKind` / `uiMode` | `code-tests` / `tests` / `editor` |
| `channels()` | `ASK`, `WRITE_CODE` |
| `supportsHints()` | `true` |
| `resultParts()` | `score`, `tests`, `time`, `walkthrough` |
| Score | Correctness 80 x passed / total (timeouts and failures count as not passed); time 20 only when every test passed within its limit. Labelled Interview or Practice as in ADR 0006. Unlike the coding round's, a DSA Practice score keeps its Time part |
| Problems | one-phase, from `content/dsa/` (`docs/contracts/problem-format.md`, "One-phase problems") |
