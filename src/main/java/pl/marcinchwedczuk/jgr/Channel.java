package pl.marcinchwedczuk.jgr;

public interface Channel<M> {
    Thunk send(M msg, ContUnit cont);
    Thunk receive(Cont<M> cont);
}
