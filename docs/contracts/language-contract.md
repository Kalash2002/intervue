# Language contract, SPI_VERSION 1

What a language package provides so the engine can run its problems without knowing the language (PLAN.md 4.1, 2.6). A package declares `spiVersion`; the engine refuses a package whose version it does not know. Adding an optional method or field is additive; anything else bumps `SPI_VERSION` and needs an ADR.

## Packaging

- `languages/<id>/language.json`: the data below.
- A Java implementation of `LanguagePackage`, registered in `META-INF/services/si.intervue.engine.languages.LanguagePackage` and found by `ServiceLoader`. The interfaces live in the engine package `si.intervue.engine.languages`; a language package depends on that package only.
- `testkit/`: files copied into every session's work folder; they write results in the run protocol (`run-protocol.md`).

## language.json

| Field | Meaning |
|---|---|
| `id`, `displayName`, `spiVersion` | Identity |
| `toolchain` | `policy` (`bundled`, `downloaded`, `user-installed`), `minVersion`, `recommended` |
| `extensions` | Source file extensions the editor and the edit applier treat as code |
| `limits` | `memoryMb` default and clamp, `outputBytes` |
| `calibration` | Benchmark program, repetitions, `referenceMs` |

## Interface

```java
public interface LanguagePackage {
  int SPI_VERSION = 1;
  LanguageDefinition definition();                // id, display name, spiVersion, extensions
  Runner runner();                                // compile and run against the run protocol
  TestKit testKit();                              // files to copy into work/
  CalibrationBenchmark benchmark();               // measures this machine
  List<HealthCheck> healthChecks();               // toolchain present, sample, encoding
  EditorSupport editor();                         // symbols and compiler diagnostics for the editor
}
```

| Method | Contract |
|---|---|
| `Runner.compile(workDir, outDir)` | Returns success, or diagnostics with file, line, column and message |
| `Runner.run(request)` | Runs a `phase:<n>`, `all` or `main` target within the limits, streams user output, leaves `results.jsonl` in `outDir`; kills the whole process tree on the wall limit |
| `EditorSupport.symbols(files)` | Classes, methods and fields with positions, from the editable files only |
| `EditorSupport.diagnostics(workDir)` | Compiler diagnostics mapped to file, line and column (1-based) |
| `healthChecks()` | Phase 1a: runtime intact, compile-and-run sample, encoding, calibration |

Read-only files listed in `variant.json` and the kit files are restored by the engine before every run; the package never decides what is editable.
