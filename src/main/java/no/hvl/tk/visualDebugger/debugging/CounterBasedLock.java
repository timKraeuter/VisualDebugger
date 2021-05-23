package no.hvl.tk.visualDebugger.debugging;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Lock which will unlock when the counter hits 0.
 */
public class CounterBasedLock {
    private AtomicInteger counter;
    private Lock lock;

    public CounterBasedLock() {
        lock = new Lock(true);
        counter = new AtomicInteger(1);
    }

    public void lock() {
//        System.out.println("Attempting to lock " + this);
        this.lock.lock();
    }

    public synchronized void increaseCounter() {
        final int i = counter.incrementAndGet();
//        System.out.println("Counter increased to " + i);
    }

    public synchronized void decreaseCounter() {
        final int i = counter.decrementAndGet();
//        System.out.println("Counter decreased to " + i);
        assert i >= 0 : "Lock counter decreases below 0!";
        if (i == 0) {
//            System.out.println("Lock unlocked " + this);
            lock.unlock();
        }
    }
}
