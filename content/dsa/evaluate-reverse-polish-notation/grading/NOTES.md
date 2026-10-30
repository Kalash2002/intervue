# Grader notes: Evaluate Reverse Polish Notation

Tests cover: the sample (24), a single value and a single negative value, operand order for `-` and `/` (`3 9 -` is -6; `20 4 /` is 5), division rounding towards zero for every sign combination (a floor division returns -4 for `-7 / 2`), negative numbers read as numbers and not as the minus operator, a deeper expression (`2 3 4 5 * - +` is -15), a mixed chain of `/`, `*` and `-`, results near the top of the `int` range (1,800,000,000), and a long expression of 50,000 operands followed by 49,999 additions (the stack grows deep; the answer is 50,000).

No naive solution: the stack version is the natural one, and the collapse-the-first-operator version in the answer is only shown, not graded. Time is not graded separately for this question (ADR 0008).
