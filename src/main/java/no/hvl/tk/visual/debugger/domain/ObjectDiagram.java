package no.hvl.tk.visual.debugger.domain;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ObjectDiagram {
    private final Set<ODObject> objects;
    private final Set<ODPrimitiveRootValue> primitiveRootValues;

    public ObjectDiagram() {
        this.objects = new HashSet<>();
        this.primitiveRootValues = new HashSet<>();
    }

    public Set<ODObject> getObjects() {
        return Collections.unmodifiableSet(objects);
    }

    public Set<ODPrimitiveRootValue> getPrimitiveRootValues() {
        return Collections.unmodifiableSet(primitiveRootValues);
    }

    public void addObject(ODObject obj) {
        this.objects.add(obj);
    }

    public void addPrimitiveRootValue(ODPrimitiveRootValue primitiveRootValue) {
        this.primitiveRootValues.add(primitiveRootValue);
    }
}
