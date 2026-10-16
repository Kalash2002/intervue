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

    /** Position k of the result holds original node k/2 from the front (even k) or from the back (odd k). */
    private static int source(int k, int n) {
        return k % 2 == 0 ? k / 2 : n - 1 - k / 2;
    }

    private static void expect(int[] values, int[] want) {
        int n = values.length;
        ListNode head = ListNode.of(values);
        List<ListNode> before = nodes(head, n);
        ReorderList.reorder(head);
        List<ListNode> after = nodes(head, n);
        int[] got = new int[after.size()];
        for (int i = 0; i < got.length; i++) {
            got[i] = after.get(i).val;
        }
        sameValues(want, got);
        for (int k = 0; k < n; k++) {
            assertTrue(after.get(k) == before.get(source(k, n)), "move the nodes themselves; do not change their values");
        }
    }

    @Test(order = 1)
    void sampleOddLength() {
        expect(new int[] {10, 20, 30, 40, 50}, new int[] {10, 50, 20, 40, 30});
    }

    @Test(order = 2)
    void evenLength() {
        expect(new int[] {7, 1, 7, 3}, new int[] {7, 3, 1, 7});
        expect(new int[] {6, 5, 4, 3, 2, 1}, new int[] {6, 1, 5, 2, 4, 3});
    }

    @Test(order = 3)
    void twoNodesStayAsTheyAre() {
        expect(new int[] {9, 2}, new int[] {9, 2});
    }

    @Test(order = 4)
    void singleNode() {
        expect(new int[] {-4}, new int[] {-4});
    }

    @Test(order = 5)
    void threeNodes() {
        expect(new int[] {1, 2, 3}, new int[] {1, 3, 2});
    }

    @Test(order = 6)
    void repeatedValuesMoveAsNodes() {
        expect(new int[] {0, 0, 0, 0, 0, 1}, new int[] {0, 1, 0, 0, 0, 0});
    }

    @Test(order = 7)
    void everyLengthFromOneToTwelve() {
        for (int n = 1; n <= 12; n++) {
            int[] values = new int[n];
            int[] want = new int[n];
            for (int i = 0; i < n; i++) {
                values[i] = 100 * i - 1_000_000; // distinct, and at the low end of the range
            }
            for (int k = 0; k < n; k++) {
                want[k] = values[source(k, n)];
            }
            expect(values, want);
        }
    }

    @Test(order = 8, timeoutMs = 1500)
    void longList() {
        int n = 200_000;
        int[] values = new int[n];
        int[] want = new int[n];
        for (int i = 0; i < n; i++) {
            values[i] = i * 31 % 2_000_001 - 1_000_000; // stays within -10^6 .. 10^6
        }
        for (int k = 0; k < n; k++) {
            want[k] = values[source(k, n)];
        }
        expect(values, want);
    }
}
