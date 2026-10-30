import static kit.Check.assertEquals;

import kit.Test;

public class Phase1Tests {

    @Test(order = 1)
    void sample() {
        int[] sorted = {10, 20, 20, 30, 50};
        assertEquals(1, SortedSlots.insertAt(sorted, 20));
        assertEquals(3, SortedSlots.insertAt(sorted, 25));
        assertEquals(0, SortedSlots.insertAt(sorted, 5));
        assertEquals(5, SortedSlots.insertAt(sorted, 60));
    }

    @Test(order = 2)
    void emptyArray() {
        assertEquals(0, SortedSlots.insertAt(new int[] {}, 7));
    }

    @Test(order = 3)
    void singleElement() {
        int[] one = {7};
        assertEquals(0, SortedSlots.insertAt(one, 3));
        assertEquals(0, SortedSlots.insertAt(one, 7));
        assertEquals(1, SortedSlots.insertAt(one, 9));
    }

    @Test(order = 4)
    void everyElementEqual() {
        int[] same = {4, 4, 4, 4};
        assertEquals(0, SortedSlots.insertAt(same, 4));
        assertEquals(0, SortedSlots.insertAt(same, 3));
        assertEquals(4, SortedSlots.insertAt(same, 5));
    }

    @Test(order = 5)
    void firstOfSeveralEqualElements() {
        int[] values = {1, 2, 2, 2, 2, 2, 9};
        assertEquals(1, SortedSlots.insertAt(values, 2));
        assertEquals(6, SortedSlots.insertAt(values, 3));
        assertEquals(6, SortedSlots.insertAt(values, 9));
    }

    @Test(order = 6)
    void negativeValues() {
        int[] values = {-9, -5, -5, 0, 3};
        assertEquals(1, SortedSlots.insertAt(values, -5));
        assertEquals(1, SortedSlots.insertAt(values, -6));
        assertEquals(3, SortedSlots.insertAt(values, 0));
        assertEquals(4, SortedSlots.insertAt(values, 1));
        assertEquals(0, SortedSlots.insertAt(values, -10));
    }

    @Test(order = 7)
    void extremeIntValues() {
        int[] values = {Integer.MIN_VALUE, 0, Integer.MAX_VALUE};
        assertEquals(0, SortedSlots.insertAt(values, Integer.MIN_VALUE));
        assertEquals(1, SortedSlots.insertAt(values, Integer.MIN_VALUE + 1));
        assertEquals(2, SortedSlots.insertAt(values, 1));
        assertEquals(2, SortedSlots.insertAt(values, Integer.MAX_VALUE));
    }

    @Test(order = 8)
    void everyTargetAroundEveryElement() {
        int[] values = {2, 4, 6, 8, 10, 12, 14};
        for (int target = 0; target <= 16; target++) {
            // the first element at least target is the one with value 2 * ceil(target / 2), at index ceil(target / 2) - 1
            int expected = Math.max(0, (target + 1) / 2 - 1);
            assertEquals(expected, SortedSlots.insertAt(values, target), "target " + target);
        }
    }

    @Test(order = 9, timeoutMs = 600)
    void sixtyThousandSearchesInAMillionElements() {
        int n = 1_000_000;
        int[] values = new int[n];
        for (int i = 0; i < n; i++) {
            values[i] = 2 * i; // even numbers 0 to 1,999,998
        }
        for (int k = 0; k < 60_000; k++) {
            int target = (int) ((k * 7919L) % 2_000_001); // 0 to 2,000,000, the last value is past every element
            assertEquals((target + 1) / 2, SortedSlots.insertAt(values, target), "target " + target);
        }
    }
}
