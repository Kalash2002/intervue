public class Main {
    public static void main(String[] args) {
        Trie trie = new Trie();
        trie.insert("tram");
        trie.insert("trap");
        trie.insert("tab");
        System.out.println(trie.search("tra") + " " + trie.startsWith("tra") + " " + trie.search("trap"));
    }
}
