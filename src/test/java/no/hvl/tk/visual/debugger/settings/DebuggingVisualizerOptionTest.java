package no.hvl.tk.visual.debugger.settings;

import org.junit.jupiter.api.Test;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
class DebuggingVisualizerOptionTest {

    @Test
    void testToString() {
        assertThat(DebuggingVisualizerOption.EMBEDDED.toString(), is("Embedded visualizer (no interaction)"));
        assertThat(DebuggingVisualizerOption.WEB_UI.toString(), is("Browser visualizer"));
    }
}