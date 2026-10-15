# The phase pipeline: one prompt in, one reviewed increment out

Every roadmap phase is a file in `docs/prompts/`. Running `/phase <file>` executes these steps in order. Each step names who does it and what it must produce. A step that fails stops the pipeline with a written reason; nothing is merged half-done.

| Step | Who | Input | Output | Stop condition |
|---|---|---|---|---|
| 1. Diagnose | `status-diagnoser` subagent | `docs/status.md`, `CHANGELOG.md`, `git log`, CI status, the phase prompt | Status report: done, in progress, blocked, gap list against the phase exit criteria, risks noticed | Repository does not build, or a previous phase's gates are red |
| 2. Research | `researcher` subagent with Context7 | The libraries and APIs the phase touches | Short notes with exact API shapes and versions, saved to `docs/research/<phase>.md` | None; unknowns are listed as questions |
| 3. Plan | main agent using the superpowers `brainstorming` and `writing-plans` skills | Prompt, status report, research notes | A step plan in `docs/plans/<phase>.md`: files to create or change, tests to write first, order of work, acceptance checks | The plan touches a part outside the prompt's scope |
| 4. Design review | `design-reviewer` subagent | The plan, `docs/contracts/`, `CONTRIBUTING.md`, PLAN.md section 5 | Pass or a list of boundary, contract or simplicity violations to fix in the plan | Any violation, until fixed |
| 5. Implement | main agent using the superpowers `test-driven-development` and `executing-plans` skills, subagents for parallel independent steps | The approved plan | Code and tests, small commits per plan step | A test the plan requires cannot be made to pass |
| 6. Simplify | `/simplify` on the diff | The diff | Fewer lines, same behaviour, same tests green | Behaviour change detected by tests |
| 7. Review | `/code-review` at high effort | The diff | Findings fixed or recorded with a reason | Any correctness finding left open |
| 8. Gates | `qa-gatekeeper` subagent | The repository | All gates in `definition-of-done.md` green; a short report | Any red gate |
| 9. Report | main agent | Everything above | `CHANGELOG.md` entry, `docs/status.md` and `docs/runbooks/commands.md` updated, PR opened with the template filled, a summary to the human | None |

`docs/research/<phase>.md` and `docs/plans/<phase>.md` are working documents: they stay on your machine and are listed in `.gitignore`. What matters from them is carried into code, tests, ADRs, contracts, the plan or the retro, which are committed.

## Tooling the pipeline relies on

| Tool | Role in the pipeline | Install (verified 2026-10-14) |
|---|---|---|
| superpowers (obra) | Planning and execution discipline: `brainstorming`, `writing-plans`, `executing-plans`, `test-driven-development`, `systematic-debugging` | `/plugin marketplace add obra/superpowers-marketplace` then `/plugin install superpowers@superpowers-marketplace` ([github.com/obra/superpowers](https://github.com/obra/superpowers)) |
| Context7 (Upstash) | Current library documentation for the research step | `claude mcp add --scope user --header "CONTEXT7_API_KEY: <key>" --transport http context7 https://mcp.context7.com/mcp` ([context7.com/docs](https://context7.com/docs/resources/all-clients)); key from context7.com |
| `/code-review` | Review step | Built into Claude Code |
| `/simplify` | Simplify step | Built into Claude Code |
| gstack (Garry Tan) | Optional later: `/qa` for browser QA of the website, `/ship` for release hygiene, `/retro` | Clone to `~/.claude/skills/gstack` and run `./setup` ([github.com/garrytan/gstack](https://github.com/garrytan/gstack)) |

Use one planning framework at a time. superpowers and gstack both try to run the whole workflow; with both installed their skills trigger on the same words and fight over the process. Start with superpowers plus Context7 plus the two built-in skills. Add individual gstack skills only when a specific one is missed, and never its planning or review skills alongside superpowers.

## Subagents

Defined in `.claude/agents/`. Each has a narrow job, read-mostly tools, and a required output shape so the main agent can act on it without re-reading the repository.

| Agent | Tools | Produces |
|---|---|---|
| `status-diagnoser` | Read, Grep, Glob, Bash (read-only git and build commands) | The status report in step 1 |
| `researcher` | Read, Grep, Glob, Context7 tools, WebFetch | Research notes in step 2 |
| `design-reviewer` | Read, Grep, Glob | Pass or violations in step 4 |
| `qa-gatekeeper` | Read, Grep, Glob, Bash | Gate report in step 8 |

Implementation is done by the main agent, not a subagent, so that the human sees the work as it happens and can interrupt.
