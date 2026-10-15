# Run the Intervue prototype

An early prototype for engineers who already have a JDK. One timed round: a multi-file Java project you did not write, an AI assistant in chat, a 60-minute clock and three phases (fix a bug, build a feature, handle large inputs). Everything runs on your computer.

## 1. Check Java

You need a **JDK 17 or newer**; a JRE is not enough because your code is compiled.

```
java -version
javac -version
```

Both must print 17 or higher. If `javac` is missing, install a JDK (for example Temurin from adoptium.net) and open a new terminal.

## 2. Optional: the assistant

The chat assistant is your own Claude Code CLI. Install it, then run `claude` once in a terminal to sign in. Each message counts against your Claude plan or API credit. Without it, everything except chat and written feedback still works.

## 3. Start

Unzip anywhere, then:

- **Windows:** double-click `run.bat`.
- **macOS or Linux:** in a terminal, `cd` into the folder and run `sh run.sh`.

Your browser opens http://localhost:4477. If it does not, open that address yourself. Stop with Ctrl+C in the terminal. Port taken? `run.bat --port 4488` or `sh run.sh --port 4488`.

## 4. Do one round

1. Pick **Hold Exposure** (easy) for your first round and press **Start the clock**.
2. Read the brief on the right, explore the files on the left, ask the assistant when you want.
3. **Ctrl+S** saves and runs the tests; the next phase opens when the current one passes.
4. Press **Finish** when done, or when the clock runs out. The result page shows your score out of 100, a phase-by-phase breakdown and a walkthrough of a strong round.

Do not open `problems/<id>/solution/` before you have tried the problem: it holds the reference answer.

## 5. Send us your result

On the result page, copy the page, add three sentences on what felt real and what did not, and reply to the email or message that sent you here.

## If something goes wrong

| Symptom                                        | Fix                                                                                                                                      |
| ---------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------- |
| "Java was not found"                           | Install a JDK 17+ and open a new terminal.                                                                                               |
| Assistant says the CLI was not found           | Check `claude --version` in a new terminal, then restart. Or set `INTERVUE_CLAUDE` to the full path of `claude`.                         |
| "Port 4477 is already in use"                  | It is probably already running; otherwise use `--port 4488`.                                                                             |
| A phase 3 test times out on a correct solution | Time limits assume a recent laptop. Tell us your machine; raising `timeoutMs` in that problem's `starter/Phase3Tests.java` unblocks you. |

Your sessions are saved in `sessions/` inside this folder and never leave your machine, except the messages you send to the assistant. `README.md` has the full manual.
