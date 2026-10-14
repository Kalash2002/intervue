# Dryrun

A local practice room for the structured AI-assisted coding interview: a multi-file Java project you did not write, a chat assistant that cannot edit your files, a 60-minute clock and three phases (fix a bug, build a feature, handle large inputs).

Everything runs on your computer. Your code is compiled and run with your own JDK. The assistant is your own Claude Code CLI.

## Start

1. You need a JDK, version 17 or newer (`java -version`, `javac -version`).
2. For the assistant, install Claude Code and sign in once by running `claude` in a terminal. Without it, everything except chat and written feedback still works.
3. Double-click `run.bat` (Windows) or run `./run.sh` (macOS, Linux).
4. Your browser opens http://localhost:4477. If it does not, open that address yourself.

To stop, press Ctrl+C in the terminal window. To use another port: `run.bat --port 4488`.

## The dashboard

The first page shows your progress: how many problems are solved, attempted and not started, your average best score and the time you have practised. Problems are grouped by category, each with its status, best score and number of attempts. Session history is at the bottom.

A problem counts as **solved** when a finished session passes every phase in its final test run.

## A round

- **Start the clock** on a problem. The clock runs from that moment, including while the page is closed.
- Left: files. Middle: editor and output. Right: the brief and the assistant.
- **Ctrl+S** saves and runs the selected target. **Ctrl+Enter** runs. Pick the target (phase tests, all tests, the demo `Main`) next to the Run button.
- A phase's brief opens when the tests of the phase before it pass. You can open it early; that is recorded.
- Tests, `Datasets` and the files under `data/` are read-only and are restored before every run.
- The strip under the top bar draws your session: bars are runs, dots are prompts, flags are phases passed.
- **Finish** stops the clock, runs every test once more and opens the result page.

## The result page

- **Score out of 100.** Correctness 70 (phase 1 scores 20 only when all its tests pass; phases 2 and 3 score 30 and 20 in proportion to tests passing in the final run), time 10, working rhythm 20 (runs between prompts, no long stretch without a run, no skipped phase). The table on the page shows each part. Communication cannot be observed and is not scored.
- **Result by phase:** when each phase first passed, how long it took, runs and prompts in that time, and every test with its outcome.
- **What a strong round looks like:** a full walkthrough for that problem: what to read first, a prompt to understand the codebase fast, how to reproduce and reason to the bug, the prompts and checks for each phase, the technique behind it with its complexity, practice links and a time budget. It is only shown after you finish, and it gives the solution away, so attempt the problem properly first.
- **Follow-ups and feedback:** questions to answer aloud, your notes, and written feedback from a reviewer model that sees the grading notes.

### The assistant

It receives your saved files and the brief with every message, and replies in chat only. It runs in an empty scratch folder with its tools switched off.

- **Interview mode** (default): it does not volunteer bugs or whole solutions, and does the specific thing you ask. Several candidates describe the real assistant this way.
- **Helpful mode**: unrestricted.

Each message is one call to `claude -p`, so it counts against your Claude Code plan or API credit.

## Problems

| Problem | Level | Topic |
| --- | --- | --- |
| Hold Exposure | Easy | Totals over time intervals |
| Corridor Router | Medium | Routes through a payment network |
| Merchant Suggest | Hard | Search suggestions while typing |

The problems are original; none is a reported interview question. Each has a reference solution and grading notes in `problems/<id>/solution/`. Do not open that folder before you have attempted the problem.

## Adding a problem

Copy a folder under `problems/` and change it:

- `problem.json`: title, minutes, phases (test class per phase), read-only files, follow-up questions.
- `brief/intro.md`, `brief/phase1.md`, ...: what the candidate reads.
- `starter/`: the project, flat `.java` files in the default package, plus optional data files in a subfolder.
- `solution/NOTES.md`: grading notes for the reviewer. Reference code goes beside it.
- `solution/naive/`: a correct but slow solution. `java tools/Verify.java <id>` checks all three: the starter fails, the reference passes, the naive solution times out in the last phase.
- `walkthrough.md`: the walkthrough shown on the result page.
- `category` in `problem.json` groups the problem on the dashboard.

Tests use the small kit in `kit/`: `@Test(order = 1, timeoutMs = 3000)` and the static methods in `kit.Check`. The tests of a class run in a child JVM; a test that hits its time limit ends that JVM and the rest continue in a fresh one, so a stuck test cannot slow down the next.

## Updating

Unzip a new version over the old folder, or copy your old `sessions` folder into the new one. Sessions from older versions still open.

## Where things are

- `sessions/<id>/work/`: your files for that session. `session.json` beside it holds the timeline, chat and feedback.
- `server/Server.java`: the whole server, one file, no dependencies.
- `web/`: the interface. The editor is CodeMirror 5 (MIT); fonts are Hanken Grotesk and JetBrains Mono (SIL Open Font License).
- `tools/Verify.java`: checks every problem three ways before a release.

## Troubleshooting

| Symptom | Fix |
| --- | --- |
| "Java was not found" | Install a JDK and reopen the terminal. A JRE alone is not enough; `javac` is needed. |
| Assistant says the CLI was not found | Make sure `claude --version` works in a new terminal, then restart Dryrun. Or set the environment variable `DRYRUN_CLAUDE` to the full path of the `claude` program. |
| Assistant says it is not signed in | Run `claude` in a terminal and sign in. |
| "Port 4477 is already in use" | Dryrun is probably already running. Otherwise start with `--port 4488`. |
| A phase 3 test times out on the reference solution | The time limits assume a reasonably recent laptop. Raise `timeoutMs` in that problem's `starter/Phase3Tests.java`. |

Dryrun listens on 127.0.0.1 only and refuses requests from other sites.
