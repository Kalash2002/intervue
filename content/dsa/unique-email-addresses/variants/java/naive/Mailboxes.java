import java.util.ArrayList;
import java.util.List;

/** Counts mailboxes behind a list of addresses. Checks each address against every one counted so far. */
public class Mailboxes {

    static int countDistinct(String[] emails) {
        List<String> seen = new ArrayList<>();
        for (String email : emails) {
            String plain = normalise(email);
            if (!seen.contains(plain)) {
                seen.add(plain);
            }
        }
        return seen.size();
    }

    private static String normalise(String email) {
        int at = email.indexOf('@');
        StringBuilder name = new StringBuilder(at);
        for (int i = 0; i < at; i++) {
            char c = email.charAt(i);
            if (c == '+') {
                break;
            }
            if (c != '.') {
                name.append(c);
            }
        }
        return name.append(email, at, email.length()).toString();
    }
}
