import static kit.Check.assertEquals;
import static kit.Check.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import kit.Test;

public class Phase1Tests {

    private static void expect(int[] first, int[] second, int[] merged) {
        ListNode a = ListNode.of(first);
        ListNode b = ListNode.of(second);
        Set<ListNode> unused = Collections.newSetFromMap(new IdentityHashMap<>());
        for (ListNode n = a; n != null; n = n.next) {
            unused.add(n);
        }
        for (ListNode n = b; n != null; n = n.next) {
            unused.add(n);
        }
        int[] got = new int[merged.length];
        int count = 0;
        for (ListNode n = MergeLists.merge(a, b); n != null; n = n.next) {
            assertTrue(count < merged.length, "the merged list has too many nodes, or some node links back");
            assertTrue(unused.remove(n), "use each given node exactly once; do not create new ones");
            got[count++] = n.val;
        }
        sameValues(merged, Arrays.copyOf(got, count));
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
        expect(new int[] {2, 5, 9}, new int[] {3, 4, 10, 12}, new int[] {2, 3, 4, 5, 9, 10, 12});
    }

    @Test(order = 2)
    void equalValuesInBothLists() {
        expect(new int[] {-7, 0, 0}, new int[] {0, 6}, new int[] {-7, 0, 0, 0, 6});
        expect(new int[] {5}, new int[] {5}, new int[] {5, 5});
    }

    @Test(order = 3)
    void firstListEmpty() {
        expect(new int[] {}, new int[] {-3, -3}, new int[] {-3, -3});
    }

    @Test(order = 4)
    void secondListEmpty() {
        expect(new int[] {1, 8}, new int[] {}, new int[] {1, 8});
    }

    @Test(order = 5)
    void bothEmpty() {
        assertTrue(MergeLists.merge(null, null) == null, "two empty lists merge into an empty list (null)");
    }

    @Test(order = 6)
    void oneListEntirelyBeforeTheOther() {
        expect(new int[] {1, 2, 3}, new int[] {7, 8}, new int[] {1, 2, 3, 7, 8});
        expect(new int[] {7, 8}, new int[] {1, 2, 3}, new int[] {1, 2, 3, 7, 8});
    }

    @Test(order = 7)
    void negativeValuesAndLimits() {
        expect(new int[] {-1_000_000, -40, 1_000_000}, new int[] {-500, -40, 999_999}, new int[] {-1_000_000, -500, -40, -40, 999_999, 1_000_000});
    }

    @Test(order = 8, timeoutMs = 2000)
    void longInterleavedLists() {
        int n = 100_000; // 200,000 nodes in all: deep enough to overflow a recursive merge
        int[] evens = new int[n];
        int[] odds = new int[n];
        int[] all = new int[2 * n];
        for (int i = 0; i < n; i++) {
            evens[i] = 2 * i - n;
            odds[i] = 2 * i + 1 - n;
        }
        for (int i = 0; i < 2 * n; i++) {
            all[i] = i - n; // evens and odds together are -n .. n - 1
        }
        expect(evens, odds, all);
    }
}
