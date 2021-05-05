package no.hvl.tk.visualDebugger.debugging;

import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 */
public class CounterBasedLock {
    private AtomicInteger counter;
    private Lock lock;

    public CounterBasedLock() {
        lock = new Lock(true);
        counter = new AtomicInteger(1);
    }

    public void lock() {
        this.lock.lock();
    }

    public synchronized void increaseCounter() {
        final int i = counter.incrementAndGet();
    }

    public synchronized void decreaseCounter() {
        final int i = counter.decrementAndGet();
        assert i >= 0 : "Lock counter decreases below 0!";
        if (i == 0) {
            lock.unlock();
        }
    }
}
