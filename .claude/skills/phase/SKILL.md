---
name: phase
description: Run one Intervue roadmap phase end to end from its prompt file in docs/prompts/, through diagnose, research, plan, design review, test-first implementation, simplify, code review, gates and report. Use when the user says "/phase <file>" or "run phase X".
---

# Run a phase

The argument is a prompt file under `docs/prompts/`. If none is given, list the files there and ask which one.

Follow `docs/process/pipeline.md` exactly, in order, and tell the user which step you are on in one line each time you move.

1. Read the prompt file in full. Extract: goal, scope in and out, constraints, exit criteria, gates, deliverables.
2. Delegate to the `status-diagnoser` subagent with the prompt file path. If it recommends stopping, stop and show its report.
3. Delegate to the `researcher` subagent with the list of libraries and APIs the prompt names plus any you expect to need. Wait for `docs/research/<phase>.md`.
4. Plan with the superpowers `brainstorming` skill only if the prompt leaves a design choice open; otherwise go straight to `writing-plans`. Write `docs/plans/<phase>.md` with ordered steps, each naming the failing test to write first and the files it touches. Keep the plan inside the prompt's scope; put anything else under "Later" in `docs/status.md`.
5. Delegate to the `design-reviewer` subagent with the plan path. Fix every violation in the plan and re-run until it passes.
6. Implement with the superpowers `test-driven-development` and `executing-plans` skills: red test, minimal code, green, commit per step with a Conventional Commit message scoped to the part. Use subagents only for steps the plan marks independent.
7. Run `/simplify` on the diff. Keep tests green.
8. Run `/code-review` at high effort on the diff. Fix each finding or write the reason in the PR body.
9. Delegate to the `qa-gatekeeper` subagent. If RED, fix and re-run from step 7.
10. Update `CHANGELOG.md` under Unreleased, `docs/status.md`, and `docs/runbooks/commands.md` with a section for this phase listing the commands it introduced or changed (setup, run, test, deploy, data access). Open the PR with `.github/PULL_REQUEST_TEMPLATE.md` filled. Report to the user: what shipped, exit criteria met or not, what the human must try by hand, and what went to "Later".

Rules: never skip a step because it "looks fine"; never widen scope; say "cannot verify" rather than guess; stop and ask the user only when a decision changes the deliverable materially.
