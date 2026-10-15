public class Main {
    public static void main(String[] args) {
        String[] products = {"kiln", "kind", "kit", "kite", "lamp"};
        System.out.println(Autocomplete.suggest(products, "kit", 3));
    }
}
