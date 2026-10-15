# How a strong round goes: Build-Log Bracket Checker

**Read this first.** This is one good way through the problem, not the only one. The complexity figures describe the reference solution. The practice links go to other sites and to different questions that use the same technique; they are not this question. The prompts are written for interview mode, where the assistant does what you specify and nothing more.

## Minutes 0 to 4: read before you run

Say aloud: "I'll take four minutes to read the checker, the error record and the sample log before I run anything."

1. `BracketError.java`: a column (from 0) and one of four kinds. Read the comments on the kinds; they are the phase 2 rules in short.
2. `BracketChecker.isBalanced`: a `Deque<Character>` stack. Openers push; a closer pops and compares. Then read the line after the loop: `return true;`. Note it, do not fix it yet.
3. `unbalancedLines`: line numbers from 1, through `isBalanced`.
4. `data/build.log`: six lines. Line 3 `test(unit (fast)` leaves one bracket open, line 4 has a stray `)`, line 5 closes `[` with `}`.
5. `Phase1Tests.java`: five tests. `sampleLog` expects lines 3, 4 and 5.

Only now run the phase 1 tests.

## Prompt: understand the codebase fast

Use this once, while you read the tests yourself. It asks for facts you can check, not for a diagnosis.

```text
Read-only question. For this project give me:
1. One line per class: what it owns and which other classes it calls.
2. For BracketChecker.isBalanced: every way the method can return, with the line number of each return.
3. What Datasets.deeplyNested and Datasets.buildLog produce, in one sentence each.
Keep it under 150 words. Mark anything you are inferring.
```

Check item 2 against the code yourself: there are two returns, one inside the loop and one after it. That second one is the whole of phase 1.

## Phase 1: unclosed lines pass (minutes 4 to 8)

### Reproduce

Run phase 1 tests. `anOpenerLeftOpenFails` fails on `"(("`, and `sampleLog` fails because it gets `[4, 5]` instead of `[3, 4, 5]`. The stray-closer and mismatch tests pass.

### Narrow it down, aloud

"Every failure is a line whose only fault is an opener that never closes. Lines with a bad closer are caught. Closers are checked inside the loop; nothing checks what is left on the stack once the loop ends."

### The cause

After the loop the method returns `true` whatever is still on the stack.

### The fix

```java
return open.isEmpty();
```

Run phase 1 tests again; all five pass.

### Say this

"Most tests passed because the loop already rejects every closer error; only leftovers at the end were ignored. The check is one pass, linear in the line length."

## Phase 2: point at the error (minutes 8 to 28)

### Analysis before any prompt

Say the rules in the order they apply, then work two lines by hand:

- Quote state comes first: inside `"..."` brackets do not count, and a backslash skips the next character. Outside quotes a backslash is an ordinary character.
- Closer errors are found during the scan, so the first one wins and the method can return at once.
- At the end, an open quote beats open brackets: `UNCLOSED_STRING` at the quote's column.
- Otherwise open brackets are `UNCLOSED` at the **earliest** one still open, which is the bottom of the stack, not the top.

By hand: `"(](("` gives `MISMATCH` at column 1 (the scan stops there). `"()(x"` gives `UNCLOSED` at column 2.

### Decide the approach yourself

"One pass with an index. I keep the column of an open quote, or -1, and a stack of the columns of open brackets. Columns, not characters, because the error needs a column and I can read the character back from the line."

### Prompt

```text
Implement BracketChecker.firstError(line) as one left-to-right pass with an index i.
Keep quote = column of an open double quote, or -1. While quote >= 0: on a backslash skip the
next character (i++); on a double quote set quote = -1; ignore everything else.
Outside quotes: a double quote sets quote = i; an opener pushes i onto a stack of columns;
a closer returns UNEXPECTED_CLOSER at i when the stack is empty, or MISMATCH at i when the
opener at the top does not match. After the loop return UNCLOSED_STRING at quote if quote >= 0,
else UNCLOSED at the bottom of the stack (the earliest open column) if the stack is not empty,
else Optional.empty(). Then change isBalanced(line) to return firstError(line).isEmpty().
Only these two methods.
```

### Check the reply for

- `UNCLOSED` at the bottom of the stack. Reporting the top fails `unclosedPointsAtTheEarliestOpener` on `"(a[b"`.
- The quote check before the open-bracket check at the end of the line.
- Backslash handling only inside quotes.
- `isBalanced` delegating to `firstError`. Two separate implementations drift, and `isBalancedAgreesWithFirstError` catches it.

Run phase 2 tests, then phase 1 again: the new `isBalanced` must still pass it.

### Reference shape

