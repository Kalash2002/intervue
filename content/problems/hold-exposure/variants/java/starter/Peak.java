/**
 * The highest total amount held on an account.
 *
 * @param amountMinor the highest total, in minor units
 * @param atMinute the first minute at which that total is reached; -1 when the account has no holds
 */
public record Peak(long amountMinor, int atMinute) {
    public static final Peak NONE = new Peak(0, -1);
}
