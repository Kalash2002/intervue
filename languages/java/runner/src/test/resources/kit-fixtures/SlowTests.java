import kit.Test;

public class SlowTests {
  @Test(order = 1, timeoutMs = 300)
  void sleepsPastItsLimit() throws InterruptedException {
    Thread.sleep(60_000);
  }

  @Test(order = 2, timeoutMs = 2000)
  void runsAfterTheTimeoutInAFreshJvm() {}
}
