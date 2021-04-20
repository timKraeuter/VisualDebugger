import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebugSessionListener;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.frame.XCompositeNode;
import com.intellij.xdebugger.frame.XDebuggerTreeNodeHyperlink;
import com.intellij.xdebugger.frame.XStackFrame;
import com.intellij.xdebugger.frame.XValueChildrenList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Arrays;

public class DebugPOCAction extends AnAction {

    @Override
    public void update(AnActionEvent e) {
        e.getPresentation().setEnabled(isDebuggingInProgress());
    }

    private boolean isDebuggingInProgress() {
        return this.getDebugSessionIfExists() != null;
    }

    @Override
    public void actionPerformed(@NotNull final AnActionEvent e) {
        System.out.println("test123");
        final XDebugSession debugSession = getDebugSessionIfExists();

        if (debugSession != null) {
            debugSession.addSessionListener(new XDebugSessionListener() {
                @Override
                public void sessionPaused() {
                    System.out.println("Next step in debugger!");
                    final XStackFrame currentStackFrame = debugSession.getCurrentStackFrame();
                    currentStackFrame.computeChildren(new XCompositeNode() {
                        @Override
                        public void addChildren(@NotNull XValueChildrenList children, boolean last) {
                            System.out.println(children);
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
                    });
                }
            });
        } else {
            System.out.println("No debugging session active!");
        }
    }

    @Nullable
    private XDebugSession getDebugSessionIfExists() {
        // Atm, I assume there is only one open project.
        final Project project = Arrays.asList(ProjectManager.getInstance().getOpenProjects()).get(0);
        return XDebuggerManager.getInstance(project).getCurrentSession();
    }
}