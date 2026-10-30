/** Raises a floating-point number to an integer power. */
public class Power {

    /** Returns x to the power n; x is never 0 unless n is positive. */
    static double raise(double x, int n) {
        long e = n; // in a long, -n fits even for Integer.MIN_VALUE
        if (e < 0) {
            x = 1 / x;
            e = -e;
        }
        double result = 1;
        for (long i = 0; i < e; i++) {
            result *= x; // one multiplication per unit of the exponent
        }
        return result;
    }
}
