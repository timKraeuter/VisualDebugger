package no.hvl.tk.visualDebugger;

import com.intellij.debugger.engine.JavaValue;
import com.intellij.debugger.engine.evaluation.EvaluateException;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebugSessionListener;
import com.intellij.xdebugger.frame.XCompositeNode;
import com.intellij.xdebugger.frame.XDebuggerTreeNodeHyperlink;
import com.intellij.xdebugger.frame.XStackFrame;
import com.intellij.xdebugger.frame.XValueChildrenList;
import com.jetbrains.jdi.ObjectReferenceImpl;
import com.sun.jdi.Field;
import com.sun.jdi.Value;
import no.hvl.tk.visualDebugger.domain.PrimitiveTypes;
import no.hvl.tk.visualDebugger.visualization.DebuggingVisualizer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Objects;

public class DebugListener implements XDebugSessionListener {
    private static final Logger LOGGER = Logger.getInstance(DebugListener.class);

    public static final int default_debugging_depth = 3;

    private XDebugSession debugSession;
    private int depth;
    private DebuggingVisualizer debuggingVisualizer;

    public DebugListener(final XDebugSession debugSession, final DebuggingVisualizer debuggingVisualizer) {
        this(debugSession, debuggingVisualizer, default_debugging_depth);
    }

    public DebugListener(
            final XDebugSession debugSession,
            final DebuggingVisualizer debuggingVisualizer,
            final int depth) {
        if (depth < 0) {
            throw new IllegalArgumentException("Debugging depth cannot be negative.");
        }
        Objects.requireNonNull(debugSession, "Debug session must not be null.");
        this.debugSession = debugSession;
        this.debuggingVisualizer = debuggingVisualizer;
        this.depth = depth;
    }

    @Override
    public void sessionPaused() {
        LOGGER.debug("Next step in debugger!");

        final XStackFrame currentStackFrame = debugSession.getCurrentStackFrame();
        Objects.requireNonNull(currentStackFrame, "Stack frame unexpectedly was null.");

        currentStackFrame.computeChildren(new MyXCompositeNode(this.debuggingVisualizer, this.depth));
    }

    private String getType(final JavaValue value) {
        if (value.getDescriptor().isValueReady()) {
            return value.getDescriptor().getValue().type().name();
        }
        try {
            return value.getDescriptor().calcValue(value.getEvaluationContext()).type().name();
        } catch (EvaluateException e) {
            LOGGER.error(e);
            throw new RuntimeException(e);
        }
    }

    private class MyXCompositeNode implements XCompositeNode {
        private DebuggingVisualizer debuggingVisualizer;
        private int depth;

        public MyXCompositeNode(
                final DebuggingVisualizer debuggingVisualizer,
                final int depth) {
            this.debuggingVisualizer = debuggingVisualizer;
            this.depth = depth;
        }

        @Override
        public void addChildren(@NotNull XValueChildrenList children, boolean last) {
            for (int i = 0; i < children.size(); i++) {
                JavaValue value = (JavaValue) children.getValue(i);
                String typeName = getType(value);
                // Value and type finished?
                // Get Variable/Object name now.
                System.out.println("Type name: " + typeName);
                if (PrimitiveTypes.isNonBoxedPrimitiveType(typeName)) {
                    System.out.println("Value: " + getNonBoxedPrimitiveValue(value));
                }
                if (PrimitiveTypes.isBoxedPrimitiveType(typeName)) {
                    System.out.println("Value unboxed: " + getBoxedPrimitiveValue(value));
                }
                if (depth == 0) {
                    return;
                }
            }
        }

        private String getBoxedPrimitiveValue(JavaValue value) {
            try {
                final ObjectReferenceImpl value1 = (ObjectReferenceImpl) value.getDescriptor().calcValue(value.getEvaluationContext());
                final Field valueField = value1.referenceType().allFields().stream()
                                               .filter(field -> "value".equals(field.name()))
                                               .findFirst()
                                               .get(); // Should always have a "value" field.
                final Value aValue = value1.getValue(valueField);
                return aValue.toString();
            } catch (EvaluateException e) {
                LOGGER.error(e);
                throw new RuntimeException(e);
            }
        }

        private String getNonBoxedPrimitiveValue(final JavaValue value) {
            if (value.getDescriptor().isValueReady()) {
                return value.getDescriptor().getValue().toString();
            }
            try {
                return value.getDescriptor().calcValue(value.getEvaluationContext()).toString();
            } catch (EvaluateException e) {
                LOGGER.error(e);
                throw new RuntimeException(e);
            }
        }

        @Override
        public void tooManyChildren(int remaining) {
        }

        @Override
        public void setAlreadySorted(boolean alreadySorted) {
        }

        @Override
        public void setErrorMessage(@NotNull String errorMessage) {
        }

        @Override
        public void setErrorMessage(@NotNull String errorMessage, @Nullable XDebuggerTreeNodeHyperlink link) {
        }

        @Override
        public void setMessage(@NotNull String message, @Nullable Icon icon, @NotNull SimpleTextAttributes attributes, @Nullable XDebuggerTreeNodeHyperlink link) {
        }
    }
}
