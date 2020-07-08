package pl.marcinchwedczuk.jgr;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class Semaphore {
    private int value;
    private List<WaitQueueEntry> waitingQueue = new ArrayList<>();

    public Semaphore(int initialValue) {
        if (initialValue < 0) throw new IllegalArgumentException("initialValue");
        this.value = initialValue;
    }

    public Thunk down(int n, Cont<Integer> cont) {
        if (value >= n) {
            value -= n;

            int valueSnapshot = value;
            return () -> cont.apply(valueSnapshot);
        }
        else {
            waitingQueue.add(new WaitQueueEntry(cont, n));
            return Trampoline.stopThread();
        }
    }

    public Thunk up(int n, Cont<Integer> cont) {
        value += n;

        int valueSnapshot = value;
        return wakeUp(0,() -> cont.apply(valueSnapshot));
    }

    private Thunk wakeUp(int waitingIndex, ContUnit cont) {
        if (waitingIndex < waitingQueue.size() && value > 0) {
            WaitQueueEntry entry = waitingQueue.get(waitingIndex);
            if (entry.downValue <= value) {
                value -= entry.downValue;
                waitingQueue.remove(waitingIndex);

                int valueSnapshot = value;
                return Trampoline.resume(
                        () -> entry.thread.apply(valueSnapshot),
                        // We removed waitingIndex element so we try
                        // the same index again.
                        () -> wakeUp(waitingIndex, cont));
            }
            else {
                // Reserve as much as we can
                entry.downValue -= value;
                value = 0;
                return cont::apply;
            }
        }
        else {
            return cont::apply;
        }
    }

    private static class WaitQueueEntry {
        public final Cont<Integer> thread;
        public int downValue;

        public WaitQueueEntry(Cont<Integer> thread, int downValue) {
            this.thread = thread;
            this.downValue = downValue;
        }
    }
}
