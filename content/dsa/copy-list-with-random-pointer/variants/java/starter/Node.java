import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;

/** A list node with a second, random link. Read-only: the tests build and check lists with the helpers. */
public class Node {
    int val;
    Node next;
    Node random;

    Node(int val) {
        this.val = val;
    }

    /** Builds a list from {value, randomIndex} pairs; a null index means no random link; no pairs gives null. */
    static Node of(Integer[][] pairs) {
        Node[] nodes = new Node[pairs.length];
        for (int i = 0; i < pairs.length; i++) {
            nodes[i] = new Node(pairs[i][0]);
        }
        for (int i = 0; i < pairs.length; i++) {
            nodes[i].next = i + 1 < pairs.length ? nodes[i + 1] : null;
            nodes[i].random = pairs[i][1] == null ? null : nodes[pairs[i][1]];
        }
        return pairs.length == 0 ? null : nodes[0];
    }

    /** Each node's position, counted from the head; fails on a list that loops back on itself. */
    private static Map<Node, Integer> positions(Node head) {
        Map<Node, Integer> index = new IdentityHashMap<>();
        for (Node n = head; n != null; n = n.next) {
            if (index.putIfAbsent(n, index.size()) != null) {
                throw new IllegalStateException("the list does not end: some node links back");
            }
        }
        return index;
    }

    /** Reads a list back as {value, randomIndex} pairs; fails on a loop or a random link that leaves the list. */
    static Integer[][] toPairs(Node head) {
        Map<Node, Integer> index = positions(head);
        Integer[][] pairs = new Integer[index.size()][];
        int i = 0;
        for (Node n = head; n != null; n = n.next, i++) {
            Integer target = n.random == null ? null : index.get(n.random);
            if (n.random != null && target == null) {
                throw new IllegalStateException("node " + i + " has a random link to a node outside its list");
            }
            pairs[i] = new Integer[] {n.val, target};
        }
        return pairs;
    }

    /**
     * Returns null when copy is a deep copy of original (same values, same next and random shape, and
     * not one node or link shared with original), otherwise what is wrong.
     */
    static String deepCopyProblem(Node original, Node copy) {
        Map<Node, Integer> originals = positions(original);
        int i = 0;
        for (Node n = copy; n != null && i <= originals.size(); n = n.next, i++) {
            if (originals.containsKey(n)) {
                return "node " + i + " of the copy is a node of the input; make new nodes";
            }
            if (n.random != null && originals.containsKey(n.random)) {
                return "the random link of copy node " + i + " points into the input list";
            }
        }
        if (i > originals.size()) {
            return "the copy has more nodes than the input, or some node links back";
        }
        if (i < originals.size()) {
            return "the copy has " + i + " nodes, the input has " + originals.size();
        }
        Map<Node, Integer> copies = positions(copy);
        Node a = original;
        Node b = copy;
        for (int k = 0; a != null; k++, a = a.next, b = b.next) {
            if (a.val != b.val) {
                return "copy node " + k + " holds " + b.val + ", the input node holds " + a.val;
            }
            Integer want = a.random == null ? null : originals.get(a.random);
            Integer got = b.random == null ? null : copies.get(b.random);
            if (b.random != null && got == null) {
                return "copy node " + k + " has a random link to a node outside the copy";
            }
            if (!Objects.equals(want, got)) {
                return "the random link of copy node " + k + " goes to index " + got + ", in the input to " + want;
            }
        }
        return null;
    }
}
