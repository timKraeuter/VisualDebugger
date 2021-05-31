package no.hvl.tk.visual.debugger.actions.settings;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class SettingsAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull final AnActionEvent e) {
        new SettingsDialog().show();
    }
}
