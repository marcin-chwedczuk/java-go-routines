package pl.marcinchwedczuk.jgr;

@FunctionalInterface
interface Thunk {
    Thunk run();
}
