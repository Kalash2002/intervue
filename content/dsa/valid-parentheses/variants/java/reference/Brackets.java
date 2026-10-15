/** Checks that brackets are closed by the right kind in the right order. */
public class Brackets {

    /** Returns true when every bracket in s is matched and correctly nested. */
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

    private static boolean matches(char opener, char closer) {
        return (opener == '(' && closer == ')')
                || (opener == '[' && closer == ']')
                || (opener == '{' && closer == '}');
    }
}
