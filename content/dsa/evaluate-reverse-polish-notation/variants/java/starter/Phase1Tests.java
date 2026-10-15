import static kit.Check.assertEquals;

import kit.Test;

public class Phase1Tests {

    @Test(order = 1)
    void sample() {
        assertEquals(24, Postfix.evaluate(new String[] {"9", "3", "-", "4", "*"}));
    }

    @Test(order = 2)
    void singleValue() {
        assertEquals(42, Postfix.evaluate(new String[] {"42"}));
        assertEquals(-8, Postfix.evaluate(new String[] {"-8"}));
    }

    @Test(order = 3)
    void operandOrderForSubtractionAndDivision() {
        assertEquals(-6, Postfix.evaluate(new String[] {"3", "9", "-"}));
        assertEquals(5, Postfix.evaluate(new String[] {"20", "4", "/"}));
    }

    @Test(order = 4)
    void divisionRoundsTowardsZero() {
        assertEquals(-3, Postfix.evaluate(new String[] {"-7", "2", "/"}));
        assertEquals(-3, Postfix.evaluate(new String[] {"7", "-2", "/"}));
        assertEquals(3, Postfix.evaluate(new String[] {"-7", "-2", "/"}));
        assertEquals(3, Postfix.evaluate(new String[] {"7", "2", "/"}));
    }

    @Test(order = 5)
    void negativeNumbersAreNotTheMinusOperator() {
        assertEquals(-2, Postfix.evaluate(new String[] {"-5", "-3", "-"}));
        assertEquals(-12, Postfix.evaluate(new String[] {"-2", "6", "*"}));
    }

    @Test(order = 6)
    void deeperExpression() {
        assertEquals(-15, Postfix.evaluate(new String[] {"2", "3", "4", "5", "*", "-", "+"}));
    }

    @Test(order = 7)
    void mixedChain() {
        assertEquals(53, Postfix.evaluate(new String[] {"100", "5", "/", "3", "*", "7", "-"}));
    }

    @Test(order = 8)
    void largeIntermediateResult() {
        assertEquals(1_800_000_000, Postfix.evaluate(new String[] {"40000", "50000", "+", "20000", "*"}));
    }

    @Test(order = 9)
    void longExpressionWithDeepStack() {
        int operands = 50_000;
        String[] tokens = new String[2 * operands - 1];
        for (int i = 0; i < operands; i++) {
            tokens[i] = "1";
        }
        for (int i = operands; i < tokens.length; i++) {
            tokens[i] = "+";
        }
        assertEquals(operands, Postfix.evaluate(tokens));
    }
}
