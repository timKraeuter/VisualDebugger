package no.hvl.tk.visual.debugger.debugging.stackframe.mocks;

import com.sun.jdi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ReferenceTypeMock implements ReferenceType, ClassType {
    private final String name;
    private final List<InterfaceType> interfaces = new ArrayList<>();

    public ReferenceTypeMock(final String name) {
        this.name = name;
    }

    public void addInterface(final InterfaceType type) {
        this.interfaces.add(type);
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public List<Field> allFields() {
        return new ArrayList<>();
    }

    @Override
    public List<Field> visibleFields() {
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
    public List<String> sourceNames(final String stratum) {
        return null;
    }

    @Override
    public List<String> sourcePaths(final String stratum) {
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
    public Field fieldByName(final String fieldName) {
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
    public List<Method> methodsByName(final String name) {
        return Collections.singletonList(new MethodMock(name));
    }

    @Override
    public List<Method> methodsByName(final String name, final String signature) {
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
    public Map<Field, Value> getValues(final List<? extends Field> fields) {
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
    public List<Location> allLineLocations(final String stratum, final String sourceName) {
        return null;
    }

    @Override
    public List<Location> locationsOfLine(final int lineNumber) {
        return null;
    }

    @Override
    public List<Location> locationsOfLine(final String stratum, final String sourceName, final int lineNumber) {
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
    public List<ObjectReference> instances(final long maxInstances) {
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
    public int compareTo(@NotNull final ReferenceType o) {
        return 0;
    }

    @Nullable
    @Override
    public ClassType superclass() {
        return null;
    }

    @Override
    public List<InterfaceType> interfaces() {
        return this.interfaces;
    }

    @Override
    public List<InterfaceType> allInterfaces() {
        return null;
    }

    @Override
    public List<ClassType> subclasses() {
        return null;
    }

    @Override
    public boolean isEnum() {
        return false;
    }

    @Override
    public void setValue(final Field field, final Value value) {

    }

    @Override
    public Value invokeMethod(final ThreadReference threadReference, final Method method, final List<? extends Value> list, final int i) {
        return null;
    }

    @Override
    public ObjectReference newInstance(final ThreadReference threadReference, final Method method, final List<? extends Value> list, final int i) {
        return null;
    }

    @Override
    public Method concreteMethodByName(final String s, final String s1) {
        return null;
    }
}
