import java.nio.file.Path;

/** Small demo. Run it from the Run menu to see the sample network. */
public class Main {
    public static void main(String[] args) throws Exception {
        SocialGraph graph = GraphParser.fromFile(Path.of("data/friends.csv"));
        FriendFinder finder = new FriendFinder(graph);
        for (String user : graph.users()) {
            System.out.println(user + " -> " + graph.friendsOf(user));
        }
        System.out.println("ana and dev share " + finder.mutualFriends("ana", "dev") + " friends");
    }
}
