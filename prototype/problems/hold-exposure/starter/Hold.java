/**
 * An authorisation hold: money reserved on an account for a period of time.
 * The hold is active from {@code startMinute} up to, but not including, {@code endMinute}.
 *
 * @param amountMinor amount in minor units (paise); always positive
 */
public record Hold(String id, String account, long amountMinor, int startMinute, int endMinute) {
    public Hold {
        if (amountMinor <= 0) {
            throw new IllegalArgumentException("hold " + id + ": amount must be positive");
        }
        if (startMinute >= endMinute) {
            throw new IllegalArgumentException("hold " + id + ": must end after it starts");
        }
    }

    public boolean activeAt(int minute) {
        return startMinute <= minute && minute < endMinute;
    }
}
