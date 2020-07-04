package pl.marcinchwedczuk.jgr;

public class Primitives {
    private static Thunk add(int a, int b, Cont<Integer> cont) {
        int sum = a + b;
        return () -> cont.apply(sum);
    }

    private static Thunk add(int a, int b, int c, Cont<Integer> cont) {
        return add(a, b, sum ->
                add(sum, c, cont));
    }

    private static Thunk multiply(int a, int b, Cont<Integer> cont) {
        int product = a * b;
        return () -> cont.apply(product);
    }

    private static Thunk eq(int a, int b, Cont<Boolean> cont) {
        boolean result = (a == b);
        return () -> cont.apply(result);
    }

    private static Thunk lt(int a, int b, Cont<Boolean> cont) {
        boolean result  = (a < b);
        return () -> cont.apply(result);
    }

    private static Thunk iff(boolean expr,
                             Cont<Boolean> trueBranch,
                             Cont<Boolean> falseBranch) {
        return (expr)
                ? () -> trueBranch.apply(true)
                : () -> falseBranch.apply(false);
    }
}
