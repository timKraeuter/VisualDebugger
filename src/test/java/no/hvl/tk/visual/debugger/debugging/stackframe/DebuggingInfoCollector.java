package no.hvl.tk.visual.debugger.debugging.stackframe;

import com.intellij.debugger.engine.JavaValue;
import com.intellij.openapi.util.Pair;
import no.hvl.tk.visual.debugger.debugging.visualization.DebuggingInfoVisualizer;
import no.hvl.tk.visual.debugger.debugging.visualization.DebuggingInfoVisualizerBase;
import no.hvl.tk.visual.debugger.domain.ODObject;
import no.hvl.tk.visual.debugger.domain.ObjectDiagram;

public class DebuggingInfoCollector extends DebuggingInfoVisualizerBase {
    @Override
    public DebuggingInfoVisualizer addDebugNodeForObject(ODObject object, JavaValue jValue) {
        return null;
    }

    @Override
    public Pair<ODObject, JavaValue> getDebugNodeAndObjectForObjectId(String objectId) {
        return null;
    }

    @Override
    public void finishVisualization() {
    }

    @Override
    public void debuggingActivated() {

    }

    @Override
    public void debuggingDeactivated() {

    }

    @Override
    protected void preAddObject() {

    }

    public ObjectDiagram getDiagram() {
        return this.diagram;
    }
}
