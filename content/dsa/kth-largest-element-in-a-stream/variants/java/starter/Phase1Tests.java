import static kit.Check.assertEquals;

import java.util.Random;
import kit.Test;

public class Phase1Tests {

    @Test(order = 1)
    void sample() {
        KthLargest board = new KthLargest(2, new int[] {5, -1, 8});
        assertEquals(5, board.add(3));
        assertEquals(8, board.add(9));
        assertEquals(9, board.add(10));
        assertEquals(9, board.add(-4));
    }

    @Test(order = 2)
    void repeatedValuesCountSeparately() {
        KthLargest board = new KthLargest(3, new int[] {7, 7});
        assertEquals(7, board.add(7)); // 7 7 7
        assertEquals(7, board.add(12)); // 12 7 7
        assertEquals(7, board.add(12)); // 12 12 7
        assertEquals(12, board.add(15)); // 15 12 12
    }

    @Test(order = 3)
    void startsEmptyWithKOne() {
        KthLargest board = new KthLargest(1, new int[] {});
        assertEquals(-6, board.add(-6));
        assertEquals(-6, board.add(-20));
        assertEquals(0, board.add(0));
    }

    @Test(order = 4)
    void kEqualsTheNumberOfValues() {
        KthLargest board = new KthLargest(4, new int[] {3, 1, 2});
        assertEquals(0, board.add(0)); // 3 2 1 0
        assertEquals(1, board.add(5)); // 5 3 2 1
        assertEquals(1, board.add(-1));
    }

    @Test(order = 5)
    void onlyValuesAboveTheKthMoveIt() {
        KthLargest board = new KthLargest(3, new int[] {50, 40, 30, 20, 10});
        assertEquals(30, board.add(5));
        assertEquals(35, board.add(35)); // 50 40 35
        assertEquals(40, board.add(45)); // 50 45 40
        assertEquals(45, board.add(60)); // 60 50 45
    }

    @Test(order = 6)
    void valuesAtTheLimits() {
        KthLargest board = new KthLargest(2, new int[] {1_000_000_000, -1_000_000_000});
        assertEquals(-1_000_000_000, board.add(-1_000_000_000));
        assertEquals(1_000_000_000, board.add(1_000_000_000));
    }

    @Test(order = 7)
    void twoBoardsAreIndependent() {
        KthLargest first = new KthLargest(1, new int[] {4});
        KthLargest second = new KthLargest(2, new int[] {90, 80});
        assertEquals(4, first.add(2));
        assertEquals(85, second.add(85));
        assertEquals(6, first.add(6));
    }

    @Test(order = 8, timeoutMs = 1000)
    void hundredThousandAdds() {
        int n = 100_000;
        int[] nums = new int[n];
        for (int i = 0; i < n; i++) {
            nums[i] = i;
        }
        Random random = new Random(11);
        for (int i = n - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int swap = nums[i];
            nums[i] = nums[j];
            nums[j] = swap;
        }
        // the 50,000th largest of 0..99,999 is 50,000; each new value above it moves it up by one
        KthLargest board = new KthLargest(50_000, nums);
        for (int i = 0; i < n; i++) {
            int value = i % 2 == 0 ? 100_000 + i / 2 : -1 - i; // a new high, then a value far below
            assertEquals(50_001 + i / 2, board.add(value));
        }
    }
}
