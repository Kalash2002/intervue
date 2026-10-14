# Friend Suggestion

A social app stores who is friends with whom. Friendship is **mutual**: if Ana is friends with Ben, Ben is friends with Ana. Nobody is their own friend.

The growth team wants two features on top of the graph: "people you may know" and "how many steps apart are we".

You have 60 minutes for three phases. Start by reading the code; `Main` prints the sample network from `data/friends.csv`.

| File | What it holds |
| --- | --- |
| `SocialGraph` | Users and friendships |
| `GraphParser` | Reads the network: one friendship per line, or a lone user |
| `Suggestion` | A suggested user and how many friends you share |
| `FriendFinder` | Questions about the graph. Most of your work is here. |
| `Datasets` | Builds the large networks for phase 3 |

Tests are read-only. You can add your own files.
