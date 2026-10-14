import static kit.Check.assertEquals;

import java.util.List;
import kit.Test;

/** A network of 100,000 users. Each test has a time limit; a correct but slow solution fails with TIMEOUT. */
public class Phase3Tests {

    @Test(order = 1, timeoutMs = 4000)
    void suggestionsForFourThousandUsers() {
        FriendFinder finder = new FriendFinder(Datasets.network(100_000, 8, 41L));
        long checksum = 0;
        for (int u = 0; u < 100_000; u += 25) {
            for (Suggestion s : finder.suggestions("U" + u, 3)) {
                checksum = checksum * 31 + Long.parseLong(s.user().substring(1)) * 7 + s.mutualFriends();
            }
        }
        assertEquals(EXPECTED_SUGGESTIONS, checksum, "checksum over suggestions");
    }

    @Test(order = 2, timeoutMs = 5000)
    void degreesApartAcrossTheNetwork() {
        FriendFinder finder = new FriendFinder(Datasets.network(100_000, 8, 43L));
        List<Integer> steps = List.of(
                finder.degreesApart("U0", "U99999"),
                finder.degreesApart("U17", "U81234"),
                finder.degreesApart("U50000", "U3"));
        assertEquals(EXPECTED_STEPS, steps);
    }

    static final long EXPECTED_SUGGESTIONS = -1_265_601_989_629_665_947L;
    static final List<Integer> EXPECTED_STEPS = List.of(115, 93, 59);
}
