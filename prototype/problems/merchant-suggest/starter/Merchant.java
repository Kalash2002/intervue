/**
 * A merchant customers can pay.
 *
 * @param name display name as the merchant registered it, for example "Cafe Coffee Day"
 * @param txnCount number of payments made to this merchant in the last 30 days
 */
public record Merchant(String name, String category, long txnCount) {}
