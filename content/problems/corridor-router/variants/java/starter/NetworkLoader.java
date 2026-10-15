import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Reads a network from text. One corridor per line:
 *
 * <pre>
 *   INR>USD 120        active corridor, fee 120
 *   INR>GBP 150 off    inactive corridor
 *   # comment
 * </pre>
 */
public final class NetworkLoader {
    private NetworkLoader() {}

    public static Network fromFile(Path file) throws IOException {
        return fromLines(Files.readAllLines(file));
    }

    public static Network fromLines(List<String> lines) {
        Network network = new Network();
        int lineNumber = 0;
        for (String raw : lines) {
            lineNumber++;
            String line = raw.strip();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            String[] parts = line.split("\\s+");
            String[] ends = parts[0].split(">");
            if (parts.length < 2 || ends.length != 2) {
                throw new IllegalArgumentException("line " + lineNumber + ": cannot read '" + raw + "'");
            }
            boolean active = !(parts.length > 2 && parts[2].equalsIgnoreCase("off"));
            network.add(new Corridor(ends[0], ends[1], Long.parseLong(parts[1]), active));
        }
        return network;
    }
}
