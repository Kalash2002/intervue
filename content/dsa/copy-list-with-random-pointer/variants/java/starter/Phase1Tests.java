import static kit.Check.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import kit.Test;

public class Phase1Tests {

    /** Copies the list and checks the copy, and that every node of the input still has its value and links. */
    private static void expect(Integer[][] pairs) {
        Node head = Node.of(pairs);
        List<Node> nodes = new ArrayList<>();
        for (Node n = head; n != null; n = n.next) {
            nodes.add(n);
        }
        Node copy = CopyRandomList.copy(head);
        for (int i = 0; i < nodes.size(); i++) {
            Node n = nodes.get(i);
            Node next = i + 1 < nodes.size() ? nodes.get(i + 1) : null;
            Node random = pairs[i][1] == null ? null : nodes.get(pairs[i][1]);
            assertTrue(n.val == pairs[i][0] && n.next == next && n.random == random,
                    "input node " + i + " was changed; leave the input list as it was");
        }
        String problem = Node.deepCopyProblem(head, copy);
        assertTrue(problem == null, problem);
    }

    @Test(order = 1)
    void sample() {
        expect(new Integer[][] {{6, 2}, {1, null}, {9, 0}, {4, 3}});
    }

    @Test(order = 2)
    void equalValuesWithCrossedLinks() {
        expect(new Integer[][] {{5, 1}, {5, 0}});
        expect(new Integer[][] {{-3, 2}, {-3, null}, {-3, 1}, {-3, 2}});
    }

    @Test(order = 3)
    void emptyList() {
        assertTrue(CopyRandomList.copy(null) == null, "the copy of an empty list is an empty list (null)");
    }

    @Test(order = 4)
    void singleNodeLinkingToItself() {
        expect(new Integer[][] {{0, 0}});
    }

    @Test(order = 5)
    void noRandomLinks() {
        expect(new Integer[][] {{2, null}, {7, null}, {-1, null}});
    }

    @Test(order = 6)
    void linksThatPointForward() {
        expect(new Integer[][] {{11, 3}, {12, 3}, {13, 2}, {14, null}});
    }

    @Test(order = 7)
    void everyNodeLinksToTheHead() {
        expect(new Integer[][] {{8, 0}, {8, 0}, {2, 0}, {8, 0}, {2, 0}});
    }

    @Test(order = 8)
    void valuesAtTheLimits() {
        expect(new Integer[][] {{-10_000, 1}, {10_000, 0}, {0, null}});
    }

    @Test(order = 9, timeoutMs = 3500)
    void longList() {
        int n = 200_000;
        Random random = new Random(38);
        Integer[][] pairs = new Integer[n][];
        for (int i = 0; i < n; i++) {
            // few distinct values, so matching nodes by value goes wrong; one link in ten is null
            pairs[i] = new Integer[] {random.nextInt(10), random.nextInt(10) == 0 ? null : random.nextInt(n)};
        }
        expect(pairs);
    }
}
