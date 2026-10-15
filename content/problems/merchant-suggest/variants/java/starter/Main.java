import java.nio.file.Path;

/** Small demo. Run it from the Run menu to look up a few merchants in the sample directory. */
public class Main {
    public static void main(String[] args) throws Exception {
        MerchantDirectory directory = DirectoryLoader.fromFile(Path.of("data/merchants.txt"));
        System.out.println(directory.size() + " merchants");
        for (String name : new String[] {"amazon pay", "BIG BAZAAR", "Cafe Coffee Day", "Swiggy", "zomato", "nobody"}) {
            System.out.println("find(\"" + name + "\") -> " + directory.find(name).map(Merchant::name).orElse("not found"));
        }
    }
}
