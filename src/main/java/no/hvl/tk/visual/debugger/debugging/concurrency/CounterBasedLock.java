package no.hvl.tk.visual.debugger.debugging.concurrency;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Lock which will unlock when the counter hits 0.
 */
public class CounterBasedLock {
    private final AtomicInteger counter;
    private final Lock lock;

    public CounterBasedLock() {
        lock = new Lock(true);
        counter = new AtomicInteger(1);
    }

    public void lock() {
        this.lock.lock();
    }

    public synchronized void increaseCounter() {
        counter.incrementAndGet();
    }

    public synchronized void decreaseCounter() {
        final int i = counter.decrementAndGet();
        assert i >= 0 : "Lock counter decreases below 0!";
        if (i == 0) {
            lock.unlock();
        }
    }
}
