package pl.marcinchwedczuk.jgr;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Primitives {
    public static Thunk add(int a, int b, Cont<Integer> cont) {
        int sum = a + b;
        return () -> cont.apply(sum);
    }

    public static Thunk add(int a, int b, int c, Cont<Integer> cont) {
        return add(a, b, sum ->
                add(sum, c, cont));
    }

    public static Thunk subtract(int a, int b, Cont<Integer> cont) {
        int difference = a - b;
        return () -> cont.apply(difference);
    }

    public static Thunk multiply(int a, int b, Cont<Integer> cont) {
        int product = a * b;
        return () -> cont.apply(product);
    }

    public static Thunk eq(int a, int b, Cont<Boolean> cont) {
        boolean result = (a == b);
        return () -> cont.apply(result);
    }

    public static Thunk lt(int a, int b, Cont<Boolean> cont) {
        boolean result  = (a < b);
        return () -> cont.apply(result);
    }

    public static Thunk gt(int a, int b, Cont<Boolean> cont) {
        boolean result  = (a > b);
        return () -> cont.apply(result);
    }

    public static Thunk iff(boolean expr,
                            Cont<Boolean> trueBranch,
                            Cont<Boolean> falseBranch) {
        return (expr)
                ? () -> trueBranch.apply(true)
                : () -> falseBranch.apply(false);
    }

    public static <F> Thunk readField(Supplier<F> fieldReader,
                                      Cont<F> cont) {
        var fieldValue = fieldReader.get();
        return () -> cont.apply(fieldValue);
    }

    public static <F> Thunk writeField(F fieldValue,
                                       Consumer<F> fieldWriter,
                                       ContUnit cont) {
        fieldWriter.accept(fieldValue);
        return cont::apply;
    }

    public static Thunk fmt(String s, Object arg1, Cont<String> cont) {
        String sf = String.format(s, arg1);
        return () -> cont.apply(sf);
    }

    public static Thunk fmt(String s, Object arg1, Object arg2, Cont<String> cont) {
        String sf = String.format(s, arg1, arg2);
        return () -> cont.apply(sf);
    }

    public static Thunk fmt(String s, Object arg1, Object arg2, Object arg3,
                            Cont<String> cont) {
        String sf = String.format(s, arg1, arg2, arg3);
        return () -> cont.apply(sf);
    }

    public static Thunk println(String s, ContUnit cont) {
        System.out.println(s);
        return cont::apply;
    }

    public static Thunk rand(int minInclusive, int maxExclusive, Cont<Integer> cont) {
        int r = ThreadLocalRandom.current().nextInt(minInclusive, maxExclusive);
        return () -> cont.apply(r);
    }
}
