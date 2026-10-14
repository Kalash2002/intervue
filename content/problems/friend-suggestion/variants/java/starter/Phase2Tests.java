import static kit.Check.assertEquals;
import static kit.Check.assertThrows;

import java.nio.file.Path;
import java.util.List;
import kit.Test;

public class Phase2Tests {

    private static FriendFinder sample() throws Exception {
        return new FriendFinder(GraphParser.fromFile(Path.of("data/friends.csv")));
    }

    @Test(order = 1)
    void suggestsFriendsOfFriendsWithTheirSharedCount() throws Exception {
        assertEquals(List.of(new Suggestion("dev", 2)), sample().suggestions("ana", 5));
    }

    @Test(order = 2)
    void ranksByMutualFriendsThenId() throws Exception {
        FriendFinder f = sample();
        assertEquals(List.of(new Suggestion("ana", 2), new Suggestion("fay", 1)), f.suggestions("dev", 5));
        assertEquals(List.of(new Suggestion("ben", 1), new Suggestion("cai", 1)), f.suggestions("eli", 5));
        assertEquals(List.of(new Suggestion("ben", 1)), f.suggestions("eli", 1));
    }

    @Test(order = 3)
    void suggestionsEdgeCases() throws Exception {
        FriendFinder f = sample();
        assertEquals(List.of(), f.suggestions("gus", 3));
        assertEquals(List.of(), f.suggestions("ana", 0));
        assertThrows(IllegalArgumentException.class, () -> f.suggestions("ana", -1));
        assertThrows(IllegalArgumentException.class, () -> f.suggestions("nobody", 3));
    }

    @Test(order = 4)
    void degreesApartCountsTheShortestPath() throws Exception {
        FriendFinder f = sample();
        assertEquals(1, f.degreesApart("ana", "ben"));
        assertEquals(2, f.degreesApart("ana", "dev"));
        assertEquals(4, f.degreesApart("ana", "fay"));
        assertEquals(4, f.degreesApart("fay", "ana"));
    }

    @Test(order = 5)
    void degreesApartEdgeCases() throws Exception {
        FriendFinder f = sample();
        assertEquals(0, f.degreesApart("eli", "eli"));
        assertEquals(-1, f.degreesApart("ana", "gus"));
        assertThrows(IllegalArgumentException.class, () -> f.degreesApart("ana", "nobody"));
        assertThrows(IllegalArgumentException.class, () -> f.degreesApart("nobody", "ana"));
    }
}
