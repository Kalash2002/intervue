import java.util.ArrayList;
import java.util.List;

/** A stack of integers that reports its smallest value in constant time. */
public class MinStack {
    private final List<Integer> values = new ArrayList<>();

    /** Puts a value on top of the stack. */
    void push(int value) {
        values.add(value);
    }

    /** Removes the value on top. */
    void pop() {
        values.remove(values.size() - 1);
    }

    /** Returns the value on top. */
    int top() {
        return values.get(values.size() - 1);
    }

    /** Returns the smallest value in the stack. */
    int getMin() {
        int min = values.get(0);
        for (int value : values) {
            if (value < min) {
                min = value;
            }
        }
        return min;
    }
}
