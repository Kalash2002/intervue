import static kit.Check.assertEquals;
import static kit.Check.assertTrue;

import kit.Test;

public class Phase1Tests {

    private static void expect(int[] values, int pos, boolean want) {
        assertEquals(want, CycleCheck.hasCycle(ListNode.withCycle(values, pos)), "values " + (values.length <= 30 ? java.util.Arrays.toString(values) : values.length + " nodes") + ", pos " + pos);
    }

    @Test(order = 1)
    void sample() {
        expect(new int[] {5, 1, 8, 2}, 1, true);
    }

    @Test(order = 2)
    void repeatedValuesAreNotACycle() {
        expect(new int[] {4, 4, 4}, -1, false);
        expect(new int[] {2, 7, 2, 7, 2, 7}, -1, false);
        expect(new int[] {2, 7, 2, 7, 2, 7}, 3, true);
    }

    @Test(order = 3)
    void singleNodePointingToItself() {
        expect(new int[] {13}, 0, true);
    }

    @Test(order = 4)
    void singleNodeWithoutACycle() {
        expect(new int[] {-13}, -1, false);
    }

    @Test(order = 5)
    void emptyList() {
        assertEquals(false, CycleCheck.hasCycle(null), "an empty list (null) has no cycle");
    }

    @Test(order = 6)
    void twoNodes() {
        expect(new int[] {7, -4}, -1, false);
        expect(new int[] {7, -4}, 0, true);
        expect(new int[] {7, -4}, 1, true);
    }

    @Test(order = 7)
    void cycleBackToTheHead() {
        expect(new int[] {3, 9, -6, 0, 1_000_000}, 0, true);
    }

    @Test(order = 8)
    void listIsLeftUnchanged() {
        int[] values = {-1_000_000, 6, 6, 0, 11, 6};
        ListNode head = ListNode.withCycle(values, 2);
        ListNode[] nodes = new ListNode[values.length];
        ListNode cur = head;
        for (int i = 0; i < values.length; i++) {
            nodes[i] = cur;
            cur = cur.next;
        }
        assertEquals(true, CycleCheck.hasCycle(head));
        for (int i = 0; i < values.length; i++) {
            assertEquals(values[i], nodes[i].val, "node " + i + " value must not change");
            ListNode next = i + 1 < values.length ? nodes[i + 1] : nodes[2];
            assertTrue(nodes[i].next == next, "node " + i + " link must not change");
        }
    }

    @Test(order = 9, timeoutMs = 500)
    void longListWithoutACycle() {
        expect(longValues(), -1, false);
    }

    @Test(order = 10, timeoutMs = 500)
    void longListWhoseTailLinksBack() {
        int[] values = longValues();
        expect(values, 0, true);
        expect(values, values.length - 1, true); // the last node points to itself
    }

    private static int[] longValues() {
        int[] values = new int[100_000];
        for (int i = 0; i < values.length; i++) {
            values[i] = i % 3; // many repeats: only node identity tells nodes apart
        }
        return values;
    }
}
