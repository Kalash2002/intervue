import kit.Check;
import kit.Test;

public class Phase2Tests {
  @Test(order = 1)
  void subtractsNotYetWritten() {
    Check.assertEquals(-1, Calc.add(2, 3));
  }
}
