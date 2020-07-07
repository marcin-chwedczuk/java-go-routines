package pl.marcinchwedczuk.jgr;

public class FullChannel<M> {
    private final HalfChannel<M> child2Parent = new HalfChannel<>();
    private final HalfChannel<M> parent2Child = new HalfChannel<>();

    private final Channel<M> childEndpoint = new Channel<>() {
        @Override
        public Thunk send(M msg, ContUnit cont) {
            return child2Parent.send(msg, cont);
        }

        @Override
        public Thunk receive(Cont<M> cont) {
            return parent2Child.receive(cont);
        }
    };

    private final Channel<M> parentEndpoint = new Channel<>() {
        @Override
        public Thunk send(M msg, ContUnit cont) {
            return parent2Child.send(msg, cont);
        }

        @Override
        public Thunk receive(Cont<M> cont) {
            return child2Parent.receive(cont);
        }
    };

    public Channel<M> childEndpoint() {
        return childEndpoint;
    }

    public Channel<M> parentEndpoint() {
        return parentEndpoint;
    }
}
