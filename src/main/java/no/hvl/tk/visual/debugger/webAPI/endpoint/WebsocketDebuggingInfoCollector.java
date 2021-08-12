package no.hvl.tk.visual.debugger.webAPI.endpoint;

import com.intellij.debugger.engine.JavaValue;
import com.intellij.openapi.util.Pair;
import no.hvl.tk.visual.debugger.debugging.visualization.DebuggingInfoVisualizer;
import no.hvl.tk.visual.debugger.debugging.visualization.DebuggingInfoVisualizerBase;
import no.hvl.tk.visual.debugger.domain.ODObject;
import no.hvl.tk.visual.debugger.domain.ObjectDiagram;

public class WebsocketDebuggingInfoCollector extends DebuggingInfoVisualizerBase {

    /**
     * Currently active visualizer (needs updates for debug nodes and ids).
     */
    private final DebuggingInfoVisualizer debuggingInfoVisualizer;

    public WebsocketDebuggingInfoCollector(final DebuggingInfoVisualizer debuggingInfoVisualizer) {
        this.debuggingInfoVisualizer = debuggingInfoVisualizer;
    }

    @Override
    protected void handleObjectAndJavaValue(final ODObject id, final JavaValue jValue) {
        this.debuggingInfoVisualizer.addObjectAndCorrespondingDebugNode(id, jValue);
    }

    @Override
    public Pair<ODObject, JavaValue> getDebugNodeAndObjectForObjectId(final String objectId) {
        // not needed should never be called.
        return null;
    }

    @Override
    public void finishVisualization() {
        // not needed should never be called.
    }

    public ObjectDiagram getCurrentDiagram() {
        return this.diagram;
    }
}
