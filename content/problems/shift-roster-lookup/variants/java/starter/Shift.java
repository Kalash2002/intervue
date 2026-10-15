/** A nurse covering a ward from {@code startMinute} up to, but not including, {@code endMinute}. */
public record Shift(String nurse, String ward, int startMinute, int endMinute) {
    public Shift {
        if (startMinute >= endMinute) {
            throw new IllegalArgumentException("shift of " + nurse + " must end after it starts");
        }
    }

    public boolean covers(int minute) {
        return startMinute <= minute && minute < endMinute;
    }
}
