import static kit.Check.assertEquals;
import static kit.Check.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import kit.Test;

public class Phase1Tests {

    /** The nodes from head to the end; fails when there are more than limit (a loop). */
    private static List<ListNode> nodes(ListNode head, int limit) {
        List<ListNode> out = new ArrayList<>();
        for (ListNode n = head; n != null; n = n.next) {
            assertTrue(out.size() < limit, "the list is too long: no node was removed, or some node links back");
            out.add(n);
        }
        return out;
    }

    /** Whole lists in the message when short; the first difference when long. */
    private static void sameValues(int[] want, int[] got) {
        if (want.length <= 30 && got.length <= 30) {
            assertEquals(Arrays.toString(want), Arrays.toString(got));
            return;
        }
        assertEquals(want.length, got.length, "number of nodes");
        for (int i = 0; i < want.length; i++) {
            assertEquals(want[i], got[i], "value at position " + i);
        }
    }

    private static void expect(int[] values, int n, int[] want) {
        ListNode head = ListNode.of(values);
        List<ListNode> before = nodes(head, values.length);
        List<ListNode> after = nodes(RemoveFromEnd.remove(head, n), values.length - 1);
        int[] got = new int[after.size()];
        for (int i = 0; i < got.length; i++) {
            got[i] = after.get(i).val;
        }
        sameValues(want, got);
        int removed = values.length - n;
        for (int k = 0; k < after.size(); k++) {
            assertTrue(after.get(k) == before.get(k < removed ? k : k + 1), "remove exactly the n-th node from the end and keep the others as they are");
        }
    }

    @Test(order = 1)
    void sample() {
        expect(new int[] {6, 11, 2, 19, 30}, 2, new int[] {6, 11, 2, 30});
    }

    @Test(order = 2)
    void removeTheHead() {
        expect(new int[] {3, 6, 9}, 3, new int[] {6, 9});
    }

    @Test(order = 3)
    void removeTheLastNode() {
        expect(new int[] {3, 6, 9}, 1, new int[] {3, 6});
    }

    @Test(order = 4)
    void singleNodeLeavesAnEmptyList() {
        expect(new int[] {12}, 1, new int[] {});
    }

    @Test(order = 5)
    void twoNodes() {
        expect(new int[] {5, -5}, 1, new int[] {5});
        expect(new int[] {5, -5}, 2, new int[] {-5});
    }

    @Test(order = 6)
    void repeatedValuesRemoveTheRightNode() {
        expect(new int[] {5, 5, 5, 5}, 3, new int[] {5, 5, 5});
        expect(new int[] {2, 7, 2, 7, 2}, 4, new int[] {2, 2, 7, 2});
    }

    @Test(order = 7)
    void valuesAtTheLimits() {
        expect(new int[] {1_000_000, -1_000_000, 0, -1_000_000}, 2, new int[] {1_000_000, -1_000_000, -1_000_000});
    }

    @Test(order = 8, timeoutMs = 2000)
    void longList() {
        int size = 100_000;
        int[] values = new int[size];
        for (int i = 0; i < size; i++) {
            values[i] = i * 17 % 2_000_001 - 1_000_000; // stays within -10^6 .. 10^6
        }
        for (int n : new int[] {1, size, 37_000}) { // the last node, the head, and one in between
            int[] want = new int[size - 1];
            int removed = size - n;
            for (int k = 0; k < want.length; k++) {
                want[k] = values[k < removed ? k : k + 1];
            }
            expect(values, n, want);
        }
    }
}
