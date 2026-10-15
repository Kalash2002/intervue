import static kit.Check.assertEquals;
import static kit.Check.assertFalse;
import static kit.Check.assertThrows;
import static kit.Check.assertTrue;

import java.nio.file.Path;
import java.util.Set;
import kit.Test;

public class Phase1Tests {

    private static SocialGraph sample() throws Exception {
        return GraphParser.fromFile(Path.of("data/friends.csv"));
    }

    @Test(order = 1)
    void lonelyAndUnknownUsersHaveNoFriends() throws Exception {
        SocialGraph graph = sample();
        assertEquals(Set.of(), graph.friendsOf("gus"));
        assertEquals(Set.of(), graph.friendsOf("nobody"));
        assertThrows(IllegalArgumentException.class, () -> graph.addFriendship("ana", "ana"));
    }

    @Test(order = 2)
    void theFirstNamedSideSeesItsFriends() throws Exception {
        assertEquals(Set.of("ben", "cai"), sample().friendsOf("ana"));
    }

    @Test(order = 3)
    void friendshipGoesBothWays() throws Exception {
        FriendFinder finder = new FriendFinder(sample());
        assertTrue(finder.areFriends("ana", "ben"), "ana is friends with ben");
        assertTrue(finder.areFriends("ben", "ana"), "ben is friends with ana");
        assertFalse(finder.areFriends("ana", "dev"), "ana is not friends with dev");
    }

    @Test(order = 4)
    void nobodyIsTheirOwnFriend() throws Exception {
        SocialGraph graph = sample();
        for (String user : graph.users()) {
            assertFalse(graph.friendsOf(user).contains(user), user + " is listed as their own friend");
        }
        assertEquals(Set.of("ana", "cai", "dev"), graph.friendsOf("ben"));
    }

    @Test(order = 5)
    void mutualFriendsOnTheSample() throws Exception {
        FriendFinder finder = new FriendFinder(sample());
        assertEquals(2, finder.mutualFriends("ana", "dev"));
        assertEquals(2, finder.mutualFriends("dev", "ana"));
        assertEquals(0, finder.mutualFriends("ana", "gus"));
    }
}
