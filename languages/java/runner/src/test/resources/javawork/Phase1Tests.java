import kit.Check;
import kit.Test;

public class Phase1Tests {
  @Test(order = 1)
  void adds() {
    Check.assertEquals(5, Calc.add(2, 3));
  }
}
