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
            assertTrue(out.size() < limit, "the list does not end: some node still links back");
            out.add(n);
        }
        return out;
    }

    private static void expect(int[] values, int[] reversed) {
        ListNode head = ListNode.of(values);
        List<ListNode> before = nodes(head, values.length);
        List<ListNode> after = nodes(ReverseList.reverse(head), values.length);
        int[] got = new int[after.size()];
        for (int i = 0; i < got.length; i++) {
            got[i] = after.get(i).val;
        }
        sameValues(reversed, got);
        for (int i = 0; i < after.size(); i++) {
            assertTrue(after.get(i) == before.get(before.size() - 1 - i), "relink the given nodes; do not create new ones");
        }
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

    @Test(order = 1)
    void sample() {
        expect(new int[] {8, 3, 5, 1}, new int[] {1, 5, 3, 8});
    }

    @Test(order = 2)
    void twoNodes() {
        expect(new int[] {6, -2}, new int[] {-2, 6});
    }

    @Test(order = 3)
    void singleNode() {
        expect(new int[] {42}, new int[] {42});
    }

    @Test(order = 4)
    void emptyList() {
        assertTrue(ReverseList.reverse(null) == null, "an empty list reverses to an empty list (null)");
    }

    @Test(order = 5)
    void repeatedValuesKeepTheirNodes() {
        expect(new int[] {4, 4, 9, 4}, new int[] {4, 9, 4, 4});
    }

    @Test(order = 6)
    void oldHeadBecomesTheTail() {
        ListNode head = ListNode.of(10, 20, 30);
        ListNode got = ReverseList.reverse(head);
        assertEquals(30, got.val);
        assertTrue(head.next == null, "the old head is now the last node, so its next must be null");
    }

    @Test(order = 7)
    void valuesAtTheLimits() {
        expect(new int[] {-1_000_000, 1_000_000, 0}, new int[] {0, 1_000_000, -1_000_000});
    }

    @Test(order = 8, timeoutMs = 2000)
    void longList() {
        int n = 100_000; // deep enough to overflow the stack of a recursive reversal
        int[] values = new int[n];
        int[] reversed = new int[n];
        for (int i = 0; i < n; i++) {
            values[i] = i * 13 % 2_000_001 - 1_000_000; // stays within -10^6 .. 10^6
            reversed[n - 1 - i] = values[i];
        }
        expect(values, reversed);
    }
}
