# Code review plan

You are a solo developer working with coding agents, so review has three layers until a second human joins.

| Layer | When | Who | What it checks |
|---|---|---|---|
| Design review | Before any code in a phase, step 4 of the pipeline | `design-reviewer` subagent | Boundaries, contracts, simplicity: does the plan touch only its part, does it add anything the phase prompt did not ask for, does it reuse what exists |
| AI code review | On every PR, step 7 of the pipeline | `/code-review` at high effort, `--max-findings all` for Phase 1 PRs | Correctness, missing tests, security at trust boundaries, inconsistent naming, dead code |
| Human self-review | Before merge | You, with the PR template | Does it do what the prompt said, is the diff readable without the agent's explanation, would you understand it in a year, screenshots for UI |
| Periodic | Monthly, or after every three phases | `/simplify` on the whole module plus a ponytail-style over-engineering pass | What can be deleted, which abstractions have one implementation, which flags are at 100% |
| Second human | When a contributor joins | Required reviewer on `main` via branch protection | Everything above, plus the content isolation rule for content PRs |

## Review etiquette for the agent

- Findings are reported as file, line, severity, reproduction, minimal fix.
- A finding is closed by a fix or by a written reason in the PR; never by silence.
- Review does not reopen scope: if the reviewer wants a feature, it goes to `docs/status.md` under "Later".
- Do not approve an unperformed check. If the smoke test did not run on macOS, the PR says so.
