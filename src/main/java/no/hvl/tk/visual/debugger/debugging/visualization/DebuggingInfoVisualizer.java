package no.hvl.tk.visual.debugger.debugging.visualization;

import com.intellij.debugger.engine.JavaValue;
import com.intellij.openapi.util.Pair;
import no.hvl.tk.visual.debugger.domain.ODObject;

public interface DebuggingInfoVisualizer {

    DebuggingInfoVisualizer addObjectAndCorrespondingDebugNode(ODObject object, JavaValue jValue);

    Pair<ODObject, JavaValue> getDebugNodeAndObjectForObjectId(String objectId);

    DebuggingInfoVisualizer addAttributeToObject(ODObject object, String fieldName, String fieldValue, String fieldType);

    DebuggingInfoVisualizer addLinkToObject(ODObject from, ODObject to, String linkType);

    void addPrimitiveRootValue(String variableName, String type, String value);

    void finishVisualization();
}
