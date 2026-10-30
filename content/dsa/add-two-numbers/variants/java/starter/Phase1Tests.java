import static kit.Check.assertEquals;
import static kit.Check.assertTrue;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import kit.Test;

public class Phase1Tests {

    /** Adds the two digit lists and checks the sum, and that every input node keeps its digit and link. */
    private static void expect(int[] first, int[] second, int[] sum) {
        ListNode a = ListNode.of(first);
        ListNode b = ListNode.of(second);
        List<ListNode> nodes = new ArrayList<>();
        for (ListNode n = a; n != null; n = n.next) {
            nodes.add(n);
        }
        List<ListNode> nodesOfB = new ArrayList<>();
        for (ListNode n = b; n != null; n = n.next) {
            nodesOfB.add(n);
        }
        int[] got = digits(AddNumbers.add(a, b), sum.length);
        unchanged(nodes, first, "first");
        unchanged(nodesOfB, second, "second");
        if (sum.length <= 40 && got.length <= 40) {
            assertEquals(Arrays.toString(sum), Arrays.toString(got));
            return;
        }
        assertEquals(sum.length, got.length, "number of digits");
        for (int i = 0; i < sum.length; i++) {
            assertEquals(sum[i], got[i], "digit at position " + i);
        }
    }

    /** The digits from head to the end; fails when there are more than limit (a loop, or too many digits). */
    private static int[] digits(ListNode head, int limit) {
        List<Integer> out = new ArrayList<>();
        for (ListNode n = head; n != null; n = n.next) {
            assertTrue(out.size() < limit, "the result has more digits than the sum, or some node links back");
            out.add(n.val);
        }
        return out.stream().mapToInt(Integer::intValue).toArray();
    }

    private static void unchanged(List<ListNode> nodes, int[] values, String name) {
        for (int i = 0; i < nodes.size(); i++) {
            ListNode next = i + 1 < nodes.size() ? nodes.get(i + 1) : null;
            assertTrue(nodes.get(i).val == values[i] && nodes.get(i).next == next,
                    "node " + i + " of " + name + " was changed; leave the input lists as they were");
        }
    }

    @Test(order = 1)
    void sample() {
        expect(new int[] {7, 3, 2}, new int[] {5, 9}, new int[] {2, 3, 3});
    }

    @Test(order = 2)
    void carryMakesAnExtraDigit() {
        expect(new int[] {6, 1}, new int[] {4, 8}, new int[] {0, 0, 1});
        expect(new int[] {9}, new int[] {9}, new int[] {8, 1});
    }

    @Test(order = 3)
    void zeroPlusANumber() {
        expect(new int[] {0}, new int[] {8, 4, 6}, new int[] {8, 4, 6});
        expect(new int[] {8, 4, 6}, new int[] {0}, new int[] {8, 4, 6});
    }

    @Test(order = 4)
    void singleDigitsWithoutCarry() {
        expect(new int[] {3}, new int[] {4}, new int[] {7});
    }

    @Test(order = 5)
    void carryRunsThroughTheLongerList() {
        expect(new int[] {1}, new int[] {9, 9, 9, 9, 2}, new int[] {0, 0, 0, 0, 3});
    }

    @Test(order = 6)
    void longerFirstList() {
        expect(new int[] {5, 0, 0, 7}, new int[] {5}, new int[] {0, 1, 0, 7});
    }

    @Test(order = 7)
    void numbersTooBigForALong() {
        int[] nines = new int[30];
        Arrays.fill(nines, 9);
        int[] power = new int[31];
        power[30] = 1;
        expect(nines, new int[] {1}, power); // (10^30 - 1) + 1 = 10^30
        expect(new int[] {2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4},
                new int[] {3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5},
                new int[] {5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9});
    }

    @Test(order = 8, timeoutMs = 1000)
    void longNumbers() {
        Random random = new Random(39);
        int[] first = randomDigits(random, 10_000);
        int[] second = randomDigits(random, 9_000);
        BigInteger want = number(first).add(number(second));
        String text = new StringBuilder(want.toString()).reverse().toString();
        int[] sum = new int[text.length()];
        for (int i = 0; i < sum.length; i++) {
            sum[i] = text.charAt(i) - '0';
        }
        expect(first, second, sum);
    }

    /** Random digits, least significant first, with a last digit that is not 0. */
    private static int[] randomDigits(Random random, int length) {
        int[] digits = new int[length];
        for (int i = 0; i < length; i++) {
            digits[i] = random.nextInt(10);
        }
        digits[length - 1] = 1 + random.nextInt(9);
        return digits;
    }

    private static BigInteger number(int[] digits) {
        StringBuilder text = new StringBuilder();
        for (int i = digits.length - 1; i >= 0; i--) {
            text.append(digits[i]);
        }
        return new BigInteger(text.toString());
    }
}
