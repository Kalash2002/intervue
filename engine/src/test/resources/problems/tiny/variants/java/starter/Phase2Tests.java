import kit.Check;
import kit.Test;

public class Phase2Tests {
  @Test(order = 1)
  void sumsSmall() {
    Check.assertEquals(6, Calc.sum(new int[] {1, 2, 3}));
  }

  @Test(order = 2)
  void sumsEmpty() {
    Check.assertEquals(0, Calc.sum(new int[0]));
  }
}
