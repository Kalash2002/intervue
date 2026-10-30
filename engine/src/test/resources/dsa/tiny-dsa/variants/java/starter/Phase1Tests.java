import kit.Check;
import kit.Test;

public class Phase1Tests {
  @Test(order = 1)
  void doubles() {
    Check.assertEquals(4, Calc.twice(2));
  }
}
