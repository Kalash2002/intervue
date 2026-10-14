# Learning for Phase 6: Go package, port, Linux builds

Time (est.): 20 hours.

## Minimal concepts

| Concept | Why it matters in Dryrun | Source |
|---|---|---|
| Reading Go: packages, slices and maps, structs and methods, interfaces, error values, `defer`, goroutines only as far as `go test` uses them | Reviewing AI-drafted ports | A Tour of Go; Effective Go, the sections on slices, maps, errors |
| Standard library equivalents: `sort`, `container/heap`, `strings`, `bufio`, `encoding/csv` | Standard library only | Go package docs |
| Modules offline: `go.mod` with no requires, `GOFLAGS=-mod=readonly`, `GOPROXY=off`, `GOTOOLCHAIN=local`, `GOCACHE` in the app folder | Builds with no network | go.dev module reference and toolchain page |
| `go vet` runs with `go test`; the whole-binary `-timeout`; why the kit forks itself per test | Correct per-test timeouts | `cmd/go` docs; `testing` package docs |
| `gofmt` as the only standard-library formatter | Editor formatting for Go | `cmd/gofmt` docs |
| Linux packaging: AppImage and deb from the Tauri bundler, `prlimit` for resource limits, unsigned distribution warnings | The Linux build | Tauri Linux bundling page; `prlimit(1)` man page |

## Tutor prompt

```
You are my tutor for reading Go and for Linux packaging basics. I am a backend engineer (Java) who has never written Go, building Dryrun, where interview problems get Go variants (starter, read-only tests, reference, deliberately slow solution) built offline with the standard library and run by a small test runner with per-test timeouts, and where the desktop app gets unsigned Linux builds. I have about 20 hours.

Teach me only: Go syntax and idioms I will meet in such code (packages, slices and maps and their gotchas, structs and methods, interfaces, error values, defer); the standard-library equivalents of Java collections and sorting, including container/heap; building fully offline with a go.mod that has no requires, GOFLAGS, GOPROXY=off, GOTOOLCHAIN=local and a GOCACHE inside my app folder; why go test's -timeout is whole-binary and how a runner forks itself per test for a per-test limit; what go vet will reject in content; gofmt; producing AppImage and deb with the Tauri bundler and what users see when they are unsigned; prlimit for CPU and memory caps.

Method: show me small Java snippets I know and the idiomatic Go, two sentences of explanation, one common mistake an AI port makes (slice aliasing, nil maps, shadowed err), then one question to check I understood before moving on. Keep answers short. At the end give me a 30-minute exercise: port one small Java class with a hop-limited shortest path to Go by hand and make it pass go vet, and a checklist of what I should be able to spot in a port without notes.

Do not teach me goroutine-heavy concurrency, web servers, generics in depth, or CGO. Say when behaviour depends on the Go version.
```
