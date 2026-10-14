import kit.Test;

/** A static initializer that throws, as when a test field calls a broken constructor. */
public class BrokenInitTests {
  static final int VALUE = Integer.parseInt("not a number");

  @Test(order = 1)
  void neverRuns() {}
}
