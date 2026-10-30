import java.util.*;

/** Counts the pings received within the most recent window of seconds. */
public class Pings {

    /** Creates an empty counter; a ping at time t is recent at now when now - window < t <= now. */
    Pings(int window) {
    }

    /** Records one ping; calls come in non-decreasing time order. */
    void ping(int second) {
    }

    /** Returns how many recorded pings are recent at the given second. */
    int recent(int second) {
        return 0;
    }
}
