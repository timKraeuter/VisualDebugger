package no.hvl.tk.visual.debugger.debugging.stackframe;

import com.intellij.debugger.engine.SuspendContext;
import com.intellij.execution.ui.RunnerLayoutUi;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.util.IconLoader;
import com.intellij.util.ui.UIUtil;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebugSessionListener;
import com.sun.jdi.*;
import no.hvl.tk.visual.debugger.DebugProcessListener;
import no.hvl.tk.visual.debugger.SharedState;
import no.hvl.tk.visual.debugger.debugging.visualization.DebuggingInfoVisualizer;
import no.hvl.tk.visual.debugger.debugging.visualization.PlantUmlDebuggingVisualizer;
import no.hvl.tk.visual.debugger.debugging.visualization.WebSocketDebuggingVisualizer;
import no.hvl.tk.visual.debugger.domain.ODObject;
import no.hvl.tk.visual.debugger.domain.PrimitiveTypes;
import no.hvl.tk.visual.debugger.settings.PluginSettingsState;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

import static no.hvl.tk.visual.debugger.debugging.stackframe.StackFrameSessionListenerHelper.*;

public class StackFrameSessionListener implements XDebugSessionListener {

    private static final Logger LOGGER = Logger.getInstance(StackFrameSessionListener.class);
    private static final int SUFFIX_LENGTH = ".java".length();
    private static final String CONTENT_ID = "no.hvl.tk.VisualDebugger";
    private static final String TOOLBAR_ACTION = "VisualDebugger.VisualizerToolbar"; // has to match with plugin.xml


    private JPanel userInterface;

    private final XDebugSession debugSession;
    private DebuggingInfoVisualizer debuggingVisualizer;
    private ThreadReference thread;

    public StackFrameSessionListener(XDebugSession debugSession) {
        this.debugSession = debugSession;
    }

    @Override
    public void sessionPaused() {
        this.initUIIfNeeded();

        startVisualDebugging();
    }

    private void startVisualDebugging() {
        if (!SharedState.isDebuggingActive()) {
            return;
        }
        StackFrame stackFrame = this.getCorrectStackFrame(debugSession);

        visualizeThisObject(stackFrame);
        visualizeVariables(stackFrame);

        this.debuggingVisualizer.finishVisualization();
    }

    private void visualizeThisObject(StackFrame stackFrame) {
        ObjectReference thisObjectReference = stackFrame.thisObject();
        assert thisObjectReference != null;

        this.convertObjectReference(
                "this",
                thisObjectReference,
                stackFrame,
                null,
                PluginSettingsState.getInstance().getVisualisationDepth());
    }

    private void visualizeVariables(StackFrame stackFrame) {
        try {
            // All visible variables in the stack frame
            final List<LocalVariable> methodVariables = stackFrame.visibleVariables();
            methodVariables.forEach(localVariable -> this.convertVariable(
                    localVariable,
                    stackFrame,
                    null,
                    PluginSettingsState.getInstance().getVisualisationDepth()));
        } catch (AbsentInformationException e) {
            // OK
        }
    }

    private void initUIIfNeeded() {
        if (this.userInterface != null) {
            return;
        }
        this.userInterface = new JPanel();
        this.getOrCreateDebuggingInfoVisualizer(); // make sure visualizer is initialized
        if (!SharedState.isDebuggingActive()) {
            this.resetUIAndAddActivateDebuggingButton();
        } else {
            this.debuggingVisualizer.debuggingActivated();
        }
        final var uiContainer = new SimpleToolWindowPanel(false, true);

        final var actionManager = ActionManager.getInstance();
        final var actionToolbar = actionManager.createActionToolbar(
                TOOLBAR_ACTION,
                (DefaultActionGroup) actionManager.getAction(TOOLBAR_ACTION),
                false
        );
        actionToolbar.setTargetComponent(this.userInterface);
        uiContainer.setToolbar(actionToolbar.getComponent());
        uiContainer.setContent(this.userInterface);

        final RunnerLayoutUi ui = this.debugSession.getUI();
        final var content = ui.createContent(
                CONTENT_ID,
                uiContainer,
                "Visual Debugger",
                IconLoader.getIcon("/icons/icon_16x16.png", DebugProcessListener.class),
                null);
        content.setCloseable(false);
        UIUtil.invokeLaterIfNeeded(() -> ui.addContent(content));
        LOGGER.debug("UI initialized!");
    }

