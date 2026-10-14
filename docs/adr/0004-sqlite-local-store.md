# 0004: SQLite local store in the engine

Date: 2026-10-14. Status: accepted.

## Context

The prototype stores each session as `session.json`. PLAN.md 2.2 needs append-only timelines, edit records, settings, health results and, from Phase 2, a sync outbox that can answer "what is not yet uploaded". Writing one JSON file per session cannot answer that cheaply and is not atomic across files.

## Decision

The engine keeps one SQLite database, `intervue.db`, in the user's data folder, accessed through `sqlite-jdbc` 3.53.4.0 with `journal_mode=WAL` and `foreign_keys=true`. Phase 1a creates the tables it uses (`sessions`, `events`, `edits`, `chat_turns`, `session_files`, `settings`, `health_results`, `schema_version`); later phases add the rest of PLAN.md 2.2 by migration. Migrations are numbered SQL files run forward-only at engine start, recorded in `schema_version`; a shipped migration is never edited. Session working files stay on disk under `sessions/<id>/work/` because compilers need real files.

## Consequences

- One file to back up, inspect and delete with the account.
- sqlite-jdbc extracts a native library to the temp folder at load; antivirus or a read-only temp folder can block it, so the sessions-folder health check also opens the database.
- The engine runs on the class path, so the jlink image needs `java.sql` but not `java.sql.rowset` (research, Phase 1a).
