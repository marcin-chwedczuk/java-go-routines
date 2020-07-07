package pl.marcinchwedczuk.jgr;

@FunctionalInterface
public interface ContUnit extends Cont<Void> {
    Thunk apply();

    default Thunk apply(Void ignored) {
        return apply();
    }
}
