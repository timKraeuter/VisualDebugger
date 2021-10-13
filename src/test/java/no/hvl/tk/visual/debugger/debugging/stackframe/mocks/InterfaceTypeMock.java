package no.hvl.tk.visual.debugger.debugging.stackframe.mocks;

import com.sun.jdi.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InterfaceTypeMock implements InterfaceType {
    private final String typeName;

    public InterfaceTypeMock(final String typeName) {
        this.typeName = typeName;
    }

    @Override
    public String name() {
        return this.typeName;
    }

    // Below is irrelevant

    @Override
    public List<InterfaceType> superinterfaces() {
        return new ArrayList<>();
    }

    @Override
    public List<InterfaceType> subinterfaces() {
        return null;
    }

    @Override
    public List<ClassType> implementors() {
        return null;
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
    public ClassLoaderReference classLoader() {
        return null;
    }

    @Override
    public String sourceName() {
        return null;
    }

    @Override
    public List<String> sourceNames(final String s) {
        return null;
    }

    @Override
    public List<String> sourcePaths(final String s) {
        return null;
    }

    @Override
    public String sourceDebugExtension() {
        return null;
    }

    @Override
    public boolean isStatic() {
        return false;
    }

    @Override
    public boolean isAbstract() {
        return false;
    }

    @Override
    public boolean isFinal() {
        return false;
    }

    @Override
    public boolean isPrepared() {
        return false;
    }

    @Override
    public boolean isVerified() {
        return false;
    }

    @Override
    public boolean isInitialized() {
        return false;
    }

    @Override
    public boolean failedToInitialize() {
        return false;
    }

    @Override
    public List<Field> fields() {
        return null;
    }

    @Override
    public List<Field> visibleFields() {
        return null;
    }

    @Override
    public List<Field> allFields() {
        return null;
    }

    @Override
    public Field fieldByName(final String s) {
        return null;
    }

    @Override
    public List<Method> methods() {
        return null;
    }

    @Override
    public List<Method> visibleMethods() {
        return null;
    }

    @Override
    public List<Method> allMethods() {
        return null;
    }

    @Override
    public List<Method> methodsByName(final String s) {
        return null;
    }

    @Override
    public List<Method> methodsByName(final String s, final String s1) {
        return null;
    }

    @Override
    public List<ReferenceType> nestedTypes() {
        return null;
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
    public ClassObjectReference classObject() {
        return null;
    }

    @Override
    public List<Location> allLineLocations() {
        return null;
    }

    @Override
    public List<Location> allLineLocations(final String s, final String s1) {
        return null;
    }

    @Override
    public List<Location> locationsOfLine(final int i) {
        return null;
    }

    @Override
    public List<Location> locationsOfLine(final String s, final String s1, final int i) {
        return null;
    }

    @Override
    public List<String> availableStrata() {
        return null;
    }

    @Override
    public String defaultStratum() {
        return null;
    }

    @Override
    public List<ObjectReference> instances(final long l) {
        return null;
    }

    @Override
    public int majorVersion() {
        return 0;
    }

    @Override
    public int minorVersion() {
        return 0;
    }

    @Override
    public int constantPoolCount() {
        return 0;
    }

    @Override
    public byte[] constantPool() {
        return new byte[0];
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
    public int compareTo(@NotNull final ReferenceType referenceType) {
        return 0;
    }
}
