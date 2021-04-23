package domain;

import java.util.HashSet;
import java.util.Set;

public class ObjectDiagram {
    private Set<ODObject> objects;

    public ObjectDiagram() {
        this.objects = new HashSet<>();
    }

    public Set<ODObject> getObjects() {
        return objects;
    }

    public void addObject(ODObject obj) {
        this.objects.add(obj);
    }
}
