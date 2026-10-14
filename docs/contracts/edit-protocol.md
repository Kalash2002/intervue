# Edit protocol, version 1

How the Write code channel returns edits and how the engine applies them (PLAN.md 2.8). The engine's `EditApplier` is the only code that writes them. Phase 1a feeds it from `FakeEditSource` (recorded streams, kept for tests and the developer-mode demo); Phase 1ab from the Claude CLI on the developer route; Phase 1b from the backend. Non-additive changes bump `protocol` and need an ADR.

## Stream

JSON lines, applied one at a time as they arrive:

```
{"protocol":1,"t":"start","replyId":"r7"}
{"t":"note","text":"I will index holds by account so holdsFor stops scanning the whole list."}
{"t":"edit","id":"e1","path":"HoldBook.java","op":"replace","base":"sha256:…","search":"<exact existing text>","replace":"<new text>"}
{"t":"edit","id":"e2","path":"AccountIndex.java","op":"create","content":"<file text>"}
{"t":"done","edits":2}
```

The first line must be `start` with a known `protocol`. Unknown `t` values are ignored. `base` is `sha256:` plus the lowercase hex SHA-256 of the file bytes the model was shown.

## Rules (each has a test in `engine/session/edits/`)

| Rule | Behaviour |
|---|---|
| Allowed paths | Editable source files inside `work/`, and new files with the language's source extension. Never read-only files, never `data/`, never outside `work/`. Otherwise: skipped, shown as "not allowed" |
| Base version | `replace` needs `base` equal to the current file hash; a mismatch shows "your file changed; the suggestion is shown but not applied" |
| Exact match | `search` occurs exactly once; zero or several matches leave the edit unapplied and shown as a suggestion |
| Create | `create` refuses an existing path |
| Size and encoding | Files over 200 KB and files that are not valid UTF-8 are not edited; inserted text takes the file's existing line endings |
| Atomic per edit | The file is copied to `sessions/<id>/edits/<editId>.before` before writing; a failed write restores it |
| Events | `aiEdit {id, path, op, hunks, beforeHash, afterHash, replyId}` on apply; `aiEditUndo {id}` on undo; every `run` event carries `aiEditsActive` |
| Undo | Per edit and per reply; restores the snapshot only if the file still hashes to `afterHash`, otherwise returns the diff and applies nothing. Undoing a `create` deletes the file under the same check |
| Reset | "Reset file" and "Reset problem" copy fresh files from the problem source into `work/`; the source is never modified; both are events. Reset does not undo edits: edits it overwrote are marked undone |
| Assisted | A session is `assisted` when any `run` event or the finish has `aiEditsActive > 0`; informational only, it never changes solved or the score label (ADR 0006) |
| Injection | File contents are data; a file containing instruction-like text changes nothing in how edits are applied |
