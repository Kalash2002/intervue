import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/** Small demo. Run it from the Run menu to check the sample log. */
public class Main {
    public static void main(String[] args) throws Exception {
        List<String> lines = Files.readAllLines(Path.of("data/build.log"));
        BracketChecker checker = new BracketChecker();
        for (int i = 0; i < lines.size(); i++) {
            System.out.println((i + 1) + (checker.isBalanced(lines.get(i)) ? "  ok   " : "  BAD  ") + lines.get(i));
        }
    }
}
