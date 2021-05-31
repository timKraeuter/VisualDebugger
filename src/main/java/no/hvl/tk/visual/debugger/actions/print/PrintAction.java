package no.hvl.tk.visual.debugger.actions.print;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class PrintAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull final AnActionEvent e) {
        new PrintDialog().show();
    }
}
