package no.hvl.tk.visualDebugger.debugging;

import no.hvl.tk.visualDebugger.debugging.visualization.DebuggingVisualizer;
import no.hvl.tk.visualDebugger.domain.ODObject;

public class DebuggingVisualizerMock implements DebuggingVisualizer {
    public DebuggingVisualizerMock() {
    }

    @Override
    public DebuggingVisualizer addObject(ODObject object) {
        return this;
    }

    @Override
    public DebuggingVisualizer addAttributeToObject(ODObject object, final String fieldName, final String fieldValue) {
        return this;
    }

    @Override
    public DebuggingVisualizer addLinkToObject(ODObject from, ODObject to, String linkType) {
        return null;
    }

    @Override
    public void finishVisualization() {
    }

    @Override
    public void addPrimitiveRootValue(final String variableName, final String type, final String value) {
    }
}
