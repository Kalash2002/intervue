import java.util.HashSet;
import java.util.Set;

/** Counts mailboxes behind a list of addresses. Each address is reduced on its own, then a set counts. */
public class Mailboxes {

    static int countDistinct(String[] emails) {
        Set<String> mailboxes = new HashSet<>();
        for (String email : emails) {
            mailboxes.add(normalise(email));
        }
        return mailboxes.size();
    }

    private static String normalise(String email) {
        int at = email.indexOf('@');
        StringBuilder name = new StringBuilder(at);
        for (int i = 0; i < at; i++) {
            char c = email.charAt(i);
            if (c == '+') {
                break; // everything after the first plus is ignored
            }
            if (c != '.') {
                name.append(c);
            }
        }
        return name.append(email, at, email.length()).toString(); // the domain is kept as written
    }
}
