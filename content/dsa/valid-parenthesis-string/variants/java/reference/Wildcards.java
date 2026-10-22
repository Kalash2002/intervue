/** Decides whether the stars in a bracket string can be read so that the brackets balance. */
public class Wildcards {

    /** Returns true if some reading of the stars as (, ) or nothing balances s. */
    static boolean canBalance(String s) {
        int low = 0; // fewest brackets that can be open here
        int high = 0; // most brackets that can be open here
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            low += c == '(' ? 1 : -1; // the fewest read a star as )
            high += c == ')' ? -1 : 1; // the most read a star as (
            if (high < 0) {
                return false; // more ) than ( and stars together: no reading survives
            }
            low = Math.max(low, 0); // a count below zero closed nothing: drop those readings
        }
        return low == 0;
    }
}
