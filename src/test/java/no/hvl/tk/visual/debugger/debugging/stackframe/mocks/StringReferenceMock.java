package no.hvl.tk.visual.debugger.debugging.stackframe.mocks;

import com.sun.jdi.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class StringReferenceMock implements StringReference {
    public static AtomicLong idCounter = new AtomicLong(0);

    private final long id;
    private final String value;
    private final ReferenceTypeMock type;

    public StringReferenceMock(String value) {
        this.value = value;
        this.type = new ReferenceTypeMock("java.lang.String");
        this.id = idCounter.incrementAndGet();
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public ReferenceType referenceType() {
        return type;
    }

    @Override
    public long uniqueID() {
        return id;
    }

    @Override
    public Value getValue(Field sig) {
        return this;
    }

    @Override
    public Map<Field, Value> getValues(List<? extends Field> fields) {
        return null;
    }

    @Override
    public void setValue(Field field, Value value) {

    }

    @Override
    public Value invokeMethod(ThreadReference thread, Method method, List<? extends Value> arguments, int options) {
        return null;
    }

    @Override
    public void disableCollection() {

    }

    @Override
    public void enableCollection() {

    }

    @Override
    public boolean isCollected() {
        return false;
    }

    @Override
    public List<ThreadReference> waitingThreads() {
        return null;
    }

    @Override
    public ThreadReference owningThread() {
        return null;
    }

    @Override
    public int entryCount() {
        return 0;
    }

    @Override
    public List<ObjectReference> referringObjects(long maxReferrers) {
        return null;
    }

    @Override
    public Type type() {
        return null;
    }

    @Override
    public VirtualMachine virtualMachine() {
        return null;
    }
}
