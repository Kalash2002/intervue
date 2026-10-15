import java.util.Arrays;

/** A stack of integers that reports its smallest value in constant time. */
public class MinStack {
    private int[] values = new int[16];
    private int[] minimums = new int[16]; // minimums[i] is the smallest of values[0..i]
    private int size = 0;

    /** Puts a value on top of the stack. */
    void push(int value) {
        if (size == values.length) {
            values = Arrays.copyOf(values, size * 2);
            minimums = Arrays.copyOf(minimums, size * 2);
        }
        values[size] = value;
        minimums[size] = size == 0 ? value : Math.min(value, minimums[size - 1]);
        size++;
    }

    /** Removes the value on top. */
    void pop() {
        size--;
    }

    /** Returns the value on top. */
    int top() {
        return values[size - 1];
    }

    /** Returns the smallest value in the stack. */
    int getMin() {
        return minimums[size - 1];
    }
}
