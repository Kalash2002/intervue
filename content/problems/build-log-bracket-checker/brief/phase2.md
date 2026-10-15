## Phase 2: point at the error

Implement in `BracketChecker`:

```java
Optional<BracketError> firstError(String line)
```

Rules:

- Text between double quotes is ignored: brackets inside `"..."` do not count. Inside quotes, a backslash escapes the next character, so `\"` does not end the quoted text.
- Read left to right. A closing bracket with nothing open is `UNEXPECTED_CLOSER` at its column. A closing bracket that does not match the most recent open one is `MISMATCH` at its column. The first such error wins.
- If the line ends inside quotes, the error is `UNCLOSED_STRING` at the opening quote.
- Otherwise, if brackets are still open at the end, the error is `UNCLOSED` at the **earliest** one still open.
- A line without errors returns `Optional.empty()`.

Then make `isBalanced(line)` agree with it: balanced means `firstError(line)` is empty.

Run **Phase 2 tests**.
