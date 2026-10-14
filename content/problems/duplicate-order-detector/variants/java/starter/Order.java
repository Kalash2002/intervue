/**
 * One checkout order.
 *
 * @param amountMinor amount in minor units (paise); always positive
 * @param minute minutes since the start of the day
 */
public record Order(String id, String customer, String sku, long amountMinor, int minute) {
    public Order {
        if (amountMinor <= 0) {
            throw new IllegalArgumentException("order " + id + ": amount must be positive");
        }
        if (minute < 0) {
            throw new IllegalArgumentException("order " + id + ": minute cannot be negative");
        }
    }

    /** The part of the order that decides whether two orders are the same basket. */
    public OrderKey key() {
        return new OrderKey(customer, sku, amountMinor);
    }
}
