import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/** Reads merchants from a file with one merchant per line: {@code name|category|txnCount}. */
public final class DirectoryLoader {
    private DirectoryLoader() {}

    public static MerchantDirectory fromFile(Path file) throws IOException {
        return fromLines(Files.readAllLines(file));
    }

    public static MerchantDirectory fromLines(List<String> lines) {
        MerchantDirectory directory = new MerchantDirectory();
        for (String raw : lines) {
            String line = raw.strip();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            String[] cells = line.split("\\|");
            if (cells.length != 3) {
                throw new IllegalArgumentException("cannot read merchant line: " + raw);
            }
            directory.add(new Merchant(cells[0].strip(), cells[1].strip(), Long.parseLong(cells[2].strip())));
        }
        return directory;
    }
}
