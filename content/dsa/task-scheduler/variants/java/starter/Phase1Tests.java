import static kit.Check.assertEquals;

import kit.Test;

public class Phase1Tests {

    private static char[] jobs(String kinds) {
        return kinds.toCharArray();
    }

    @Test(order = 1)
    void sample() {
        assertEquals(7, Printer.minSlots(jobs("AAABBC"), 2));
    }

    @Test(order = 2)
    void noJobs() {
        assertEquals(0, Printer.minSlots(jobs(""), 5));
    }

    @Test(order = 3)
    void singleJob() {
        assertEquals(1, Printer.minSlots(jobs("A"), 5));
    }

    @Test(order = 4)
    void zeroGapNeedsNoIdleSlots() {
        assertEquals(5, Printer.minSlots(jobs("CDDCE"), 0));
        assertEquals(6, Printer.minSlots(jobs("MMMMMM"), 0));
    }

    @Test(order = 5)
    void enoughKindsMeansNoIdleSlot() {
        assertEquals(5, Printer.minSlots(jobs("PPQQR"), 1)); // P Q R P Q
        assertEquals(11, Printer.minSlots(jobs("KKKLLLMMMNN"), 1)); // frame gives 7, the job count 11 wins
    }

    @Test(order = 6)
    void oneKindOnly() {
        assertEquals(13, Printer.minSlots(jobs("AAAA"), 3));
    }

    @Test(order = 7)
    void kindsTieForHighestCount() {
        assertEquals(10, Printer.minSlots(jobs("XXXYYY"), 3)); // X Y _ _ X Y _ _ X Y
        assertEquals(18, Printer.minSlots(jobs("QQQQQRRRRRS"), 3)); // four frames of 4 slots plus Q R
    }

    @Test(order = 8)
    void dominantKindAmongManySmallOnes() {
        assertEquals(13, Printer.minSlots(jobs("TTTTTUVWXYZ"), 2)); // T U V T W X T Y Z T _ _ T
    }

    @Test(order = 9)
    void gapLargerThanTheNumberOfJobs() {
        assertEquals(7, Printer.minSlots(jobs("ABCDEFG"), 100));
    }

    @Test(order = 10, timeoutMs = 1000)
    void hugeAnswer() {
        char[] all = new char[100_000];
        java.util.Arrays.fill(all, 'A');
        assertEquals(300_097_000, Printer.minSlots(all, 3000)); // 99,999 frames of 3,001 slots, plus one
    }
}
