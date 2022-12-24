package no.hvl.tk.visual.debugger.debugging.stackframe.mocks;

import com.sun.jdi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StackFrameMock implements StackFrame {

    private final ObjectReference thisObj;
    private final Map<LocalVariable, Value> localVars = new HashMap<>();

    public StackFrameMock(ObjectReference thisObj) {
        this.thisObj = thisObj;
    }

    @Override
    public Location location() {
        return null;
    }

    @Override
    public ThreadReference thread() {
        return null;
    }

    @Nullable
    @Override
    public ObjectReference thisObject() {
        return thisObj;
    }

    @NotNull
    @Override
    public List<LocalVariable> visibleVariables() {
        return new ArrayList<>(localVars.keySet());
    }

    @Nullable
    @Override
    public LocalVariable visibleVariableByName(String name) {
        return null;
    }

    @Override
    public Value getValue(LocalVariable variable) {
        return this.localVars.get(variable);
    }

    @NotNull
    @Override
    public Map<LocalVariable, Value> getValues(List<? extends LocalVariable> variables) {
        return localVars;
    }

    @Override
    public void setValue(LocalVariable variable, Value value)  {
        this.localVars.put(variable, value);
    }

    @NotNull
    @Override
    public List<Value> getArgumentValues() {
        throw new UnsupportedOperationException("tbd");
    }

    @Override
    public VirtualMachine virtualMachine() {
        return null;
    }
}
