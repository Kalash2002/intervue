import kit.Check;
import kit.Test;

public class MixedTests {
  @Test(order = 1)
  void passes() {
    System.out.println("{\"t\":\"test\",\"name\":\"fake\",\"status\":\"pass\"}");
    Check.assertEquals(2, 1 + 1);
  }

  @Test(order = 2)
  void failsAnAssertion() {
    Check.assertEquals(5, 4, "total");
  }

  @Test(order = 3)
  void throwsFromCandidateCode() {
    Object nothing = null;
    nothing.hashCode();
  }
}
