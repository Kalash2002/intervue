import static kit.Check.assertEquals;

import java.util.Random;
import kit.Test;

public class Phase1Tests {

    @Test(order = 1)
    void sample() {
        MedianFinder finder = new MedianFinder();
        finder.addNum(6);
        assertEquals(6.0, finder.findMedian());
        finder.addNum(-2);
        assertEquals(2.0, finder.findMedian());
        finder.addNum(11);
        assertEquals(6.0, finder.findMedian());
        finder.addNum(3);
        assertEquals(4.5, finder.findMedian());
    }

    @Test(order = 2)
    void halfValuesAreNotRounded() {
        MedianFinder finder = new MedianFinder();
        finder.addNum(-5);
        finder.addNum(-8);
        assertEquals(-6.5, finder.findMedian()); // integer division would give -6
        finder.addNum(-8);
        assertEquals(-8.0, finder.findMedian());
        MedianFinder other = new MedianFinder();
        other.addNum(1);
        other.addNum(2);
        assertEquals(1.5, other.findMedian());
    }

    @Test(order = 3)
    void sumPastTheIntRange() {
        MedianFinder finder = new MedianFinder();
        finder.addNum(Integer.MAX_VALUE);
        finder.addNum(Integer.MAX_VALUE - 1);
        assertEquals(2147483646.5, finder.findMedian());
        MedianFinder low = new MedianFinder();
        low.addNum(Integer.MIN_VALUE);
        low.addNum(Integer.MIN_VALUE);
        assertEquals(-2147483648.0, low.findMedian());
    }

    @Test(order = 4)
    void singleNumber() {
        MedianFinder finder = new MedianFinder();
        finder.addNum(-31);
        assertEquals(-31.0, finder.findMedian());
    }

    @Test(order = 5)
    void repeatedValues() {
        MedianFinder finder = new MedianFinder();
        int[] adds = {4, 4, 9, 1, 4};
        double[] medians = {4.0, 4.0, 4.0, 4.0, 4.0};
        for (int i = 0; i < adds.length; i++) {
            finder.addNum(adds[i]);
            assertEquals(medians[i], finder.findMedian());
        }
    }

    @Test(order = 6)
    void risingThenFalling() {
        MedianFinder finder = new MedianFinder();
        int[] adds = {10, 20, 30, 40, 5, 1, 0};
        // sorted prefixes: 10 | 10 20 | 10 20 30 | 10..40 | 5 10 20 30 40 | 1 5 10 20 30 40 | 0 1 5 10 20 30 40
        double[] medians = {10.0, 15.0, 20.0, 25.0, 20.0, 15.0, 10.0};
        for (int i = 0; i < adds.length; i++) {
            finder.addNum(adds[i]);
            assertEquals(medians[i], finder.findMedian());
        }
    }

    @Test(order = 7)
    void twoFindersAreIndependent() {
        MedianFinder first = new MedianFinder();
        MedianFinder second = new MedianFinder();
        first.addNum(100);
        second.addNum(-100);
        second.addNum(-50);
        assertEquals(100.0, first.findMedian());
        assertEquals(-75.0, second.findMedian());
    }

    @Test(order = 8, timeoutMs = 3000)
    void hundredThousandAddsWithAMedianAfterEach() {
        int range = 1000; // values -500..499, counted here to know the median
        int[] count = new int[range];
        Random random = new Random(19);
        MedianFinder finder = new MedianFinder();
        for (int n = 1; n <= 100_000; n++) {
            int value = random.nextInt(range) - 500;
            count[value + 500]++;
            finder.addNum(value);
            double got = finder.findMedian();
            int lowRank = (n - 1) / 2; // the two middle positions, counting from 0
            int highRank = n / 2;
            int lowValue = 0;
            int highValue = 0;
            int seen = 0;
            for (int b = 0; b < range; b++) {
                if (seen <= lowRank && lowRank < seen + count[b]) {
                    lowValue = b - 500;
                }
                if (seen <= highRank && highRank < seen + count[b]) {
                    highValue = b - 500;
                    break;
                }
                seen += count[b];
            }
            double expected = (lowValue + highValue) / 2.0;
            if (got != expected) {
                assertEquals(expected, got, "median after " + n + " numbers");
            }
        }
    }
}
