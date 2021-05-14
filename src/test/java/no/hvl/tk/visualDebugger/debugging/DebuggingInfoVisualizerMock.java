package no.hvl.tk.visualDebugger.debugging;

import no.hvl.tk.visualDebugger.debugging.visualization.DebuggingInfoVisualizer;
import no.hvl.tk.visualDebugger.domain.ODObject;

public class DebuggingInfoVisualizerMock implements DebuggingInfoVisualizer {
    public DebuggingInfoVisualizerMock() {
    }

    @Override
    public DebuggingInfoVisualizer addObject(ODObject object) {
        return this;
    }

    @Override
    public DebuggingInfoVisualizer addAttributeToObject(ODObject object, String fieldName, String fieldValue, String fieldType) {
        return this;
    }

    @Override
    public DebuggingInfoVisualizer addLinkToObject(ODObject from, ODObject to, String linkType) {
        return null;
    }

    @Override
    public void finishVisualization() {
    }

    @Override
    public void addPrimitiveRootValue(final String variableName, final String type, final String value) {
    }
}
