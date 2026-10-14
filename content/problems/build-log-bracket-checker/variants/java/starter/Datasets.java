import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Builds the huge lines and logs used by the phase 3 tests. Same input, same output. */
public final class Datasets {
    private Datasets() {}

    private static final String OPENERS = "([{";
    private static final String CLOSERS = ")]}";

    /** {@code depth} openers, their closers in the right order, then one stray closer at the end. */
    public static String deeplyNested(int depth) {
        StringBuilder line = new StringBuilder(2 * depth + 1);
        for (int i = 0; i < depth; i++) {
            line.append(OPENERS.charAt(i % 3));
        }
        for (int i = depth - 1; i >= 0; i--) {
            line.append(CLOSERS.charAt(i % 3));
        }
        return line.append(')').toString();
    }

    /** Log lines of about 80 characters with some quoted text; a few are broken in different ways. */
    public static List<String> buildLog(int lines, long seed) {
        Random random = new Random(seed);
        List<String> log = new ArrayList<>(lines);
        for (int n = 0; n < lines; n++) {
            StringBuilder line = new StringBuilder("step" + n + " ");
            int depth = 0;
            StringBuilder stack = new StringBuilder();
            while (line.length() < 80) {
                int pick = random.nextInt(10);
                if (pick < 3) {
                    int b = random.nextInt(3);
                    line.append(OPENERS.charAt(b));
                    stack.append(CLOSERS.charAt(b));
                    depth++;
                } else if (pick < 6 && depth > 0) {
                    line.append(stack.charAt(--depth));
                    stack.setLength(depth);
                } else if (pick == 6) {
                    line.append(" \"msg ) [x\\\" ] \" ");
                } else {
                    line.append((char) ('a' + random.nextInt(26)));
                }
            }
            while (depth > 0) {
                line.append(stack.charAt(--depth));
            }
            int breakage = random.nextInt(50);
            if (breakage == 0) {
                line.append(']');
            } else if (breakage == 1) {
                line.insert(5, '{');
            } else if (breakage == 2) {
                line.append(" \"unterminated");
            }
            log.add(line.toString());
        }
        return log;
    }
}
