# Run protocol, version 1

How every language runner reports test results to the engine (PLAN.md 4.3, ADR 0005). The parser in `shared-java/run-protocol` is the only reader. A change that is not additive bumps `protocol` and needs an ADR.

## Input to a run

`{workDir, outDir, target, variant, limits:{wallMs, memoryMb, outputBytes}, calibrationFactor}`

- `target`: `phase:<n>`, `all`, or `main` (the demo entry point; no results file).
- `calibrationFactor`: a number from 1.0 to 4.0. The kit multiplies every per-test `timeoutMs` by it; the engine multiplies the whole-run `wallMs` by it.

## Output

The user's program output streams to the UI as plain text. Structured results go to `{outDir}/results.jsonl`, one JSON object per line, so user prints cannot corrupt them.

```
{"protocol":1,"t":"start","runner":"java-kit/2.0","language":"java","factor":1.3}
{"t":"suite","name":"Phase2Tests","tests":11}
{"t":"test","suite":"Phase2Tests","name":"singleHold","status":"pass","ms":11}
{"t":"test","suite":"Phase2Tests","name":"sampleFile","status":"fail","ms":4,"detail":"expected <255049> but was <255048>","at":"Phase2Tests.sampleFile(Phase2Tests.java:118)"}
{"t":"test","suite":"Phase3Tests","name":"oneBusyAccount","status":"timeout","ms":7800,"limitMs":7800}
{"t":"summary","passed":12,"failed":1,"timeouts":1,"compileError":false,"wallMs":9120}
```

| Line `t` | Required fields | Optional fields |
|---|---|---|
| `start` | `protocol` (integer, first line only), `runner`, `language` | `factor` |
| `suite` | `name`, `tests` (count) | |
| `test` | `suite`, `name`, `status`, `ms` | `detail`, `at`, `limitMs` (the scaled limit this test ran under; the Java kit writes it on every test line) |
| `summary` | `passed`, `failed`, `timeouts`, `compileError`, `wallMs` | |

`status` is `pass`, `fail`, `timeout`, or `error` (the harness failed, not the test).

## Parser rules

1. The first line must be `start` with `protocol` equal to a version the parser knows; otherwise the run is refused as "runner protocol not supported".
2. Unknown `t` values and unknown fields are ignored, so new line types and fields are additive.
3. A missing or truncated last line (the runner was killed) is tolerated: tests already reported stand, and the run has no summary.
4. Tests named in a `suite` line but never reported count as not run.
5. A compile error is reported by the engine, not the kit: no results file is read and the run carries `compileError: true`.
