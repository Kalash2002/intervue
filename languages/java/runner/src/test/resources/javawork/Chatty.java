public class Chatty {
  public static void main(String[] args) {
    String line = "x".repeat(99);
    for (int i = 0; i < 20_000; i++) {
      System.out.println(line);
    }
  }
}
