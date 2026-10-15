import static kit.Check.assertEquals;

import kit.Test;

public class Phase1Tests {

    @Test(order = 1)
    void sample() {
        assertEquals(1, Mailboxes.countDistinct(new String[] {"sam.k@mail.org", "samk+news@mail.org", "sa.mk@mail.org"}));
        assertEquals(3, Mailboxes.countDistinct(new String[] {"lee@a.com", "lee@b.com", "lee@ac.om"}));
        assertEquals(2, Mailboxes.countDistinct(new String[] {"ann+1@z.net", "ann+2@z.net", "a.nnb@z.net"}));
    }

    @Test(order = 2)
    void emptyAndSingle() {
        assertEquals(0, Mailboxes.countDistinct(new String[] {}));
        assertEquals(1, Mailboxes.countDistinct(new String[] {"a@b.c"}));
    }

    @Test(order = 3)
    void sameAddressRepeated() {
        assertEquals(1, Mailboxes.countDistinct(new String[] {"a@b.c", "a@b.c", "a@b.c"}));
    }

    @Test(order = 4)
    void dotsInTheNameAreIgnored() {
        assertEquals(1, Mailboxes.countDistinct(new String[] {"a.b.c@d.e", "abc@d.e", "ab.c@d.e", "a..b.c@d.e"}));
    }

    @Test(order = 5)
    void everythingAfterTheFirstPlusIsIgnored() {
        assertEquals(1, Mailboxes.countDistinct(
                new String[] {"a+b+c@d.e", "a@d.e", "a+@d.e", "a+b.c@d.e", "a.+b@d.e"}));
    }

    @Test(order = 6)
    void dotsInTheDomainAreKept() {
        assertEquals(3, Mailboxes.countDistinct(new String[] {"a@b.c", "a@bc", "a@b.d"}));
    }

    @Test(order = 7)
    void differentNamesStayDifferent() {
        assertEquals(2, Mailboxes.countDistinct(new String[] {"ab@x.y", "ba@x.y", "a.b@x.y", "b.a@x.y"}));
        assertEquals(2, Mailboxes.countDistinct(new String[] {"abc+z@x.y", "ab+cz@x.y"}));
    }

    @Test(order = 8)
    void sameNameOnDifferentDomains() {
        assertEquals(2, Mailboxes.countDistinct(new String[] {"a.b@x.y", "ab+q@x.y", "ab@z.y", "a.b+r@z.y"}));
    }

    @Test(order = 9, timeoutMs = 2000)
    void oneHundredThousandAddressesForSeventyThousandMailboxes() {
        int total = 100_000;
        int mailboxes = 70_000;
        String[] emails = new String[total];
        for (int i = 0; i < total; i++) {
            int k = i % mailboxes; // address i reaches mailbox k, and every k from 0 to 69,999 occurs
            StringBuilder code = new StringBuilder("mailbox");
            for (int d = 0, rest = k; d < 6; d++, rest /= 26) {
                code.append((char) ('a' + rest % 26)); // six letters, different for every k
            }
            StringBuilder name = new StringBuilder();
            for (int p = 0; p < code.length(); p++) {
                name.append(code.charAt(p));
                if ((i + p) % 4 == 0) {
                    name.append('.'); // dots in different places for different addresses
                }
            }
            if (i % 3 == 0) {
                name.append("+ne.ws"); // a tag with a dot of its own
            }
            emails[i] = name + "@" + (k % 3 == 0 ? "alpha.example.com" : "beta.example.com");
        }
        assertEquals(mailboxes, Mailboxes.countDistinct(emails));
    }
}
