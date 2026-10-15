# Build-Log Bracket Checker

A CI system writes structured build logs, one record per line. A line is **balanced** when every `(`, `[` and `{` is closed by the matching `)`, `]` or `}`, in the right order, on the same line. Unbalanced lines usually mean a tool crashed halfway through writing.

You have 45 minutes for three phases. Start by reading the code; `Main` checks the sample log in `data/build.log`.

| File | What it holds |
| --- | --- |
| `BracketError` | Where a line goes wrong, and how |
| `BracketChecker` | The checks. Most of your work is here. |
| `Datasets` | Builds the huge lines and logs for phase 3 |

Columns count from 0. Tests are read-only. You can add your own files.
