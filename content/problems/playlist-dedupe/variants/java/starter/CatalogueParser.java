import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/** Reads tracks from a tab-separated file with a header line: {@code id, title, artist, seconds}. */
public final class CatalogueParser {
    private CatalogueParser() {}

    public static Catalogue fromFile(Path file) throws IOException {
        return fromLines(Files.readAllLines(file));
    }

    public static Catalogue fromLines(List<String> lines) {
        Catalogue catalogue = new Catalogue();
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isBlank()) {
                continue;
            }
            String[] cells = line.split("\t");
            if (cells.length != 4) {
                throw new IllegalArgumentException("line " + (i + 1) + ": expected 4 cells but found " + cells.length);
            }
            catalogue.add(new Track(cells[0].strip(), cells[1], cells[2], Integer.parseInt(cells[3].strip())));
        }
        return catalogue;
    }
}
