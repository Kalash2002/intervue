# 0005: Run protocol v1, results in a JSON-lines file

Date: 2026-10-14. Status: accepted.

## Context

The prototype's test runner prints results to stdout between markers, so a candidate's own `System.out` can corrupt grading, and the format is Java-specific. PLAN.md 4.3 needs one format for every language.

## Decision

Every language kit writes results to `{outDir}/results.jsonl` using `docs/contracts/run-protocol.md` version 1. The first line declares `"protocol":1`. User output streams separately and is never parsed. `shared-java/run-protocol` holds the only parser. New line types and fields are additive; any other change bumps the version with a new ADR, and the engine keeps reading every version it has shipped.

## Consequences

- A user's prints cannot change a score.
- Each new language only needs a kit that writes these lines; the engine and graders are unchanged.
- The Java kit changes from the prototype: `TestRunner` takes `--results <file>` and the calibration factor. Prototype sessions are not migrated; they stay readable in the prototype only.
