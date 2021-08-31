package no.hvl.tk.visual.debugger.actions.settings;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import no.hvl.tk.visual.debugger.settings.VisualDebuggerSettingsConfigurable;
import org.jetbrains.annotations.NotNull;

public class SettingsAction extends AnAction {


    @Override
    public void actionPerformed(@NotNull final AnActionEvent e) {
        final Project project = e.getData(CommonDataKeys.PROJECT);
        ShowSettingsUtil.getInstance().showSettingsDialog(project, VisualDebuggerSettingsConfigurable.class);
    }
}
