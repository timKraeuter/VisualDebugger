package no.hvl.tk.visual.debugger.debugging.concurrency;

import com.intellij.openapi.diagnostic.Logger;
import no.hvl.tk.visual.debugger.debugging.NodeDebugVisualizer;

public class Lock {
    private static final Logger LOGGER = Logger.getInstance(NodeDebugVisualizer.class);

    private boolean locked;

    /**
     * Creates a new Lock with the lock-flag set to the
     * value of the parameter.
     */
    public Lock(boolean locked) {
        this.locked = locked;
    }

    public synchronized void lock() {
        while (this.locked) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                LOGGER.warn("Lock wait interrupted!", e);
                Thread.currentThread().interrupt();
            }
        }
        this.locked = true;
    }

    public synchronized void unlock() {
        this.locked = false;
        this.notifyAll();
    }
}

