package pl.marcinchwedczuk.jgr;

@FunctionalInterface
interface Cont<T> {
    Thunk apply(T input);
}