```java
for (int i = 0; i < line.length(); i++) {
    char c = line.charAt(i);
    if (quote >= 0) {
        if (c == '\\') i++;
        else if (c == '"') quote = -1;
    } else if (c == '"') {
        quote = i;
    } else if (isOpener(c)) {
        push(i);
    } else if (isCloser(c)) {
        if (depth == 0) return error(i, UNEXPECTED_CLOSER);
        if (closerFor(line.charAt(pop())) != c) return error(i, MISMATCH);
    }
}
```

## Phase 3: huge lines (minutes 28 to 35)

### Read the two tests first

- `aLineNestedAMillionDeep`: 2,000,001 characters, a million openers, a million closers, then one stray closer at column 2,000,000. Three seconds.
- `aLongBuildLog`: 200,000 ordinary lines. Any per-line linear check passes.

Say how many times your code looks at each character: "Once. Each character is read once and each bracket is pushed and popped at most once, so the deep line is two million steps."

### What to do

If your phase 2 code is the one-pass stack, phase 3 already passes. Run it. If it times out, the matcher is looking backwards for each closer, which is a million closers each walking back up to a million characters. Replace that search with the stack.

An optional tidy-up, worth saying aloud even if you skip it: a `Deque<Integer>` boxes a million integers. An `int[]` that doubles when full holds the same columns in about 4 MB.

```text
In BracketChecker.firstError, replace the Deque of columns with an int[] stack and a depth
counter, starting at 16 entries and doubling with Arrays.copyOf when full. Change nothing else.
```

Run all three phases.

## The technique: a stack for nested pairs

**The idea.** Brackets close in the reverse order they open, so the bracket that must close next is always the most recent one still open. A stack gives you exactly that element in O(1): push on open, pop on close.

**How to recognise it.** Anything nested where the latest opener must close first: brackets, HTML or XML tags, function calls and returns, undo within a scope, "decode string" problems.

**Why counters fail.** One counter per bracket type knows how many are open but not in what order, so it accepts `([)]`. One global counter cannot even tell `(` from `[`.

**The two decisions that cause bugs.** What to store on the stack: the character is enough for yes or no, but an error position needs the column. And which open bracket to report at the end: the brief asks for the earliest, the bottom of the stack.

| Approach | Time for a line of n characters | Extra space |
| --- | --- | --- |
| Backward search for each closer | O(n squared) | O(n) |
| Stack of columns, one pass | O(n) | O(depth), at most O(n) |
| Counter per bracket type | O(n) | O(1), but wrong on crossed pairs |

**Practise the technique**

These open inside the app under DSA.

- [Valid Parentheses](dsa://valid-parentheses): phase 1 exactly, as a yes or no.
- [Minimum Remove to Make Valid Parentheses](dsa://minimum-remove-to-make-valid-parentheses): a stack of indexes, as in phase 2.
- [Longest Valid Parentheses](dsa://longest-valid-parentheses): a harder stack-of-indexes problem.

## Time budget

| Minutes | What |
| --- | --- |
| 0 to 4 | Read the two classes, the phase 1 test names and the sample log |
| 4 to 8 | Phase 1: reproduce, name the missing end check, one-line fix |
| 8 to 12 | Phase 2 rules aloud and two lines worked by hand |
| 12 to 28 | Phase 2: `firstError`, `isBalanced` delegating, phase 1 and 2 runs |
| 28 to 35 | Phase 3: read both tests, count passes per character, run, optional `int[]` |
| 35 to 45 | Read your whole diff, answer the follow-ups aloud |

## Follow-up answers

**Why did most tests pass even though unclosed brackets were accepted?** Every closer error returns inside the loop. Only lines whose single fault is a leftover opener reach the `return true`, and only two tests have such lines.

**Why a stack, and what does a counter per bracket type get wrong?** The bracket to close next is the latest one opened, which is the top of a stack. Counters lose the order, so `([)]` balances under counters but is a mismatch at column 2.

**Cost.** O(n) time for a line of n characters: each character is read once, each bracket pushed and popped at most once. Memory is O(depth), up to O(n) for a line that is all openers. A million-deep line needs a million stack entries: about 4 MB as `int[]`, several times more as boxed `Integer`s.

**Single quotes and `//` comments.** Replace the quote column with a small state machine: code, double-quoted, single-quoted, comment. Each state has one rule for what ends it, and brackets only count in the code state. A `switch` on the state keeps each rule in one place. Record where each quoted state started so an unterminated one can still be reported.

**What the assistant got wrong.** Answer from your own session. Typical: `UNCLOSED` at the most recent opener, a backslash treated as an escape outside quotes, open brackets checked before the open quote, or `isBalanced` left with its own logic.
