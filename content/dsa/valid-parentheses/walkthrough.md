# How a strong round goes: Valid Parentheses

## Approach

Brackets close in the reverse order they open, so the one that must close next is always the most recent opener that is still waiting. That is exactly what a stack gives me. I push every opener. On a closer I check two things: there is something open, and the top is the same kind. If either fails the string is wrong. At the end the stack must be empty, otherwise some opener was never closed. Counting each kind is not enough, because it accepts crossed pairs such as `([)]`.

```java
static boolean isBalanced(String s) {
    char[] stack = new char[s.length()]; // the openers still waiting, latest on top
    int top = 0;
    for (int i = 0; i < s.length(); i++) {
        char c = s.charAt(i);
        if (c == '(' || c == '[' || c == '{') {
            stack[top++] = c;
        } else {
            if (top == 0) {
                return false; // a closer with nothing open
            }
            char opener = stack[--top];
            if (!matches(opener, c)) {
                return false; // wrong kind, or crossed pairs
            }
        }
    }
    return top == 0; // anything left over was never closed
}
```

## Complexity

| Approach | Time | Extra space |
| --- | --- | --- |
| Delete matched pairs again and again until nothing changes | O(n squared) | O(n) |
| One counter per bracket kind | O(n) | O(1), but wrong on crossed pairs |
| Stack of openers | O(n) | O(n) in the worst case, O(depth) otherwise |

## Where it shows up

`build-log-bracket-checker` is this question grown up: phase 1 asks the same yes-or-no, and the later phases keep the stack but store the column of each opener so the checker can point at the first error. Outside that problem, any parser of nested text uses it: HTML tags, JSON, expressions in a calculator, and undo scopes.

## Follow-up answers

**How would you report the position of the first mistake?** Store the index of each opener on the stack instead of the character, since the character is one lookup away. A bad closer is reported at its own index; if the scan ends with openers left, report the bottom one, the earliest still open.

**What if the text also contains other characters that should be ignored?** Skip them in the loop: only brackets push or pop. The checks are unchanged.

**What if the string is too large to hold in memory?** Read it in a stream and keep only the stack. Its size is the nesting depth, not the text length. If even that is too deep for memory, spill the bottom of the stack to disk, since only the top is touched.
