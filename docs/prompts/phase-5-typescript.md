# Phase 5: JavaScript/TypeScript package, port, website-practice decision

## Context to read first

`CLAUDE.md`; `docs/plan/PLAN.md` sections 4.2 (JavaScript/TypeScript column), 4.6, 2.4 (code execution options), 10 (Phase 5 row); `docs/contracts/language-contract.md`; `languages/python/` as the second reference.

## Goal

Every problem has a verified TypeScript variant on the Node LTS current at the time; a measured go or no-go on in-browser practice.

## In scope

`languages/javascript/` with Node downloads, type-stripping constraints documented, worker-per-test kit with parent kill, calibration, setup guide, editor with `lsp.json` for typescript-language-server as the optional plugin; `variants/javascript/` for the catalogue; a spike that runs the engine inside a container with the Java and Python toolchains and measures cost per session-hour for PLAN.md 2.4 option (b); a written decision in `docs/adr/`.

## Out of scope

Engine, shell, web, backend changes; building website practice itself.

## Constraints

Content avoids `enum`, `namespace`, decorators and `.tsx`; explicit `.ts` import extensions; standard library only.

## Exit criteria

1. Verification green for the catalogue on both operating systems.
2. Doctor passes for Node from clean machines.
3. The container spike reports cost per session-hour and the ADR records the decision.

## Gates, deliverables, report

As in the process documents.
