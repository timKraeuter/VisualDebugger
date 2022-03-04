package no.hvl.tk.visual.debugger.debugging.stackframe.mocks;

import com.sun.jdi.*;

import java.util.List;
import java.util.Map;

public class EntryObjectReferenceMock implements ObjectReference {

    private final long id;
    private final Value key;
    private final Value value;

    public EntryObjectReferenceMock(final Value key, final Value value) {
        this.id = StringReferenceMock.idCounter.incrementAndGet();
        this.key = key;
        this.value = value;
    }

    @Override
    public long uniqueID() {
        return this.id;
    }

    @Override
    public Value invokeMethod(
            final ThreadReference threadReference,
            final Method method,
            final List<? extends Value> list,
            final int i) {
        final String name = method.name();
        if (name.equals("getKey")) {
            return this.key;
        }
        if (name.equals("getValue")) {
            return this.value;
        }
        return null;
    }

    // Below is irrelevant

    @Override
    public ReferenceType referenceType() {
        return new ReferenceTypeMock("Map.Entry");
    }

    @Override
    public Value getValue(final Field field) {
        return null;
    }

    @Override
    public Map<Field, Value> getValues(final List<? extends Field> list) {
        return null;
    }

    @Override
    public void setValue(final Field field, final Value value) {

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
    public List<ObjectReference> referringObjects(final long l) {
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
