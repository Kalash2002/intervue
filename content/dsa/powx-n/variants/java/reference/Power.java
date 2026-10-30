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
        while (e > 0) {
            if ((e & 1) == 1) { // this bit of the exponent asks for the current square
                result *= x;
            }
            x *= x; // x to the power 1, 2, 4, 8, ... of the original
            e >>= 1;
        }
        return result;
    }
}
