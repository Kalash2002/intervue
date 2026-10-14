import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Reads parcels from CSV with a header line: {@code id,kg}. Weights are kilograms with up to three decimals. */
public final class ParcelParser {
    private ParcelParser() {}

    public static List<Parcel> fromFile(Path file) throws IOException {
        List<String> lines = Files.readAllLines(file);
        List<Parcel> parcels = new ArrayList<>();
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).strip();
            if (line.isEmpty()) {
                continue;
            }
            String[] cells = line.split(",");
            if (cells.length != 2) {
                throw new IllegalArgumentException("line " + (i + 1) + ": expected 2 cells but found " + cells.length);
            }
            int grams = new BigDecimal(cells[1].strip()).movePointRight(3).intValueExact();
            parcels.add(new Parcel(cells[0].strip(), grams));
        }
        return parcels;
    }
}
