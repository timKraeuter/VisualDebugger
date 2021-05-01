package no.hvl.tk.visualDebugger.debugging;

import com.intellij.debugger.engine.JavaValue;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.xdebugger.frame.XCompositeNode;
import com.intellij.xdebugger.frame.XDebuggerTreeNodeHyperlink;
import com.intellij.xdebugger.frame.XValueChildrenList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class NodeChildCollector implements XCompositeNode {

    private XValueChildrenList children;

    public static XValueChildrenList getChildren(JavaValue value) {
        NodeChildCollector collector = new NodeChildCollector();
        value.computeChildren(collector);
        return collector.getChildrenIfExists();
    }
    
    @Override
    public void addChildren(@NotNull XValueChildrenList children, boolean last) {
        this.children = children;
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

    public XValueChildrenList getChildrenIfExists() {
        return children;
    }
}
