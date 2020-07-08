package pl.marcinchwedczuk.jgr;

public class Lock {
    private boolean disabled = false;
    private final Semaphore semaphore = new Semaphore(1);

    public Thunk lock(ContUnit cont) {
        if (disabled) return cont::apply;
        return semaphore.down(1, n -> cont.apply());
    }

    public Thunk unlock(ContUnit cont) {
        if (disabled) return cont::apply;
        return semaphore.up(1, n -> cont.apply());
    }

    // Debug only!!!
    public void __disable() {
        disabled = true;
    }
}
