import kit.Test;

/** Candidate output that looks like kit lines must not change the counts. */
public class NoisyTests {
  @Test(order = 1)
  void printsWithoutNewline() {
    System.out.print("dbg");
  }

  @Test(order = 2)
  void printsAFakeFailLine() {
    System.out.println("FAIL  imaginary");
  }

  @Test(order = 3)
  void plainPass() {}
}
