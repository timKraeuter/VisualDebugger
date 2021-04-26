import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebugSessionListener;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.frame.*;
import com.intellij.xdebugger.frame.presentation.XValuePresentation;
import com.intellij.xdebugger.impl.ui.XValueTextProvider;
import com.intellij.xdebugger.impl.ui.tree.nodes.XValueTextRendererBase;
import org.jetbrains.annotations.NonNls;
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
                            for (int i = 0; i < children.size(); i++) {
                                XNamedValue value = (XNamedValue) children.getValue(i);
                                value.computeChildren(new XCompositeNode() {
                                    @Override
                                    public void addChildren(@NotNull XValueChildrenList children, boolean last) {
                                        // We need Type, fields and associations
                                        for (int i = 0; i < children.size(); i++) {
                                            XNamedValue value2 = (XNamedValue) children.getValue(i);
                                            value2.computePresentation(new XValueNode() {
                                                @Override
                                                public void setPresentation(@Nullable final Icon icon, @NonNls @Nullable final String type, @NonNls @NotNull final String value, final boolean hasChildren) {
                                                    System.out.println("Type:" + type);
                                                    System.out.println("Value:" + value);
                                                }

                                                @Override
                                                public void setPresentation(@Nullable final Icon icon, @NotNull final XValuePresentation presentation, final boolean hasChildren) {
                                                    System.out.println("Type: " + presentation.getType());
                                                    presentation.renderValue(new XValueTextRendererBase() {
                                                        @Override
                                                        protected void renderRawValue(@NotNull @NlsSafe final String value, @NotNull final TextAttributesKey key) {
                                                            System.out.println("Rendered value::" + value);
                                                        }

                                                        @Override
                                                        public void renderValue(@NotNull @NlsSafe final String value) {
                                                            System.out.println("Rendered value::" + value);
                                                        }

                                                        @Override
                                                        public void renderStringValue(@NotNull @NlsSafe final String value, @Nullable @NlsSafe final String additionalSpecialCharsToHighlight, final int maxLength) {
                                                            System.out.println("Rendered value::" + value);
                                                        }

                                                        @Override
                                                        public void renderComment(@NotNull @NlsSafe final String comment) {
                                                            System.out.println("Rendered comment::" + comment);
                                                        }

                                                        @Override
                                                        public void renderSpecialSymbol(@NotNull @NlsSafe final String symbol) {
                                                            System.out.println("Rendered symbol::" + symbol);
                                                        }

                                                        @Override
                                                        public void renderError(@NotNull @NlsSafe final String error) {
                                                            System.out.println("Rendered error:" + error);
                                                        }
                                                    });
                                                }

                                                @SuppressWarnings("UnstableApiUsage")
                                                @Override
                                                public void setPresentation(@Nullable final Icon icon, @NonNls @Nullable final String type, @NonNls @NotNull final String separator, @NonNls @Nullable final String value, final boolean hasChildren) {
                                                    System.out.println("Type:" + type);
                                                    System.out.println("Value:" + value);
                                                }

                                                @Override
                                                public void setFullValueEvaluator(@NotNull final XFullValueEvaluator fullValueEvaluator) {
                                                    System.out.println("Value:" + value);
                                                }
                                            }, XValuePlace.TREE);
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
                                });
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