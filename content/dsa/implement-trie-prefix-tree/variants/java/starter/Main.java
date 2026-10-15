public class Main {
    public static void main(String[] args) {
        Trie trie = new Trie();
        trie.insert("tram");
        System.out.println(trie.search("tram") + " " + trie.search("tra") + " " + trie.startsWith("tra"));
    }
}
