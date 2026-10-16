import static kit.Check.assertEquals;
import static kit.Check.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Random;
import java.util.Set;
import kit.Test;

public class Phase1Tests {

    /** Merges the lists and checks that every given node appears exactly once, in non-decreasing order. */
    private static void expect(int[][] lists, int[] merged) {
        ListNode[] heads = new ListNode[lists.length];
        Set<ListNode> unused = Collections.newSetFromMap(new IdentityHashMap<>());
        for (int i = 0; i < lists.length; i++) {
            heads[i] = ListNode.of(lists[i]);
            for (ListNode n = heads[i]; n != null; n = n.next) {
                unused.add(n);
            }
        }
        int[] got = new int[merged.length];
        int count = 0;
        for (ListNode n = MergeK.merge(heads); n != null; n = n.next) {
            assertTrue(count < merged.length, "the merged list has too many nodes, or some node links back");
            assertTrue(unused.remove(n), "use each given node exactly once; do not create new ones");
            got[count++] = n.val;
        }
        got = Arrays.copyOf(got, count);
        if (merged.length <= 30 && got.length <= 30) {
            assertEquals(Arrays.toString(merged), Arrays.toString(got));
            return;
        }
        assertEquals(merged.length, got.length, "number of nodes");
        for (int i = 0; i < merged.length; i++) {
            assertEquals(merged[i], got[i], "value at position " + i);
        }
    }

    @Test(order = 1)
    void sample() {
        expect(new int[][] {{4, 7, 12}, {1, 9}, {5, 6, 15}}, new int[] {1, 4, 5, 6, 7, 9, 12, 15});
    }

    @Test(order = 2)
    void noLists() {
        assertTrue(MergeK.merge(new ListNode[0]) == null, "merging no lists gives an empty list (null)");
    }

    @Test(order = 3)
    void everyListEmpty() {
        expect(new int[][] {{}, {}, {}}, new int[] {});
    }

    @Test(order = 4)
    void someListsEmpty() {
        expect(new int[][] {{}, {-2, 3}, {}, {0}}, new int[] {-2, 0, 3});
    }

    @Test(order = 5)
    void oneList() {
        expect(new int[][] {{3, 3, 8}}, new int[] {3, 3, 8});
    }

    @Test(order = 6)
    void equalValuesAcrossLists() {
        expect(new int[][] {{2, 2}, {2}, {1, 2, 5}}, new int[] {1, 2, 2, 2, 2, 5});
    }

    @Test(order = 7)
    void valuesAtTheLimits() {
        expect(new int[][] {{-1_000_000, 0}, {1_000_000}, {-5, 1_000_000}},
                new int[] {-1_000_000, -5, 0, 1_000_000, 1_000_000});
    }

    @Test(order = 8)
    void oneLongListAndShortOnes() {
        expect(new int[][] {{1, 2, 3, 4, 5, 6, 7, 8}, {0}, {9}}, new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9});
    }

    @Test(order = 9, timeoutMs = 2500)
    void manyLists() {
        int k = 50_000;
        int length = 4; // 200,000 nodes in all
        Random random = new Random(43);
        int[][] lists = new int[k][length];
        int[] all = new int[k * length];
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < length; j++) {
                lists[i][j] = random.nextInt(2_000_001) - 1_000_000;
            }
            Arrays.sort(lists[i]);
            System.arraycopy(lists[i], 0, all, i * length, length);
        }
        Arrays.sort(all);
        expect(lists, all);
    }
}
