# How a strong round goes: <Problem title>

**Read this first.** This is one good way through the problem, not the only one. The complexity figures describe the reference solution. The DSA reference at the end names the data structure behind the solution, shows it in plain Java, gives its cost and lists the questions to practise, which open inside the app. The prompts are written for interview mode, where the assistant does what you specify and nothing more.

## Minutes 0 to N: read before you run

Say aloud: "<what you will read before running anything, in one sentence>."

1. `<File>.java`: <what it owns and the one thing to notice>.
2. ...

Only now run the phase 1 tests.

## Prompt: understand the codebase fast

Use this once, while you read the tests yourself. It asks for facts you can check, not for a diagnosis.

```text
Read-only question. For this project give me:
1. One line per class: what it owns and which other classes it calls.
2. <A fact about the method the bug lives in, with line numbers.>
3. <A fact about the datasets or the data file.>
Keep it under 150 words. Mark anything you are inferring.
```

## Phase 1: <title> (minutes A to B)

### Reproduce

<Which tests fail and on what input.>

### Narrow it down, aloud

"<The reasoning that points at the cause, as you would say it.>"

### The cause

<One or two sentences.>

### The fix

```java
<the smallest change>
```

### Say this

"<Why most tests passed anyway, and the cost of the fix.>"

## Phase 2: <title> (minutes B to C)

### Analysis before any prompt

<The rules in the order they apply, then one or two inputs worked by hand.>

### Decide the approach yourself

"<The approach in your own words: the data structure, what it stores, and why.>"

### Prompt

The voice: an experienced developer who has decided the approach and asks for it, the way they would in the round. State the decision and the one or two points where a wrong choice fails a test; name the method and the scope; never dictate the code line by line.

```text
<I've decided on ... I'll keep ... Write <method> that way: <the decisions that matter>. <Edge cases as
behaviour, not as branches>. Only this method.>
```

### Check the reply for

- <The decision a model most often gets wrong, and the test that catches it.>
- <A second one.>

### Reference shape

```java
<the core of the reference, 10 to 20 lines>
```

## Phase 3: <title> (minutes C to D)

### Read the two tests first

- `<test>`: <size and limit>.

### What to do

<Whether phase 2 already passes and what to change if it does not.>

```text
<The prompt for the change, same voice.>
```

## DSA reference

### Theory

![<One sentence: what the figure shows, with the concrete values drawn in it>](figures/<name>.svg)

**The idea.** <The data structure or algorithm in general terms: what it keeps and what that buys.>

**How to recognise it.** <The wordings and shapes of questions that call for it.>

**The two decisions that cause bugs.** <Where implementations go wrong.>

### Code

The structure on its own, outside this codebase; the reference solution is this shape with the problem's names.

```java
<10 to 20 lines of plain Java: the general routine, with a comment on each decision>
```

### Complexity

| Approach | Time | Extra space |
| --- | --- | --- |
| <naive> | O(...) | O(...) |
| <this technique> | O(...) | O(...) |

### Practise the technique

These open inside the app under DSA.

- [<Question title>](dsa://<question-id>): <how it relates to a phase>.
- [<Question title>](dsa://<question-id>): <...>.

## Time budget

| Minutes | What |
| --- | --- |
| 0 to N | ... |

## Follow-up answers

**<Follow-up question from problem.json>** <The answer in two to four sentences.>
