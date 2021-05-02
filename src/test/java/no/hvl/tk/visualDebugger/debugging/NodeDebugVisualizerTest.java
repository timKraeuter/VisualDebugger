package no.hvl.tk.visualDebugger.debugging;

import com.intellij.debugger.engine.JavaValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class NodeDebugVisualizerTest {

    private NodeDebugVisualizer nodeDebugVisualizer;

    @BeforeEach
    void setUp() {
        nodeDebugVisualizer = new NodeDebugVisualizer(new DebuggingVisualizerMock(), 0, new CounterBasedLock());
    }

    @Test
    void unboxedValueTest() {
        final JavaValue javaValue = Mockito.mock(JavaValue.class);
        nodeDebugVisualizer.handleValue(javaValue);
    }

    @Test
    void boxedValueTest() {

    }
}
