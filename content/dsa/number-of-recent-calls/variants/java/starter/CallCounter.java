/** Counts the calls that arrived within a recent window of milliseconds. */
public class CallCounter {
    private final int window;

    CallCounter(int window) {
        this.window = window;
    }

    /** Records a call at millisecond t and returns the calls in [t - window, t], this one included. */
    int ping(int t) {
        return 0;
    }
}
