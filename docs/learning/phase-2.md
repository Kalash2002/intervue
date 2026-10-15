# Learning for Phase 2: sync and deferred AI feedback

Time (est.): 8 hours.

## Minimal concepts

| Concept | Why it matters in Intervue | Source |
|---|---|---|
| Append-only event logs and idempotent upserts keyed by (session, seq) | Sessions sync without merges | "Designing Data-Intensive Applications" chapter 5 on replication, the sections on leaderless and conflict handling |
| The transactional outbox pattern | Nothing is lost between SQLite and the backend | microservices.io outbox pattern page |
| Cursors versus timestamps for pull | Other devices' summaries arrive once, in order | Any API pagination guide covering cursor pagination |
| Last-writer-wins and why it is acceptable only for notes | The one mutable field | Same DDIA chapter |
| Testing sync: duplicate pushes, out-of-order batches, a device offline for a month, a crash between push and ack | The bugs that lose data | Your own test list, from PLAN.md 2.2 |
| Server-sent events for streamed feedback delivery | Deferred feedback arrives while the app is open | MDN "Using server-sent events"; Spring `SseEmitter` docs |
| The Language Server Protocol lifecycle (initialize, didOpen, didChange, completion, hover, diagnostics) and `@codemirror/lsp-client` transports | Java IntelliSense as a downloadable plugin | LSP specification overview; the lsp-client README; the Eclipse JDT Language Server README (Java 21 minimum) |

## Tutor prompt

```
You are my tutor for offline-first sync. I am a backend engineer (Java, Spring Boot, SQL) building Intervue, a desktop app whose sessions are recorded locally in SQLite as append-only events and must appear identically on every device of the user, with AI feedback produced on the server for sessions done offline. I have about 8 hours.

Teach me only: append-only event logs with idempotent upserts keyed by (session id, sequence); the transactional outbox pattern between SQLite and an HTTP API; cursor-based pull; last-writer-wins for one mutable field and why it is wrong for everything else; the test cases that catch data loss (duplicate push, out-of-order batch, a device offline for a month, a crash between push and acknowledgement); server-sent events from Spring for delivering a result when it is ready; the Language Server Protocol lifecycle well enough to start the Eclipse JDT Language Server from my Java engine, bridge its stdio to a local socket, and connect CodeMirror's lsp-client to it.

Method: two-sentence explanation, one minimal example tied to Intervue (a session with 40 events pushed in two batches, a note edited on two devices), one common mistake, then one question to check I understood before moving on. Keep answers short. At the end give me a 30-minute exercise: write the push and pull request and response shapes and the five test cases as a table, and a checklist of what I should be able to do without notes.

Do not teach me CRDTs, operational transforms, or distributed consensus. Point me to the current Spring docs for exact class names.
```
