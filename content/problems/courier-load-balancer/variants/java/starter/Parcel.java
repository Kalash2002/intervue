/** One parcel; {@code weightGrams} is always positive. */
public record Parcel(String id, int weightGrams) {
    public Parcel {
        if (weightGrams <= 0) {
            throw new IllegalArgumentException("parcel " + id + ": weight must be positive");
        }
    }
}
