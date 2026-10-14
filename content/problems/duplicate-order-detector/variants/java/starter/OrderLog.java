import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Every order in the order it arrived. Minutes never go backwards. */
public class OrderLog {
    private final List<Order> orders = new ArrayList<>();

    public void add(Order order) {
        if (!orders.isEmpty() && order.minute() < orders.get(orders.size() - 1).minute()) {
            throw new IllegalArgumentException("order " + order.id() + " arrived out of order");
        }
        orders.add(order);
    }

    /** All orders, oldest first. */
    public List<Order> all() {
        return Collections.unmodifiableList(orders);
    }

    public int size() {
        return orders.size();
    }
}
