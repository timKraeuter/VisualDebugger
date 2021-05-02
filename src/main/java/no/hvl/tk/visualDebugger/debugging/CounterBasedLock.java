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
        System.out.println("Counter: " + i);
    }

    public synchronized void decreaseCounter() {
        final int i = counter.decrementAndGet();
        System.out.println("Counter: " + i);
        if (i == 0) {
            System.out.println("Unlocked!");
            lock.unlock();
        }
    }
}
