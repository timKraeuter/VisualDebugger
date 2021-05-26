package no.hvl.tk.visual.debugger.debugging;

import com.intellij.debugger.engine.JavaValue;
import com.intellij.debugger.engine.evaluation.EvaluateException;
import com.intellij.debugger.engine.evaluation.EvaluateRuntimeException;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Pair;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.xdebugger.frame.*;
import com.intellij.xdebugger.frame.presentation.XValuePresentation;
import com.jetbrains.jdi.ObjectReferenceImpl;
import com.sun.jdi.Field;
import com.sun.jdi.Value;
import no.hvl.tk.visual.debugger.Settings;
import no.hvl.tk.visual.debugger.debugging.concurrency.CounterBasedLock;
import no.hvl.tk.visual.debugger.debugging.visualization.DebuggingInfoVisualizer;
import no.hvl.tk.visual.debugger.domain.ODObject;
import no.hvl.tk.visual.debugger.domain.PrimitiveTypes;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Objects;
import java.util.Optional;

public class NodeDebugVisualizer implements XCompositeNode {
    private static final Logger LOGGER = Logger.getInstance(NodeDebugVisualizer.class);

    private final DebuggingInfoVisualizer debuggingInfoCollector;
    private final int depth;

    private CounterBasedLock lock;
    /**
     * Parent. Null if at root.
     */
    private final ODObject parent;
    private String inheritedLinkType;

    public NodeDebugVisualizer(
            final DebuggingInfoVisualizer debuggingInfoCollector,
            final int depth,
            final CounterBasedLock lock) {
        this(debuggingInfoCollector, depth, lock, null, "");
    }

    public NodeDebugVisualizer(
            final DebuggingInfoVisualizer debuggingInfoCollector,
            final int depth,
            CounterBasedLock lock,
            final ODObject parent,
            String inheritedLinkType) {
        this.debuggingInfoCollector = debuggingInfoCollector;
        this.depth = depth;
        this.lock = lock;
        this.parent = parent;
        this.inheritedLinkType = inheritedLinkType;
    }

    @Override
    public void addChildren(@NotNull XValueChildrenList children, boolean last) {
        for (int i = 0; i < children.size(); i++) {
            JavaValue value = (JavaValue) children.getValue(i);
            this.handleValue(value);
        }
        if (last) {
            this.lock.decreaseCounter();
        }
    }

    void handleValue(final JavaValue jValue) {
        final String variableName = jValue.getName();
        Optional<String> maybeTypeName = getTypeIfExists(jValue);
        if (maybeTypeName.isEmpty()) {
            // If type is null the value of the variable is null
            this.addValueToDiagram(variableName, null, null);
            return;
        }
        final String typeName = maybeTypeName.get();

        if (PrimitiveTypes.isNonBoxedPrimitiveType(typeName)) {
            final String varValue = getNonBoxedPrimitiveValue(jValue);
            addValueToDiagram(variableName, typeName, varValue);
            return;
        }
        if (PrimitiveTypes.isBoxedPrimitiveType(typeName)) {
            final String varValue = getBoxedPrimitiveValue(jValue);
            this.addValueToDiagram(variableName, typeName, varValue);
            return;
        }
        // Handle object case here.
        if (depth > 0) {
            final Pair<ODObject, String> parentAndHasCollectionSkipped = addObjectAndLinksToDiagram(
                    jValue,
                    variableName,
                    typeName);
            this.lock.increaseCounter();
            final NodeDebugVisualizer nodeDebugVisualizer = new NodeDebugVisualizer(
                    this.debuggingInfoCollector,
                    depth - 1,
                    this.lock,
                    parentAndHasCollectionSkipped.getFirst(),
                    parentAndHasCollectionSkipped.getSecond());
            // Calling compute presentation fixes a value not being ready error.
            jValue.computePresentation(new NOOPXValueNode(), XValuePlace.TREE);
            jValue.computeChildren(nodeDebugVisualizer);
            // Decrease the counter here if computeChildren() will not be called on the new debug node.
            jValue.computePresentation(new XValueNode() {
                @Override
                public void setPresentation(@Nullable Icon icon, @NonNls @Nullable String type, @NonNls @NotNull String value, boolean hasChildren) {
                    this.decreaseCounterIfNeeded(jValue, hasChildren);
                }

                @Override
                public void setPresentation(@Nullable Icon icon, @NotNull XValuePresentation presentation, boolean hasChildren) {
                    this.decreaseCounterIfNeeded(jValue, hasChildren);
                }

                @SuppressWarnings("UnstableApiUsage")
                @Override
                public void setPresentation(@Nullable Icon icon, @NonNls @Nullable String type, @NonNls @NotNull String separator, @NonNls @Nullable String value, boolean hasChildren) {
                    this.decreaseCounterIfNeeded(jValue, hasChildren);
                }

                @Override
                public void setFullValueEvaluator(@NotNull XFullValueEvaluator fullValueEvaluator) {
                    // nop
                }


                private void decreaseCounterIfNeeded(final JavaValue value, boolean hasChildren) {
                    try {
                        final Value calcedValue = value.getDescriptor().calcValue(value.getEvaluationContext());
                        if (calcedValue instanceof ObjectReferenceImpl) {
                            ObjectReferenceImpl obRef = (ObjectReferenceImpl) calcedValue;
                            final int fieldSize = obRef.referenceType().allFields().size();
                            if (fieldSize == 0 || !hasChildren) {
                                NodeDebugVisualizer.this.lock.decreaseCounter();
                            }
                        }
                    } catch (EvaluateException e) {
                        LOGGER.error(e);
                        throw new EvaluateRuntimeException(e);
                    }
                }
            }, XValuePlace.TREE);
        }
    }

