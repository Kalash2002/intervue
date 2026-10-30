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
            assertTrue(out.size() < limit, "the list does not end: some node links back");
            out.add(n);
        }
        return out;
    }

    /** Position i of the answer holds the node that was at position source(i): full groups turned around. */
    private static int source(int i, int k, int length) {
        int start = i / k * k;
        return start + k <= length ? start + k - 1 - (i - start) : i;
    }

    private static void expect(int[] values, int k, int[] want) {
        ListNode head = ListNode.of(values);
        List<ListNode> before = nodes(head, values.length);
        List<ListNode> after = nodes(KGroup.reverse(head, k), values.length);
        int[] got = new int[after.size()];
        for (int i = 0; i < got.length; i++) {
            got[i] = after.get(i).val;
        }
        if (want.length <= 30 && got.length <= 30) {
            assertEquals(Arrays.toString(want), Arrays.toString(got));
        } else {
            assertEquals(want.length, got.length, "number of nodes");
            for (int i = 0; i < want.length; i++) {
                assertEquals(want[i], got[i], "value at position " + i);
            }
        }
        for (int i = 0; i < after.size(); i++) {
            assertTrue(after.get(i) == before.get(source(i, k, values.length)),
                    "relink the given nodes; do not create new ones or move values between them");
        }
    }

    @Test(order = 1)
    void sample() {
        expect(new int[] {10, 20, 30, 40, 50, 60, 70}, 3, new int[] {30, 20, 10, 60, 50, 40, 70});
    }

    @Test(order = 2)
    void lengthIsAMultipleOfK() {
        expect(new int[] {5, 1, 8, 3}, 2, new int[] {1, 5, 3, 8});
    }

    @Test(order = 3)
    void kIsOne() {
        expect(new int[] {4, 9, 2}, 1, new int[] {4, 9, 2});
    }

    @Test(order = 4)
    void kIsTheLength() {
        expect(new int[] {7, -1, 6}, 3, new int[] {6, -1, 7});
    }

    @Test(order = 5)
    void shortLastGroupStays() {
        expect(new int[] {11, 12, 13, 14, 15, 16, 17, 18}, 3, new int[] {13, 12, 11, 16, 15, 14, 17, 18});
        expect(new int[] {6, 0, 2, 9, 4}, 4, new int[] {9, 2, 0, 6, 4});
    }

    @Test(order = 6)
    void singleNode() {
        expect(new int[] {0}, 1, new int[] {0});
    }

    @Test(order = 7)
    void repeatedValuesKeepTheirNodes() {
        expect(new int[] {2, 2, 9, 2, 2}, 2, new int[] {2, 2, 2, 9, 2});
    }

    @Test(order = 8)
    void valuesAtTheLimits() {
        expect(new int[] {1_000_000, -1_000_000, 0}, 2, new int[] {-1_000_000, 1_000_000, 0});
    }

    @Test(order = 9, timeoutMs = 2000)
    void longList() {
        int n = 100_000;
        int[] values = new int[n];
        for (int i = 0; i < n; i++) {
            values[i] = i * 7 % 2_000_001 - 1_000_000; // stays within -10^6 .. 10^6
        }
        for (int k : new int[] {2, 7, 99_998, n}) { // k = 2 means 50,000 groups; 7 and 99,998 leave a short group
            int[] want = new int[n];
            for (int i = 0; i < n; i++) {
                want[i] = values[source(i, k, n)];
            }
            expect(values, k, want);
        }
    }
}
