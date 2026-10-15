import kit.Test;

public class ExitTests {
  @Test(order = 1)
  void killsItsOwnJvm() {
    Runtime.getRuntime().halt(3);
  }
}
