import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Reads the roster from CSV with a header line: {@code nurse,ward,start,end}. */
public final class RosterParser {
    private RosterParser() {}

    public static Roster fromFile(Path file) throws IOException {
        List<String> lines = Files.readAllLines(file);
        List<Shift> shifts = new ArrayList<>();
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).strip();
            if (line.isEmpty()) {
                continue;
            }
            String[] cells = line.split(",");
            if (cells.length != 4) {
                throw new IllegalArgumentException("line " + (i + 1) + ": expected 4 cells but found " + cells.length);
            }
            shifts.add(new Shift(cells[0].strip(), cells[1].strip(),
                    Integer.parseInt(cells[2].strip()), Integer.parseInt(cells[3].strip())));
        }
        return Roster.of(shifts);
    }
}
