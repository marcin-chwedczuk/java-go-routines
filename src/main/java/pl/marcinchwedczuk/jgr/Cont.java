package pl.marcinchwedczuk.jgr;

@FunctionalInterface
public interface Cont<T> {
    Thunk apply(T input);
}
