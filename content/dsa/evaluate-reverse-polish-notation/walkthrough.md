# How a strong round goes: Evaluate Reverse Polish Notation

## Approach

In postfix an operator applies to the two values that were read most recently and not yet used, so a stack fits: push numbers as they come, and at an operator pop two values, apply it, and push the result. The first value popped is the right operand and the second is the left, which matters for `-` and `/`: for `3 9 -` I pop 9 first, then 3, and compute 3 - 9. A token is an operator only when it is exactly one of the four symbols; `"-5"` has two characters and is a number, so I check the length before deciding. Java integer division already rounds towards zero, which is what the statement asks for, so `-7 / 2` is `-3` with no extra work (floor division would give -4). When the loop ends, the one value left on the stack is the answer. A plain array as the stack is enough, since the stack never holds more values than there are tokens.

```java
static int evaluate(String[] tokens) {
    int[] stack = new int[tokens.length];
    int size = 0;
    for (String token : tokens) {
        if (token.length() == 1 && "+-*/".indexOf(token.charAt(0)) >= 0) {
            int right = stack[--size]; // the value read last is the right operand
            int left = stack[--size];
            int result = switch (token.charAt(0)) {
                case '+' -> left + right;
                case '-' -> left - right;
                case '*' -> left * right;
                default -> left / right; // Java rounds towards zero
            };
            stack[size++] = result;
        } else {
            stack[size++] = Integer.parseInt(token);
        }
    }
    return stack[0];
}
```

## Complexity

| Approach | Time | Extra space |
| --- | --- | --- |
| Stack of values, one pass | O(n) | O(n) |

## Where it shows up

Postfix evaluation is how calculators and spreadsheet engines run a formula after parsing it, and how many bytecode virtual machines work: instructions push operands onto a stack and operators consume them. Query builders also compile filters into a postfix list so that evaluation needs no recursion.

## Follow-up answers

**How would you convert an ordinary infix expression with brackets into this form?** Use the shunting-yard method: read tokens, send numbers straight to the output, and keep operators on a stack, popping those of higher or equal precedence before pushing a new one. A left bracket is pushed, and a right bracket pops operators to the output until the left bracket is found.

**How would you report a malformed expression instead of assuming it is valid?** Check that the stack holds at least two values before each operator, throw a clear error if it does not, and check that exactly one value remains at the end. Also reject tokens that are neither numbers nor known operators.

**What changes if the numbers can be decimals?** Store values as `double`, parse with `Double.parseDouble`, and use ordinary floating-point division. The rounding rule for division no longer applies, and you would need to decide how to treat division by zero.
