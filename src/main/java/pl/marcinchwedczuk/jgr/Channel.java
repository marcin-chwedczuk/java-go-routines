package pl.marcinchwedczuk.jgr;

import java.util.ArrayDeque;
import java.util.Queue;

public class Channel<V> {
    private final Queue<V> messages = new ArrayDeque<>();
    private final Queue<Cont<V>> waitingQueue = new ArrayDeque<>();

    Thunk send(V value, Cont<Void> cont) {
        if (!waitingQueue.isEmpty()) {
            Cont<V> thread = waitingQueue.poll();
            return Trampoline.resume(() -> thread.apply(value), cont);
        }
        else {
            messages.add(value);
            return () -> cont.apply(null);
        }
    }

    Thunk receive(Cont<V> cont) {
        if (messages.size() > 0) {
            V el = messages.poll();
            return () -> cont.apply(el);
        }
        else {
            waitingQueue.add(cont);
            return Trampoline.stopThread();
        }
    }
}
