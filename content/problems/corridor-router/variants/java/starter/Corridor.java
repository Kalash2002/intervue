/**
 * A one-way payment corridor between two nodes of the network (banks, currencies, partners).
 * Money can move from {@code from} to {@code to} for a fixed fee, as long as the corridor is active.
 *
 * @param feeMinor fee in minor units (paise, cents); never negative
 */
public record Corridor(String from, String to, long feeMinor, boolean active) {
    public Corridor {
        if (from == null || to == null || from.isBlank() || to.isBlank()) {
            throw new IllegalArgumentException("corridor needs both ends");
        }
        if (feeMinor < 0) {
            throw new IllegalArgumentException("fee cannot be negative: " + feeMinor);
        }
    }
}
