import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Reads the access log, a CSV with a header line: {@code client,at}. Times are seconds since the log
 * started, with up to three decimals, for example {@code 2.006}.
 */
public final class LogParser {
    private LogParser() {}

    public static RequestLog fromFile(Path file) throws IOException {
        return fromLines(Files.readAllLines(file));
    }

    public static RequestLog fromLines(List<String> lines) {
        RequestLog log = new RequestLog();
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).strip();
            if (line.isEmpty()) {
                continue;
            }
            String[] cells = line.split(",");
            if (cells.length != 2) {
                throw new IllegalArgumentException("line " + (i + 1) + ": expected 2 cells but found " + cells.length);
            }
            log.add(new Request(cells[0].strip(), parseMillis(cells[1].strip())));
        }
        return log;
    }

    /**
     * Seconds with up to three decimals, as milliseconds: "2.006" is 2006. Decimal arithmetic, because
     * 2.006 has no exact binary double and the product with 1000 lands just below 2006.
     */
    public static long parseMillis(String seconds) {
        return new BigDecimal(seconds).movePointRight(3).longValueExact();
    }
}
