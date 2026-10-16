## The question

Add two numbers.

## How to think about it

Just add them.

![Two plus three is five](figures/add.svg)

## Brute force

```java
int add(int a, int b) { return a + b; }
```

**Complexity:** time O(1), extra space O(1).

## Better: Same thing

```java
int add(int a, int b) { return b + a; }
```

**Complexity:** time O(1), extra space O(1).

## Optimal: Add

```java
int add(int a, int b) { return a + b; }
```

**Complexity:** time O(1), extra space O(1).

## Complexity

| Approach | Time | Extra space |
| --- | --- | --- |
| Add | O(1) | O(1) |

## Where it shows up

Everywhere, for example [Tiny](dsa://tiny-dsa).

## Follow-up answers

**Why?** Because.
