package no.hvl.tk.visual.debugger.debugging.stackframe.mocks;

import com.sun.jdi.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjectReferenceMock implements ObjectReference {
    private final long id;
    private final ReferenceTypeMock type;

    public ObjectReferenceMock(String typeName) {
        this.type = new ReferenceTypeMock(typeName);
        this.id = StringReferenceMock.idCounter.incrementAndGet();
    }

    @Override
    public ReferenceType referenceType() {
        return type;
    }

    @Override
    public Value getValue(Field sig) {
        return null;
    }

    @Override
    public Map<Field, Value> getValues(List<? extends Field> fields) {
        return new HashMap<>();
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
    public long uniqueID() {
        return id;
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
