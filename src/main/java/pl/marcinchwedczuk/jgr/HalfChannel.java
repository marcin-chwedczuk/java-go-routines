package pl.marcinchwedczuk.jgr;

import java.util.ArrayDeque;
import java.util.Queue;

// You can only read or write to this channel but not both
// on a single thread.
// Mixing reads/writes may result in receiving
// your own messages, that where sent earlier.
public class HalfChannel<M> {
    private final Queue<M> messages = new ArrayDeque<>();
    private final Queue<Cont<M>> waitingQueue = new ArrayDeque<>();

    Thunk send(M msg, ContUnit cont) {
        if (waitingQueue.isEmpty()) {
            messages.add(msg);
            return () -> cont.apply();
        }
        else {
            Cont<M> thread = waitingQueue.poll();
            return Trampoline.resume(() -> thread.apply(msg), cont);
        }
    }

    Thunk receive(Cont<M> cont) {
        if (messages.isEmpty()) {
            waitingQueue.add(cont);
            return Trampoline.stopThread();
        }
        else {
            M msg = messages.poll();
            return () -> cont.apply(msg);
        }
    }
}
