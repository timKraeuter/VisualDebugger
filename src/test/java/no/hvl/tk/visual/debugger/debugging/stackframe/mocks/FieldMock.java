package no.hvl.tk.visual.debugger.debugging.stackframe.mocks;

import com.sun.jdi.*;
import org.jetbrains.annotations.NotNull;

public class FieldMock implements Field {
    private final String fieldName;
    private final String typeName;

    public FieldMock(final String fieldName, final String typeName) {
        this.fieldName = fieldName;
        this.typeName = typeName;
    }

    @Override
    public String typeName() {
        return this.typeName;
    }

    @Override
    public Type type() throws ClassNotLoadedException {
        return new TypeMock(this.typeName);
    }

    @Override
    public String name() {
        return this.fieldName;
    }

    // Below is irrelevant.

    @Override
    public boolean isTransient() {
        return false;
    }

    @Override
    public boolean isVolatile() {
        return false;
    }

    @Override
    public boolean isEnumConstant() {
        return false;
    }

    @Override
    public String signature() {
        return null;
    }

    @Override
    public String genericSignature() {
        return null;
    }

    @Override
    public ReferenceType declaringType() {
        return null;
    }

    @Override
    public boolean isStatic() {
        return false;
    }

    @Override
    public boolean isFinal() {
        return false;
    }

    @Override
    public boolean isSynthetic() {
        return false;
    }

    @Override
    public int modifiers() {
        return 0;
    }

    @Override
    public boolean isPrivate() {
        return false;
    }

    @Override
    public boolean isPackagePrivate() {
        return false;
    }

    @Override
    public boolean isProtected() {
        return false;
    }

    @Override
    public boolean isPublic() {
        return false;
    }

    @Override
    public VirtualMachine virtualMachine() {
        return null;
    }

    @Override
    public int compareTo(@NotNull final Field field) {
        return 0;
    }
}
