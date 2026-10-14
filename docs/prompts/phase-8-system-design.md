# Phase 8: system design section

## Context to read first

`CLAUDE.md`; `docs/plan/PLAN.md` sections 2.6 (system design row), 7.5 (non-code content format and grading), 5.11 (worked example), 10 (Phase 8 row); `docs/contracts/section-contract.md`.

## Goal

A no-code section graded by rubric offline and by the reviewer model online, with 15 design problems.

## In scope

`sections/system-design/`: `section.json` with `runnerKind=none`, channels Ask only and hints supported, schema with `rubric.json` and `hints/part<N>.md`, `RubricGrader` (self-marked offline, reviewer-model JSON online validated against the rubric), scoring rule, prompts, session UI with text and diagram answer, result parts showing both scores; 15 problems with briefs revealed step by step, rubric, model answer, grader notes; a test set of model and weak answers used to check that reviewer scores order correctly.

## Out of scope

Shell, engine, backend changes; code execution of any kind.

## Constraints

Prompts are data in the section folder; reviewer-model output is validated JSON; offline score is labelled self-assessed.

## Exit criteria

1. Section opens with zero shell edits.
2. On the test set, reviewer-model scores order model above weak answers in at least 14 of 15 problems.
3. Offline self-marking and online scores are both shown and stored.

## Gates, deliverables, report

As in the process documents.