    private Pair<ODObject, String> addObjectAndLinksToDiagram(JavaValue jValue, String variableName, String typeName) {
        // Skip lists and sets. They will be unfolded. Remember the original link type.
        if (Settings.SKIP_COLLECTION_VISUALIZATION
                && (typeName.endsWith("Set") || typeName.endsWith("List"))
                && parent != null) {
            return Pair.create(parent, this.getLinkType(jValue));
        }
        // Normal objects
        final ODObject object = new ODObject(this.getObjectId(jValue), typeName, variableName);
        this.debuggingInfoCollector.addObject(object);
        if (this.parent != null) {
            this.debuggingInfoCollector.addLinkToObject(this.parent, object, this.getLinkType(jValue));
        }
        return Pair.create(object, "");
    }

    private long getObjectId(JavaValue jValue) {
        try {
            final ObjectReferenceImpl value = (ObjectReferenceImpl) jValue.getDescriptor().calcValue(jValue.getEvaluationContext());
            return value.uniqueID();
        } catch (EvaluateException e) {
            LOGGER.error(e);
            throw new EvaluateRuntimeException(e);
        }
    }

    private String getLinkType(JavaValue value) {
        // When skipping over collections we want to use the original link name for each subsequent link.
        if (!this.inheritedLinkType.isEmpty()) {
            return this.inheritedLinkType;
        }
        return value.getName();
    }

    private void addValueToDiagram(final String variableOrFieldName, final String typeName, final String varValue) {
        if (Objects.isNull(this.parent)) {
            this.debuggingInfoCollector.addPrimitiveRootValue(variableOrFieldName, typeName, varValue);
        } else {
            this.debuggingInfoCollector.addAttributeToObject(this.parent, variableOrFieldName, varValue, typeName);
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
            throw new EvaluateRuntimeException(e);
        }
    }

    private String getNonBoxedPrimitiveValue(final JavaValue value) {
        try {
            return value.getDescriptor().calcValue(value.getEvaluationContext()).toString();
        } catch (EvaluateException e) {
            LOGGER.error(e);
            throw new EvaluateRuntimeException(e);
        }
    }

    private Optional<String> getTypeIfExists(final JavaValue value) {
        try {
            final Value calcedValue = value.getDescriptor().calcValue(value.getEvaluationContext());
            if (calcedValue == null) {
                return Optional.empty();
            }
            return Optional.of(calcedValue.type().name());
        } catch (EvaluateException e) {
            LOGGER.error(e);
            throw new EvaluateRuntimeException(e);
        }
    }

    @Override
    public void tooManyChildren(int remaining) {
        LOGGER.debug("tooManyChildren called!");
    }

    @Override
    public void setAlreadySorted(boolean alreadySorted) {
        LOGGER.debug("setAlreadySorted called!");
    }

    @Override
    public void setErrorMessage(@NotNull String errorMessage) {
        LOGGER.warn(errorMessage);
    }

    @Override
    public void setErrorMessage(@NotNull String errorMessage, @Nullable XDebuggerTreeNodeHyperlink link) {
        LOGGER.warn(errorMessage);
    }

    @Override
    public void setMessage(
            @NotNull String message,
            @Nullable Icon icon,
            @NotNull SimpleTextAttributes attributes,
            @Nullable XDebuggerTreeNodeHyperlink link) {
        LOGGER.debug(message);
    }
}