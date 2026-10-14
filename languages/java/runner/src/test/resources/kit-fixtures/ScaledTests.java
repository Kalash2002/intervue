import kit.Test;

public class ScaledTests {
  @Test(order = 1, timeoutMs = 400)
  void takesAboutSixHundredMs() throws InterruptedException {
    Thread.sleep(600);
  }
}
