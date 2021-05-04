package no.hvl.tk.visualDebugger.debugging;

import com.intellij.debugger.engine.JavaValue;
import com.intellij.debugger.engine.evaluation.EvaluateException;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.xdebugger.frame.XCompositeNode;
import com.intellij.xdebugger.frame.XDebuggerTreeNodeHyperlink;
import com.intellij.xdebugger.frame.XValueChildrenList;
import com.intellij.xdebugger.frame.XValuePlace;
import com.jetbrains.jdi.ObjectReferenceImpl;
import com.sun.jdi.Field;
import com.sun.jdi.Value;
import no.hvl.tk.visualDebugger.domain.ODObject;
import no.hvl.tk.visualDebugger.domain.PrimitiveTypes;
import no.hvl.tk.visualDebugger.debugging.visualization.DebuggingVisualizer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class NodeDebugVisualizer implements XCompositeNode {
    private static final Logger LOGGER = Logger.getInstance(NodeDebugVisualizer.class);

    private final DebuggingVisualizer debuggingVisualizer;
    private final int depth;

    private CounterBasedLock lock;
    /**
     * Parent. Null if at root.
     */
    private final ODObject parent;

    public NodeDebugVisualizer(
            final DebuggingVisualizer debuggingVisualizer,
            final int depth,
            final CounterBasedLock lock) {
        this(debuggingVisualizer, depth, lock, null);
    }

    public NodeDebugVisualizer(
            final DebuggingVisualizer debuggingVisualizer,
            final int depth,
            CounterBasedLock lock,
            final ODObject parent) {
        this.debuggingVisualizer = debuggingVisualizer;
        this.depth = depth;
        this.lock = lock;
        this.parent = parent;
    }

    @Override
    public void addChildren(@NotNull XValueChildrenList children, boolean last) {
        for (int i = 0; i < children.size(); i++) {
            JavaValue value = (JavaValue) children.getValue(i);
            this.handleValue(value);
        }
        this.lock.decreaseCounter();
    }

    void handleValue(final JavaValue value) {
        final String variableName = value.getName();
        String typeName = getType(value);
        if (PrimitiveTypes.isNonBoxedPrimitiveType(typeName)) {
            final String varValue = getNonBoxedPrimitiveValue(value);
            addValueToDiagram(variableName, typeName, varValue);
            return;
        }
        if (PrimitiveTypes.isBoxedPrimitiveType(typeName)) {
            final String varValue = getBoxedPrimitiveValue(value);
            this.addValueToDiagram(variableName, typeName, varValue);
            return;
        }
        // Handle object case here.
        if (depth > 0) {
            final ODObject object = new ODObject(typeName, variableName);
            this.debuggingVisualizer.addObject(object);
            if (this.parent != null) {
               this.debuggingVisualizer.addLinkToObject(this.parent, object, this.getLinkType(), variableName);
            }
            increaseCounterIfNeeded(value);
            final NodeDebugVisualizer nodeDebugVisualizer = new NodeDebugVisualizer(this.debuggingVisualizer, depth - 1, this.lock, object);
            // Calling compute presentation fixes and Value not beeing ready error.
            value.computePresentation(new NOPXValueNode(), XValuePlace.TREE);
            value.computeChildren(nodeDebugVisualizer);
        }
    }

    private String getLinkType() {
        // TODO implement get link type
        return null;
    }

    private void increaseCounterIfNeeded(final JavaValue value) {
        try {
            final Value calcedValue = value.getDescriptor().calcValue(value.getEvaluationContext());
            if (calcedValue instanceof ObjectReferenceImpl) {
                ObjectReferenceImpl obRef = (ObjectReferenceImpl) calcedValue;
                final int fieldSize = obRef.referenceType().allFields().size();
                if (fieldSize != 0) {
                    this.lock.increaseCounter();
                }
            }
        } catch (EvaluateException e) {
            LOGGER.error(e);
            throw new RuntimeException(e);
        }
    }

    private void addValueToDiagram(final String variableName, final String typeName, final String varValue) {
        if (Objects.isNull(this.parent)) {
            this.debuggingVisualizer.addPrimitiveRootValue(variableName, typeName, varValue);
        } else {
            this.debuggingVisualizer.addAttributeToObject(this.parent, typeName, varValue);
        }
    }

    private String getBoxedPrimitiveValue(JavaValue value) {
        try {
            final ObjectReferenceImpl value1 = (ObjectReferenceImpl) value.getDescriptor().calcValue(value.getEvaluationContext());
            @SuppressWarnings("OptionalGetWithoutIsPresent") final Field valueField = value1.referenceType().allFields().stream()
                                                                                            .filter(field -> "value".equals(field.name()))
                                                                                            .findFirst()
                                                                                            .get(); // Should always have a "value" field.
            return value1.getValue(valueField).toString();
        } catch (EvaluateException e) {
            LOGGER.error(e);
            throw new RuntimeException(e);
        }
    }

    private String getNonBoxedPrimitiveValue(final JavaValue value) {
        try {
            return value.getDescriptor().calcValue(value.getEvaluationContext()).toString();
        } catch (EvaluateException e) {
            LOGGER.error(e);
            throw new RuntimeException(e);
        }
    }

    private String getType(final JavaValue value) {
        try {
            return value.getDescriptor().calcValue(value.getEvaluationContext()).type().name();
        } catch (EvaluateException e) {
            LOGGER.error(e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void tooManyChildren(int remaining) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAlreadySorted(boolean alreadySorted) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setErrorMessage(@NotNull String errorMessage) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setErrorMessage(@NotNull String errorMessage, @Nullable XDebuggerTreeNodeHyperlink link) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setMessage(@NotNull String message, @Nullable Icon icon, @NotNull SimpleTextAttributes attributes, @Nullable XDebuggerTreeNodeHyperlink link) {
        throw new UnsupportedOperationException();
    }
}