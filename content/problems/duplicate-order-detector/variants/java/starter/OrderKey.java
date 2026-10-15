import java.util.Objects;

/** Customer, SKU and amount: two orders with equal keys are the same basket. */
public final class OrderKey {
    private final String customer;
    private final String sku;
    private final long amountMinor;

    public OrderKey(String customer, String sku, long amountMinor) {
        this.customer = customer;
        this.sku = sku;
        this.amountMinor = amountMinor;
    }

    public String sku() {
        return sku;
    }

    public boolean equals(OrderKey other) {
        return other != null
                && customer.equals(other.customer)
                && sku.equals(other.sku)
                && amountMinor == other.amountMinor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(customer, sku, amountMinor);
    }

    @Override
    public String toString() {
        return customer + "/" + sku + "/" + amountMinor;
    }
}
