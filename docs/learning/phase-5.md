# Learning for Phase 5: JavaScript/TypeScript package, port, website-practice decision

Time (est.): 12 hours.

## Minimal concepts

| Concept | Why it matters in Dryrun | Source |
|---|---|---|
| TypeScript for content: strict types, ES modules with explicit `.ts` import extensions, what Node's type stripping does not support (enums, namespaces with code, parameter properties, decorators) | Every TypeScript variant must run with no compiler step | Node.js docs, "Modules: TypeScript" |
| Node standard library for problems: `Map`, `Set`, arrays, `node:fs`, `node:readline`, typed arrays for speed | Standard library only | Node API docs |
| `worker_threads`: `Worker`, `terminate()`, `resourceLimits`; `child_process` with `timeout` and `killSignal` | The per-test timeout mechanism of the Node kit | Node docs for `worker_threads` and `child_process` |
| Single-threaded timing: why a busy test blocks timers in its own thread and the kill must live in the parent | Correct timeouts | Same |
| Container cost spike: running a JDK plus the engine in Docker with a CPU and memory cap, measuring cost per session-hour | The website-practice decision | Docker resource constraints docs; PLAN.md 2.4 |

## Tutor prompt

```
You are my tutor for TypeScript as a problem-content language and for Node's process model. I am a backend engineer (Java) who can read JavaScript and TypeScript, building Dryrun, where interview problems get TypeScript variants that run on Node with built-in type stripping and no compiler, standard library only, in a local runner with per-test timeouts. I also need to run a small experiment on running my Java engine in a container to price website practice. I have about 12 hours.

Teach me only: writing strict TypeScript that Node's type stripping accepts (what to avoid: enums, namespaces with runtime code, parameter properties, decorators; explicit .ts import extensions; no tsconfig paths); the Node standard library pieces problems will use, including typed arrays for performance-sensitive workloads; running each test in a worker thread with resourceLimits and terminating it from the parent, with a child-process fallback; why timers inside a busy worker do not fire; running a JDK and a Java process inside Docker with CPU and memory limits and measuring cost per session-hour from a cloud provider's per-second price.

Method: two-sentence explanation, one minimal example tied to Dryrun (a phase 3 test with a 6-second limit, the kit's parent and worker), one common mistake, then one question to check I understood before moving on. Keep answers short. At the end give me a 30-minute exercise: write the worker-based test runner skeleton in TypeScript, and a checklist of what I should be able to do without notes.

Do not teach me React, bundlers, npm publishing, or Deno and Bun. Say when behaviour depends on the Node version.
```
