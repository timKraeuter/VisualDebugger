package no.hvl.tk.visual.debugger.debugging;

import com.intellij.debugger.engine.JavaValue;
import com.intellij.debugger.engine.evaluation.EvaluateException;
import com.intellij.debugger.engine.evaluation.EvaluateRuntimeException;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Pair;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.xdebugger.frame.*;
import com.intellij.xdebugger.frame.presentation.XValuePresentation;
import com.jetbrains.jdi.ArrayReferenceImpl;
import com.jetbrains.jdi.ObjectReferenceImpl;
import no.hvl.tk.visual.debugger.SharedState;
import no.hvl.tk.visual.debugger.debugging.concurrency.CounterBasedLock;
import no.hvl.tk.visual.debugger.debugging.visualization.DebuggingInfoVisualizer;
import no.hvl.tk.visual.debugger.domain.ODObject;
import no.hvl.tk.visual.debugger.domain.PrimitiveTypes;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class NodeDebugVisualizer implements XCompositeNode {
    private static final Logger LOGGER = Logger.getInstance(NodeDebugVisualizer.class);

    private final DebuggingInfoVisualizer debuggingInfoCollector;
    private final int depth;

    private final CounterBasedLock lock;
    private final Set<Long> seenObjectIds;
    /**
     * Parent. Null if at root.
     */
    private final ODObject parent;
    private final String inheritedLinkType;

    public NodeDebugVisualizer(
            final DebuggingInfoVisualizer debuggingInfoCollector,
            final int depth,
            final CounterBasedLock lock) {
        this(debuggingInfoCollector, depth, lock, null, "", new HashSet<>());
    }

    public NodeDebugVisualizer(
            final DebuggingInfoVisualizer debuggingInfoCollector,
            final int depth,
            final CounterBasedLock lock,
            final ODObject parent,
            final String inheritedLinkType,
            final Set<Long> seenObjectIds) {
        this.debuggingInfoCollector = debuggingInfoCollector;
        this.depth = depth;
        this.lock = lock;
        this.parent = parent;
        this.inheritedLinkType = inheritedLinkType;
        this.seenObjectIds = seenObjectIds;
    }

    @Override
    public void addChildren(@NotNull final XValueChildrenList children, final boolean last) {
        for (var i = 0; i < children.size(); i++) {
            final JavaValue value = (JavaValue) children.getValue(i);
            this.handleValue(value);
        }
        if (last) {
            this.lock.decreaseCounter();
        }
    }

    void handleValue(final JavaValue jValue) {
        final String variableName = jValue.getName();
        final Optional<String> maybeTypeName = NodeDebugVisualizer.getTypeIfExists(jValue);
        if (maybeTypeName.isEmpty()) {
            // If type is null the value of the variable is null
            this.addValueToDiagram(variableName, null, null);
            return;
        }
        final String typeName = maybeTypeName.get();

        if (PrimitiveTypes.isNonBoxedPrimitiveType(typeName)) {
            final String varValue = NodeDebugVisualizer.getNonBoxedPrimitiveValue(jValue);
            this.addValueToDiagram(variableName, typeName, varValue);
            return;
        }
        if (PrimitiveTypes.isBoxedPrimitiveType(typeName)) {
            final String varValue = NodeDebugVisualizer.getBoxedPrimitiveValue(jValue);
            this.addValueToDiagram(variableName, typeName, varValue);
            return;
        }
        this.handleObject(jValue, variableName, typeName);
    }

    private void handleObject(final JavaValue jValue, final String variableName, final String typeName) {
        final long objectId = NodeDebugVisualizer.getObjectId(jValue);
        if (this.depth >= 0) {

            final Pair<ODObject, String> parentAndHasCollectionSkipped = this.addObjectAndLinksToDiagram(
                    objectId,
                    jValue,
                    variableName,
                    typeName);
            if (this.seenObjectIds.contains(objectId)) {
                return;
            }
            this.seenObjectIds.add(objectId);
            this.lock.increaseCounter();
            final var nodeDebugVisualizer = new NodeDebugVisualizer(
                    this.debuggingInfoCollector,
                    this.depth - 1,
                    this.lock,
                    parentAndHasCollectionSkipped.getFirst(),
                    parentAndHasCollectionSkipped.getSecond(),
                    this.seenObjectIds);
            // Calling compute presentation fixes a value not being ready error.
            jValue.computePresentation(new NOOPXValueNode(), XValuePlace.TREE);
            jValue.computeChildren(nodeDebugVisualizer);
            // Decrease the counter here if computeChildren() will not be called on the new debug node.
            jValue.computePresentation(new XValueNode() {
                @Override
                public void setPresentation(@Nullable final Icon icon, @NonNls @Nullable final String type, @NonNls @NotNull final String value, final boolean hasChildren) {
                    this.decreaseCounterIfNeeded(jValue, hasChildren);
                }

                @Override
                public void setPresentation(@Nullable final Icon icon, @NotNull final XValuePresentation presentation, final boolean hasChildren) {
                    this.decreaseCounterIfNeeded(jValue, hasChildren);
                }

                @SuppressWarnings("UnstableApiUsage")
                @Override
                public void setPresentation(@Nullable final Icon icon, @NonNls @Nullable final String type, @NonNls @NotNull final String separator, @NonNls @Nullable final String value, final boolean hasChildren) {
                    this.decreaseCounterIfNeeded(jValue, hasChildren);
                }

                @Override
                public void setFullValueEvaluator(@NotNull final XFullValueEvaluator fullValueEvaluator) {
                    // nop
                }


                private void decreaseCounterIfNeeded(final JavaValue value, final boolean hasChildren) {
                    try {
                        final var calculatedValue = value.getDescriptor().calcValue(value.getEvaluationContext());
                        if (calculatedValue instanceof ObjectReferenceImpl) {
                            final ObjectReferenceImpl obRef = (ObjectReferenceImpl) calculatedValue;
                            final int fieldSize = obRef.referenceType().allFields().size();
                            if ((fieldSize == 0 || !hasChildren) && this.isNotArray(obRef)) {
                                NodeDebugVisualizer.this.lock.decreaseCounter();
                            }
                        }
                    } catch (final EvaluateException e) {
                        NodeDebugVisualizer.LOGGER.error(e);
                        throw new EvaluateRuntimeException(e);
                    }
                }

                private boolean isNotArray(final ObjectReferenceImpl obRef) {
                    return !(obRef instanceof ArrayReferenceImpl);
                }
            }, XValuePlace.TREE);
        }
    }

    private Pair<ODObject, String> addObjectAndLinksToDiagram(
            final long objectId,
            final JavaValue jValue,
            final String variableName,
            final String typeName) {
        // Skip lists and sets. They will be unfolded. Remember the original link type.
        if (SharedState.SKIP_COLLECTION_VISUALIZATION
                && (typeName.endsWith("Set") || typeName.endsWith("List"))
                && this.parent != null) {
            return Pair.create(this.parent, this.getLinkType(jValue));
        }
        // Normal objects
        final var object = new ODObject(objectId, typeName, variableName);
        this.debuggingInfoCollector.addObject(object);
        if (this.parent != null) {
            this.debuggingInfoCollector.addLinkToObject(this.parent, object, this.getLinkType(jValue));
        }
        return Pair.create(object, "");
    }

    private static long getObjectId(final JavaValue jValue) {
        try {
            final ObjectReferenceImpl value = (ObjectReferenceImpl) jValue.getDescriptor().calcValue(jValue.getEvaluationContext());
            return value.uniqueID();
        } catch (final EvaluateException e) {
            LOGGER.error(e);
            throw new EvaluateRuntimeException(e);
        }
    }

    private String getLinkType(final JavaValue value) {
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

    private static String getBoxedPrimitiveValue(final JavaValue value) {
        try {
            final ObjectReferenceImpl value1 = (ObjectReferenceImpl) value.getDescriptor().calcValue(value.getEvaluationContext());
            @SuppressWarnings("OptionalGetWithoutIsPresent") final var valueField = value1.referenceType().allFields().stream()
                                                                                          .filter(field -> "value".equals(field.name()))
                                                                                          .findFirst()
                                                                                          .get(); // Should always have a "value" field.
            return value1.getValue(valueField).toString();
        } catch (final EvaluateException e) {
            LOGGER.error(e);
            throw new EvaluateRuntimeException(e);
        }
    }

    private static String getNonBoxedPrimitiveValue(final JavaValue value) {
        try {
            return value.getDescriptor().calcValue(value.getEvaluationContext()).toString();
        } catch (final EvaluateException e) {
            LOGGER.error(e);
            throw new EvaluateRuntimeException(e);
        }
    }

    private static Optional<String> getTypeIfExists(final JavaValue value) {
        try {
            final var calculatedValue = value.getDescriptor().calcValue(value.getEvaluationContext());
            if (calculatedValue == null) {
                return Optional.empty();
            }
            return Optional.of(calculatedValue.type().name());
        } catch (final EvaluateException e) {
            LOGGER.error(e);
            throw new EvaluateRuntimeException(e);
        }
    }

    @Override
    public void tooManyChildren(final int remaining) {
        LOGGER.debug("tooManyChildren called!");
    }

    @Override
    public void setAlreadySorted(final boolean alreadySorted) {
        LOGGER.debug("setAlreadySorted called!");
    }

    @Override
    public void setErrorMessage(@NotNull final String errorMessage) {
        LOGGER.warn(errorMessage);
    }

    @Override
    public void setErrorMessage(@NotNull final String errorMessage, @Nullable final XDebuggerTreeNodeHyperlink link) {
        LOGGER.warn(errorMessage);
    }

    @Override
    public void setMessage(
            @NotNull final String message,
            @Nullable final Icon icon,
            @NotNull final SimpleTextAttributes attributes,
            @Nullable final XDebuggerTreeNodeHyperlink link) {
        LOGGER.debug(message);
    }
}