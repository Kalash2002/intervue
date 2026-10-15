/** Evaluates arithmetic expressions written in postfix notation. */
public class Postfix {

    /** Returns the value of the postfix expression; division rounds towards zero. */
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
}
