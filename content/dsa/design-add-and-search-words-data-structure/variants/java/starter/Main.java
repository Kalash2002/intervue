public class Main {
    public static void main(String[] args) {
        WordDictionary dictionary = new WordDictionary();
        dictionary.addWord("lamp");
        dictionary.addWord("limp");
        dictionary.addWord("lump");
        System.out.println(dictionary.search("l.mp") + " " + dictionary.search("l..p") + " " + dictionary.search("l.m"));
    }
}
