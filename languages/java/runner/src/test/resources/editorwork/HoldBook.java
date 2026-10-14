import java.util.ArrayList;
import java.util.List;

public class HoldBook {
  private final List<String> holds = new ArrayList<>();
  static final int LIMIT = 10;

  public void add(String hold) {
    int local = 1;
    holds.add(hold);
  }

  int count(String account, int minute) {
    return holds.size();
  }

  record Entry(String id, long amount) {}

  interface Listener {
    void changed();
  }
}
