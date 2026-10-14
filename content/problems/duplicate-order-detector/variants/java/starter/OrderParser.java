import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Reads orders from CSV with a header line: {@code id,customer,sku,amount,minute}.
 * Amounts are in rupees with up to two decimals, for example {@code 499.50}.
 */
public final class OrderParser {
    private OrderParser() {}

    public static OrderLog fromFile(Path file) throws IOException {
        return fromLines(Files.readAllLines(file));
    }

    public static OrderLog fromLines(List<String> lines) {
        OrderLog log = new OrderLog();
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).strip();
            if (line.isEmpty()) {
                continue;
            }
            String[] cells = line.split(",");
            if (cells.length != 5) {
                throw new IllegalArgumentException("line " + (i + 1) + ": expected 5 cells but found " + cells.length);
            }
            long amountMinor = new BigDecimal(cells[3].strip()).movePointRight(2).longValueExact();
            log.add(new Order(cells[0].strip(), cells[1].strip(), cells[2].strip(), amountMinor,
                    Integer.parseInt(cells[4].strip())));
        }
        return log;
    }
}
