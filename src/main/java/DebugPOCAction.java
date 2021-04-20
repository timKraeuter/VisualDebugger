import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebugSessionListener;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.frame.XStackFrame;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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