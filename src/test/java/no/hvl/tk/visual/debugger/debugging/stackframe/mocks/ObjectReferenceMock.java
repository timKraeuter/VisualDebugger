package no.hvl.tk.visual.debugger.debugging.stackframe.mocks;

import com.sun.jdi.*;
import no.hvl.tk.visual.debugger.debugging.stackframe.mocks.value.BooleanValueMock;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ObjectReferenceMock<E extends Value> implements ObjectReference, Iterable<E> {
    private final long id;
    private final ReferenceTypeMock type;
    private final HashMap<Field, Value> fields;
    private Collection<E> itSource = new HashSet<>();

    private Iterator<E> runningIt = null;

    public static ObjectReferenceMock<Value> create(final String typeName) {
        return new ObjectReferenceMock<>(typeName);
    }

    public static <A extends Value> ObjectReferenceMock<A> createCollectionObjectRefMock(
            final String typeName,
            final Collection<A> content) {
        final ObjectReferenceMock<A> aObjectReferenceMock = new ObjectReferenceMock<>(typeName);
        aObjectReferenceMock.setIteratorSource(content);
        return aObjectReferenceMock;
    }

    @NotNull
    @Override
    public Iterator<E> iterator() {
        return this.itSource.iterator();
    }

    private ObjectReferenceMock(final String typeName) {
        this.type = new ReferenceTypeMock(typeName);
        this.id = StringReferenceMock.idCounter.incrementAndGet();
        this.fields = new HashMap<>();
    }

    @Override
    public ReferenceTypeMock referenceType() {
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

    private void setIteratorSource(final Collection<E> collection) {
        this.itSource = collection;
    }

    // Below is irrelevant

    @Override
    public Value invokeMethod(
            final ThreadReference thread,
            final Method method,
            final List<? extends Value> arguments,
            final int options) {
        if (method.name().equals("iterator")) {
            this.runningIt = this.itSource.iterator();
            return this;
        }
        if (method.name().equals("hasNext")) {
            return new BooleanValueMock(this.runningIt.hasNext());
        }
        if (method.name().equals("next")) {
            return this.runningIt.next();
        }
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
