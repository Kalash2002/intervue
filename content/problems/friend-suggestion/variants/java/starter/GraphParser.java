import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/** Reads a network: a header line, then {@code a,b} for a friendship or a lone {@code a} for a user with none. */
public final class GraphParser {
    private GraphParser() {}

    public static SocialGraph fromFile(Path file) throws IOException {
        List<String> lines = Files.readAllLines(file);
        SocialGraph graph = new SocialGraph();
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).strip();
            if (line.isEmpty()) {
                continue;
            }
            String[] cells = line.split(",");
            if (cells.length == 1) {
                graph.addUser(cells[0].strip());
            } else if (cells.length == 2) {
                graph.addFriendship(cells[0].strip(), cells[1].strip());
            } else {
                throw new IllegalArgumentException("line " + (i + 1) + ": expected 1 or 2 cells but found " + cells.length);
            }
        }
        return graph;
    }
}
