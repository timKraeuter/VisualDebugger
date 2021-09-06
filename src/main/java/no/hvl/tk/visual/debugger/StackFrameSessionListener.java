package no.hvl.tk.visual.debugger;

import com.intellij.debugger.engine.JavaValue;
import com.intellij.debugger.engine.SuspendContext;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Pair;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebugSessionListener;
import com.intellij.xdebugger.frame.XStackFrame;
import com.sun.jdi.*;
import no.hvl.tk.visual.debugger.debugging.visualization.DebuggingInfoVisualizer;
import no.hvl.tk.visual.debugger.debugging.visualization.DebuggingInfoVisualizerBase;
import no.hvl.tk.visual.debugger.domain.ODObject;
import no.hvl.tk.visual.debugger.domain.PrimitiveTypes;
import no.hvl.tk.visual.debugger.settings.PluginSettingsState;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class StackFrameSessionListener implements XDebugSessionListener {

    private static final Logger LOGGER = Logger.getInstance(StackFrameSessionListener.class);
    private static final int SUFFIX_LENGTH = ".java".length();

    private final XDebugSession debugSession;
    private DebuggingInfoVisualizer debuggingVisualizer;

    public StackFrameSessionListener(XDebugSession debugSession) {
        this.debugSession = debugSession;
    }

    @Override
    public void sessionPaused() {
        StackFrame stackFrame = this.getCorrectStackFrame(debugSession);
        this.debuggingVisualizer = new DebuggingInfoVisualizerBase() {
            @Override
            protected void preAddObject() {

            }

            @Override
            public DebuggingInfoVisualizer addDebugNodeForObject(ODObject object, JavaValue jValue) {
                return null;
            }

            @Override
            public Pair<ODObject, JavaValue> getDebugNodeAndObjectForObjectId(String objectId) {
                return null;
            }

            @Override
            public void finishVisualization() {

            }

            @Override
            public void debuggingActivated() {

            }

            @Override
            public void debuggingDeactivated() {

            }
        };

        ObjectReference thisObjectReference = stackFrame.thisObject();
        assert thisObjectReference != null;

        this.convertObjectReference(
                "this",
                thisObjectReference,
                stackFrame,
                null,
                PluginSettingsState.getInstance().getVisualisationDepth());

        try {
            // All local variables in the current method
            final List<LocalVariable> methodVariables = stackFrame.location().method().variables();
            methodVariables.forEach(localVariable -> this.convertVariable(
                    localVariable,
                    stackFrame,
                    null,
                    PluginSettingsState.getInstance().getVisualisationDepth()));

            // All method arguments
            final List<LocalVariable> methodArguments = stackFrame.location().method().arguments();
            methodArguments.forEach(localVariable -> this.convertVariable(
                    localVariable,
                    stackFrame,
                    null,
                    PluginSettingsState.getInstance().getVisualisationDepth()));
        } catch (AbsentInformationException e) {
            // OK
        }
        this.debuggingVisualizer.finishVisualization();
    }

    private void convertObjectReference(
            String name,
            ObjectReference objectReference,
            StackFrame stackFrame,
            ODObject parentIfExists,
            Integer visualisationDepth) {
        if (visualisationDepth <= 0) {
            return;
        }
        final String objectType = objectReference.referenceType().name();
        if (PrimitiveTypes.isBoxedPrimitiveType(objectType)) {
            final Value value = objectReference.getValue(objectReference.referenceType().fieldByName("value"));
            this.convertValue(value, name, objectType, stackFrame, parentIfExists, visualisationDepth);
            return;
        }
        // TODO save seen objects.
        long objectID = objectReference.uniqueID();
        Map<Field, Value> fields = objectReference.getValues(objectReference.referenceType().allFields());
        // TODO map fields, reduce depth and continue
    }

    private void convertVariable(
            LocalVariable localVariable,
            StackFrame stackFrame,
            ODObject parentIfExists,
            int depth) {
        final Value variableValue = stackFrame.getValue(localVariable);
        final String variableName = localVariable.name();
        final String variableType = localVariable.typeName();
        this.convertValue(variableValue, variableName, variableType, stackFrame, parentIfExists, depth);
    }

    private void convertValue(
            Value variableValue,
            String variableName,
            String variableType,
            StackFrame stackFrame,
            ODObject parentIfExists,
            int depth) {
        if (variableValue instanceof BooleanValue) {
            final String value = String.valueOf(((BooleanValue) variableValue).value());
            this.addVariableToDiagram(variableName, variableType, value, parentIfExists);
            return;
        }
        if (variableValue instanceof ByteValue) {
            final String value = String.valueOf(((ByteValue) variableValue).value());
            this.addVariableToDiagram(variableName, variableType, value, parentIfExists);
            return;
        }
        if (variableValue instanceof ShortValue) {
            final String value = String.valueOf(((ShortValue) variableValue).value());
            this.addVariableToDiagram(variableName, variableType, value, parentIfExists);
            return;
        }
        if (variableValue instanceof IntegerValue) {
            final String value = Integer.toString(((IntegerValue) variableValue).value());
            this.addVariableToDiagram(variableName, variableType, value, parentIfExists);
            return;
        }
        if (variableValue instanceof LongValue) {
            final String value = Long.toString(((LongValue) variableValue).value());
            this.addVariableToDiagram(variableName, variableType, value, parentIfExists);
            return;
        }
        if (variableValue instanceof FloatValue) {
            final String value = Float.toString(((FloatValue) variableValue).value());
            this.addVariableToDiagram(variableName, variableType, value, parentIfExists);
            return;
        }
        if (variableValue instanceof DoubleValue) {
            final String value = Double.toString(((DoubleValue) variableValue).value());
            this.addVariableToDiagram(variableName, variableType, value, parentIfExists);
            return;
        }
        if (variableValue instanceof CharValue) {
            final String value = Character.toString(((CharValue) variableValue).value());
            this.addVariableToDiagram(variableName, variableType, value, parentIfExists);
            return;
        }
        if (variableValue instanceof StringReference) {
            final String value = ((StringReference) variableValue).value();
            this.addVariableToDiagram(variableName, variableType, value, parentIfExists);
            return;
        }
        if (!(variableValue instanceof ObjectReference)) {
            ObjectReference obj = (ObjectReference) variableValue;
            // TODO add link if parent exists
            this.convertObjectReference(variableName, obj, stackFrame, parentIfExists, depth);
        }
        throw new RuntimeException("Unexpected value encountered " + variableValue.toString());
    }

    private void addVariableToDiagram(String variableName, String variableType, String value, ODObject parentIfExists) {
        if (parentIfExists != null) {
            this.debuggingVisualizer.addAttributeToObject(parentIfExists, variableName, value, variableType);
        } else {
            this.debuggingVisualizer.addPrimitiveRootValue(variableName, variableType, value);
        }
    }


    private StackFrame getCorrectStackFrame(XDebugSession debugSession) {
        SuspendContext sc = (SuspendContext) debugSession.getSuspendContext();
        ThreadReference thread = sc.getThread().getThreadReference();
        try {
            final Optional<StackFrame> first = thread.frames().stream()
                                                     .filter(this::isCorrectStackFrame)
                                                     .findFirst();
            if (first.isPresent()) {
                return first.get();
            }
        } catch (IncompatibleThreadStateException e) {
            LOGGER.error(e);
            throw new RuntimeException("Correct stack frame for debugging not found!", e);
        }
        throw new RuntimeException("Correct stack frame for debugging not found!");
    }

    private boolean isCorrectStackFrame(StackFrame stackFrame) {
        final XStackFrame currentStackFrame = debugSession.getCurrentStackFrame();
        final String canonicalName = currentStackFrame.getSourcePosition().getFile().getName();
        // cut the .java
        final String wantedTypeName = canonicalName.substring(0, canonicalName.length() - SUFFIX_LENGTH);

        final String typeName = stackFrame.location().declaringType().name();

        return typeName.contains(wantedTypeName);
    }
}