    public void resetUIAndAddActivateDebuggingButton() {
        this.userInterface.removeAll();
        this.userInterface.setLayout(new FlowLayout());

        final var activateButton = new JButton("Activate visual debugger");
        activateButton.addActionListener(actionEvent -> {

            SharedState.setDebuggingActive(true);
            this.userInterface.remove(activateButton);
            this.debuggingVisualizer.debuggingActivated();
            this.userInterface.revalidate();
            this.startVisualDebugging();
        });
        this.userInterface.add(activateButton);

        this.userInterface.revalidate();
        this.userInterface.repaint();
    }

    @NotNull
    public DebuggingInfoVisualizer getOrCreateDebuggingInfoVisualizer() {
        if (this.debuggingVisualizer == null) {
            switch (PluginSettingsState.getInstance().getVisualizerOption()) {
                case WEB_UI:
                    this.debuggingVisualizer = new WebSocketDebuggingVisualizer(this.userInterface);
                    break;
                case EMBEDDED:
                    this.debuggingVisualizer = new PlantUmlDebuggingVisualizer(this.userInterface);
                    break;
                default:
                    LOGGER.warn("Unrecognized debugging visualizer chosen. Defaulting to web visualizer!");
                    this.debuggingVisualizer = new WebSocketDebuggingVisualizer(this.userInterface);
            }
        }
        return this.debuggingVisualizer;
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
        if (objectReference instanceof ArrayReference) {
            convertArray(name, (ArrayReference) objectReference, stackFrame, parentIfExists, visualisationDepth, objectType);
            return;
        }
        if (objectReference instanceof StringReference) {
            // TODO check if we really need this again!
            final String value = ((StringReference) objectReference).value();
            this.addVariableToDiagram(name, objectType, value, parentIfExists);
            return;
        }
        if ((doesImplementInterface(objectReference, "java.util.List")
                || doesImplementInterface(objectReference, "java.util.Set"))
                && isInternalPackage(objectType)) {
            convertListOrSet(name, objectReference, stackFrame, parentIfExists, visualisationDepth, objectType);
            return;
        }

        if (doesImplementInterface(objectReference, "java.util.Map") && isInternalPackage(objectType)) {
            convertMap(name, objectReference, stackFrame, parentIfExists, visualisationDepth, objectType);
            return;
        }

        // TODO save seen objects.
        final ODObject object = new ODObject(objectReference.uniqueID(), objectType, name);
        if (parentIfExists != null) {
            debuggingVisualizer.addLinkToObject(parentIfExists, object, "associationName");
        }
        this.debuggingVisualizer.addObject(object);

        // Filter static fields? Or non visible fields?
        for (Map.Entry<Field, Value> fieldValueEntry : objectReference.getValues(objectReference.referenceType().allFields()).entrySet()) {
            final String fieldName = fieldValueEntry.getKey().name();
            this.convertValue(
                    fieldValueEntry.getValue(),
                    fieldName,
                    fieldValueEntry.getKey().typeName(),
                    stackFrame,
                    object,
                    visualisationDepth - 1);
        }
    }

