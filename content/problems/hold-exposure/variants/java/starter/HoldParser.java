import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Reads holds from CSV with a header line: {@code id,account,amount,start,end}.
 * Amounts are in rupees with up to two decimals, for example {@code 1250.50}.
 */
public final class HoldParser {
    private HoldParser() {}

    public static HoldBook fromFile(Path file) throws IOException {
        return fromLines(Files.readAllLines(file));
    }

    public static HoldBook fromLines(List<String> lines) {
        HoldBook book = new HoldBook();
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).strip();
            if (line.isEmpty()) {
                continue;
            }
            String[] cells = line.split(",");
            if (cells.length != 5) {
                throw new IllegalArgumentException("line " + (i + 1) + ": expected 5 cells but found " + cells.length);
            }
            long amountMinor = new BigDecimal(cells[2].strip()).movePointRight(2).longValueExact();
            book.add(new Hold(cells[0].strip(), cells[1].strip(), amountMinor,
                    Integer.parseInt(cells[3].strip()), Integer.parseInt(cells[4].strip())));
        }
        return book;
    }
}
