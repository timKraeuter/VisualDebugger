import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebugSessionListener;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.frame.XStackFrame;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class DebugPOCAction extends AnAction {

    @Override
    public void update(AnActionEvent e) {
        // Using the event, evaluate the context, and enable or disable the action.
        System.out.println("update");
    }

    @Override
    public void actionPerformed(@NotNull final AnActionEvent e) {
        System.out.println("test123");
        // I assume atm there is only one open project.
        final Project project = Arrays.asList(ProjectManager.getInstance().getOpenProjects()).get(0);

        final XDebugSession debugSession = XDebuggerManager.getInstance(project).getCurrentSession();

        debugSession.addSessionListener(new XDebugSessionListener() {
            @Override
            public void sessionPaused() {
                System.out.println("Next step in debugger!");
                final XStackFrame currentStackFrame = debugSession.getCurrentStackFrame();
            }
        });
    }
}