package no.hvl.tk.visual.debugger.debugging.stackframe.mocks;

import com.sun.jdi.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjectReferenceMock implements ObjectReference {
    private final long id;
    private final ReferenceTypeMock type;
    private final HashMap<Field, Value> fields;

    public ObjectReferenceMock(final String typeName) {
        this.type = new ReferenceTypeMock(typeName);
        this.id = StringReferenceMock.idCounter.incrementAndGet();
        this.fields = new HashMap<>();
    }

    @Override
    public ReferenceType referenceType() {
        return this.type;
    }

    @Override
    public Value getValue(final Field field) {
        return this.fields.get(field);
    }

    @Override
    public Map<Field, Value> getValues(final List<? extends Field> fields) {
        return this.fields;
    }

    @Override
    public void setValue(final Field field, final Value value) {
        this.fields.put(field, value);
    }

    @Override
    public Type type() {
        return new TypeMock(this.type.name());
    }

    // Below is irrelevant

    @Override
    public Value invokeMethod(final ThreadReference thread, final Method method, final List<? extends Value> arguments, final int options) {
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
        return this.id;
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
    public List<ObjectReference> referringObjects(final long maxReferrers) {
        return null;
    }

    @Override
    public VirtualMachine virtualMachine() {
        return null;
    }
}
