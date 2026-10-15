/** Checks that brackets are closed by the right kind in the right order. */
public class Brackets {

    /** Returns true when every bracket in s is matched and correctly nested. */
    static boolean isBalanced(String s) {
        String current = s;
        while (true) {
            String next = current.replace("()", "").replace("[]", "").replace("{}", "");
            if (next.length() == current.length()) {
                return current.isEmpty(); // nothing left to remove: balanced only if nothing is left
            }
            current = next;
        }
    }
}
