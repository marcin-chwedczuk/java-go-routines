package pl.marcinchwedczuk.jgr;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Trampoline {
    private static final Thunk STOP_PROGRAM = newCyclicThunk();
    private static final Thunk STOP_THREAD = newCyclicThunk();

    private static final Queue<Thunk> runningQueue = new ArrayDeque<>();

    private static final Queue<Thunk> delayReady =
            new ConcurrentLinkedDeque<>();
    private static final ScheduledExecutorService delayScheduler =
            Executors.newScheduledThreadPool(1);

    public static void run(Thunk startWith) throws InterruptedException {
        runningQueue.add(startWith);

        Thunk tmp;
        while ((tmp = runningQueue.poll()) != STOP_PROGRAM) {
            if (tmp == null) {
                Thread.sleep(0);
            }
            else {
                tmp = tmp.run();

                if (tmp != STOP_THREAD) {
                    runningQueue.add(tmp);
                }
            }

            // Add ready delay tasks to running queue
            while ((tmp = delayReady.poll()) != null) {
                runningQueue.add(tmp);
            }
        }

        delayScheduler.shutdown();
    }

    public static Thunk stopProgram() { return STOP_PROGRAM; }
    public static Thunk stopThread() { return STOP_THREAD; }
    public static Thunk resume(Thunk thread, Cont<Void> cont) {
        runningQueue.add(thread);
        return () -> cont.apply(null);
    }

    public static <M> Thunk fork(Cont<Channel<M>> childThread,
                                 Cont<Channel<M>> cont) {
        var channel = new FullChannel<M>();
        runningQueue.add(() -> childThread.apply(channel.childEndpoint()));
        return () -> cont.apply(channel.parentEndpoint());
    }

    public static Thunk fork(ContUnit childThread,
                             ContUnit cont) {
        runningQueue.add(childThread::apply);
        return cont::apply;
    }

    public static Thunk delay(long delayMillis, ContUnit cont) {
        Thunk afterDelay = () -> cont.apply(null);

        delayScheduler.schedule(
                () -> delayReady.add(afterDelay),
                delayMillis, TimeUnit.MILLISECONDS);

        return STOP_THREAD;
    }

    private static Thunk newCyclicThunk() {
        final var tmp = new Thunk[] { null };
        tmp[0] = () -> tmp[0];
        return tmp[0];
    }
}