    private void convertArray(
            String name,
            ArrayReference arrayRef,
            StackFrame stackFrame,
            ODObject parentIfExists,
            Integer visualisationDepth,
            String objectType) {
        final ODObject parent = createParentIfNeededForCollection(name, arrayRef, parentIfExists, objectType);
        for (int i = 0; i < arrayRef.length(); i++) {
            final Value value = arrayRef.getValue(i);
            this.convertValue(
                    value,
                    String.valueOf(i),
                    value.type().name(),
                    stackFrame,
                    parent,
                    parentIfExists != null ? visualisationDepth : visualisationDepth - 1);
        }
    }

    @NotNull
    private ODObject createParentIfNeededForCollection(
            String name,
            ObjectReference obRef,
            ODObject parentIfExists,
            String objectType) {
        final ODObject parent;
        if (parentIfExists != null) {
            parent = parentIfExists;
        } else {
            parent = new ODObject(obRef.uniqueID(), objectType, name);
            this.debuggingVisualizer.addObject(parent);
        }
        return parent;
    }

    private void convertListOrSet(
            String name,
            ObjectReference collectionRef,
            StackFrame stackFrame,
            ODObject parentIfExists,
            Integer visualisationDepth,
            String objectType) {
        final ODObject parent = createParentIfNeededForCollection(name, collectionRef, parentIfExists, objectType);
        Iterator<Value> iterator = getIterator(thread, collectionRef);
        int i = 0;
        while (iterator.hasNext()) {
            final Value value = iterator.next();
            this.convertValue(
                    value,
                    String.valueOf(i), // TODO add association/link name
                    objectType,
                    stackFrame,
                    parent,
                    parentIfExists != null ? visualisationDepth : visualisationDepth - 1);
            i++;
        }
    }

    private void convertMap(
            String name,
            ObjectReference mapRef,
            StackFrame stackFrame,
            ODObject parentIfExists,
            Integer visualisationDepth,
            String objectType) {
        final ODObject parent = createParentIfNeededForCollection(name, mapRef, parentIfExists, objectType);
        ObjectReference entrySet = (ObjectReference) invokeSimple(thread, mapRef, "entrySet");
        Iterator<Value> iterator = getIterator(thread, entrySet);
        int i = 0;
        while (iterator.hasNext()) {
            ObjectReference entry = (ObjectReference) iterator.next();
            final Value keyValue = invokeSimple(thread, entry, "getKey");
            final Value valueValue = invokeSimple(thread, entry, "getValue");
            // TODO change name here
            final ODObject entryObject = new ODObject(entry.uniqueID(), entry.referenceType().name(), name);
            this.debuggingVisualizer.addObject(entryObject);
            // TODO check link type
            this.debuggingVisualizer.addLinkToObject(parent, entryObject, entry.referenceType().name());
            if (keyValue != null) {
                this.convertValue(
                        keyValue,
                        String.format("key %s", i),
                        keyValue.type() == null ? "" : keyValue.type().name(),
                        stackFrame,
                        entryObject,
                        visualisationDepth - 1);
            }
            if (valueValue != null) {
                this.convertValue(
                        valueValue,
                        String.format("value %s", i),
                        valueValue.type() == null ? "" : keyValue.type().name(),
                        stackFrame,
                        entryObject,
                        visualisationDepth - 1);
            }
            i++;
        }
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
        ObjectReference obj = (ObjectReference) variableValue;
        if (obj == null) {
            this.addVariableToDiagram(variableName, variableType, "null", parentIfExists);
            return;
        }
        // Only objects left.
        this.convertObjectReference(variableName, obj, stackFrame, parentIfExists, depth);
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
        thread = sc.getThread().getThreadReference();
        try {
            // TODO: We boldy assume the first stack frame is the right one, which it seems to be.
            final Optional<StackFrame> first = thread.frames().stream().findFirst();
            if (first.isPresent()) {
                return first.get();
            }
        } catch (IncompatibleThreadStateException e) {
            LOGGER.error(e);
            throw new RuntimeException("Correct stack frame for debugging not found!", e);
        }
        throw new RuntimeException("Correct stack frame for debugging not found!");
    }
}
