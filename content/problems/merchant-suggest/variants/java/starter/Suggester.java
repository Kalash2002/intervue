import java.util.List;

/** Suggests merchants while the customer types in the "Pay to" box. */
public class Suggester {
    private final MerchantDirectory directory;

    public Suggester(MerchantDirectory directory) {
        this.directory = directory;
    }

    /** Up to {@code limit} merchants matching what the customer has typed. See the brief for the rules. */
    public List<Merchant> suggest(String typed, int limit) {
        throw new UnsupportedOperationException("suggest is not implemented yet");
    }
}
