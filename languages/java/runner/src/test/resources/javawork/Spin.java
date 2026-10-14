public class Spin {
  public static void main(String[] args) throws Exception {
    new ProcessBuilder(
            System.getProperty("java.home") + "/bin/java",
            "-cp",
            System.getProperty("java.class.path"),
            "Spin$Child")
        .start();
    while (true) {
      Thread.onSpinWait();
    }
  }

  static class Child {
    public static void main(String[] args) throws Exception {
      Thread.sleep(600_000);
    }
  }
}
