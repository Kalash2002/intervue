import static kit.Check.assertEquals;

import java.util.Random;
import kit.Test;

public class Phase1Tests {

    @Test(order = 1)
    void sample() {
        MinStack stack = new MinStack();
        stack.push(4);
        stack.push(7);
        assertEquals(4, stack.getMin());
        assertEquals(7, stack.top());
        stack.pop();
        assertEquals(4, stack.top());
    }

    @Test(order = 2)
    void singleNegativeValue() {
        MinStack stack = new MinStack();
        stack.push(-3);
        assertEquals(-3, stack.top());
        assertEquals(-3, stack.getMin());
    }

    @Test(order = 3)
    void minimumRisesWhenTheSmallestIsPopped() {
        MinStack stack = new MinStack();
        stack.push(5);
        stack.push(2);
        stack.push(8);
        assertEquals(2, stack.getMin());
        stack.pop();
        assertEquals(2, stack.getMin());
        stack.pop();
        assertEquals(5, stack.getMin());
    }

    @Test(order = 4)
    void duplicateMinimumSurvivesOnePop() {
        MinStack stack = new MinStack();
        stack.push(3);
        stack.push(1);
        stack.push(1);
        stack.pop();
        assertEquals(1, stack.getMin());
        stack.pop();
        assertEquals(3, stack.getMin());
    }

    @Test(order = 5)
    void fallingThenPopping() {
        MinStack stack = new MinStack();
        int[] pushes = {9, 7, 5, 3};
        for (int value : pushes) {
            stack.push(value);
            assertEquals(value, stack.getMin());
        }
        for (int i = pushes.length - 2; i >= 0; i--) {
            stack.pop();
            assertEquals(pushes[i], stack.getMin());
        }
    }

    @Test(order = 6)
    void risingKeepsTheFirstValueAsMinimum() {
        MinStack stack = new MinStack();
        for (int value = 1; value <= 5; value++) {
            stack.push(value);
            assertEquals(1, stack.getMin());
            assertEquals(value, stack.top());
        }
    }

    @Test(order = 7)
    void extremeValues() {
        MinStack stack = new MinStack();
        stack.push(Integer.MAX_VALUE);
        stack.push(Integer.MIN_VALUE);
        assertEquals(Integer.MIN_VALUE, stack.getMin());
        assertEquals(Integer.MIN_VALUE, stack.top());
        stack.pop();
        assertEquals(Integer.MAX_VALUE, stack.getMin());
        assertEquals(Integer.MAX_VALUE, stack.top());
    }

    @Test(order = 8)
    void twoStacksAreIndependent() {
        MinStack first = new MinStack();
        MinStack second = new MinStack();
        first.push(1);
        second.push(5);
        assertEquals(5, second.getMin());
        assertEquals(1, first.getMin());
    }

    @Test(order = 9, timeoutMs = 1000)
    void largeRun() {
        int n = 100_000;
        Random random = new Random(7);
        int[] values = new int[n];
        int[] minUpTo = new int[n]; // smallest of values[0..i], tracked here in the test
        MinStack stack = new MinStack();
        for (int i = 0; i < n; i++) {
            values[i] = random.nextInt(2_000_001) - 1_000_000;
            minUpTo[i] = i == 0 ? values[i] : Math.min(values[i], minUpTo[i - 1]);
            stack.push(values[i]);
            assertEquals(minUpTo[i], stack.getMin());
        }
        for (int i = n - 1; i >= 0; i--) {
            assertEquals(values[i], stack.top());
            assertEquals(minUpTo[i], stack.getMin());
            stack.pop();
        }
    }
}
