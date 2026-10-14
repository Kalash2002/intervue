/** Turns display names and search text into a comparable form. */
public final class Normalizer {
    private Normalizer() {}

    /**
     * Lower case; every character that is not a letter a-z or a digit becomes a space;
     * runs of spaces collapse to one; no space at either end.
     * "Domino's  Pizza!" becomes "domino s pizza".
     */
    public static String normalize(String text) {
        StringBuilder out = new StringBuilder(text.length());
        boolean pendingSpace = false;
        for (int i = 0; i < text.length(); i++) {
            char c = Character.toLowerCase(text.charAt(i));
            boolean keep = (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9');
            if (!keep) {
                pendingSpace = out.length() > 0;
                continue;
            }
            if (pendingSpace) {
                out.append(' ');
                pendingSpace = false;
            }
            out.append(c);
        }
        return out.toString();
    }
}
