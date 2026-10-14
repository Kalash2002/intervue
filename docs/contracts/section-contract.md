# Section contract, SPI_VERSION 1

What a section package provides: how a kind of interview round is graded, scored and shown (PLAN.md 2.6). Same versioning rule as the language contract: packages declare `spiVersion`; additive changes only without an ADR.

## Packaging

- `sections/<id>/section.json`: `id`, `name`, `version`, `spiVersion`, `runnerKind`, `graderKind`, `uiMode`.
- A Java implementation of `SectionPackage`, registered in `META-INF/services/si.intervue.engine.sections.SectionPackage`. The interfaces live in `si.intervue.engine.sections`.
- `schema/`: the JSON Schema for this section's `problem.json`.
- `prompts/`: `system.md`, `interview-mode.md`, `helpful-mode.md`, `write-code.md`, `hint-explain.md`, `feedback.md`. Prompt text lives only here, never in code.
- `ui/`: the pnpm package with the session view and result-page parts.

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
| `channels()` | `ASK`, `WRITE_CODE` (disabled in the UI until Phase 1b) |
| `supportsHints()` | `true` |
| Interview score | Correctness 70 by phase weights (an all-or-nothing phase scores only when all its tests pass; others in proportion), time 10, rhythm 20; the prototype's formula with weights read from `problem.json` |
| Practice score | Correctness and phase results only, labelled Practice; never counts as solved |
| Assisted | An Interview session with `aiEditsActive > 0` at any run or at finish; labelled "Interview, assisted"; listed with its own best and never counted as solved |
