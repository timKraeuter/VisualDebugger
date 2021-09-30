package no.hvl.tk.visual.debugger.debugging.stackframe;

import com.sun.jdi.*;

import java.util.*;

public class StackFrameSessionListenerHelper {
    private static final String[] INTERNAL_PACKAGES = {
            "java.",
            "javax.",
            "sun.",
            "jdk.",
            "com.sun.",
            "com.intellij.",
            "org.junit.",
            "jh61b.junit.",
            "jh61b.",
    };

    private StackFrameSessionListenerHelper() {
    }

    static Iterator<Value> getIterator(ThreadReference thread, ObjectReference obj) {
        ObjectReference i = (ObjectReference) invokeSimple(thread, obj, "iterator");
        return new Iterator<>() {
            @Override
            public boolean hasNext() {
                return ((BooleanValue) invokeSimple(thread, i, "hasNext")).value();
            }

            @Override
            public com.sun.jdi.Value next() {
                return invokeSimple(thread, i, "next");
            }
        };
    }

    static com.sun.jdi.Value invokeSimple(ThreadReference thread, ObjectReference r, String name) {
        try {
            return r.invokeMethod(thread, r.referenceType().methodsByName(name).get(0), Collections.emptyList(), 0);
        } catch (Exception e) {
            return null;
        }
    }

    static boolean implementsInterface(ObjectReference obj, String iface) {
        if (obj.referenceType() instanceof ClassType) {
            Queue<InterfaceType> queue = new LinkedList<>(((ClassType) obj.referenceType()).interfaces());
            while (!queue.isEmpty()) {
                InterfaceType t = queue.poll();
                if (t.name().equals(iface)) {
                    return true;
                }
                queue.addAll(t.superinterfaces());
            }
        }
        return false;
    }

    // input format: [package.]ClassName:lineno or [package.]ClassName
    static boolean isInternalPackage(final String name) {
        return Arrays.stream(INTERNAL_PACKAGES).anyMatch(name::startsWith);
    }
}
