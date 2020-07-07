package pl.marcinchwedczuk.jgr;

@FunctionalInterface
public interface Thunk {
    Thunk run();
}
