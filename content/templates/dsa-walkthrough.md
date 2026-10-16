<!-- Owns the shape of content/dsa/<question-id>/walkthrough.md for dsa-quick: the headings, their order and what each must contain. The words are yours; the voice is in content/templates/README.md. -->
## The question

<The question restated in two sentences, in your own words. Then what makes it harder than it looks: the trap, the size that rules out the first idea, the edge that breaks the obvious code.>

## How to think about it

<The reasoning from the naive idea to the structure: what the naive idea repeats, what is worth remembering so it need not be repeated, and which data structure holds that. Work the question's own example by hand.>

![<One sentence: what the figure shows, with the concrete values drawn in it>](figures/<name>.svg)

## Brute force

<Why it is the first thing to try, and why it is correct.>

```java
<plain Java, 10 to 20 lines, one comment per decision>
```

**Complexity:** time O(...), extra space O(...).

## Better: <name>

<What it changes and why that helps.>

```java
<code>
```

**Complexity:** time O(...), extra space O(...).

**Prefer it when:** <the situation where this beats the optimal one, such as sorted input or no spare memory.>

<!-- One or more "## Better: <name>" sections; repeat the block above for each. -->

## Optimal: <name>

<The idea in a few sentences.>

```java
<code>
```

**Complexity:** time O(...), extra space O(...).

## Complexity

| Approach | Time | Extra space |
| --- | --- | --- |
| Brute force | O(...) | O(...) |
| Better: <name> | O(...) | O(...) |
| Optimal: <name> | O(...) | O(...) |

## Where it shows up

<The coding-round problems in coveredBy[], each with one line on how it uses the idea, then real-world uses. When coveredBy is empty, real-world uses only.>

## Follow-up answers

**<Follow-up question from problem.json>** <The answer in two to four sentences.>
