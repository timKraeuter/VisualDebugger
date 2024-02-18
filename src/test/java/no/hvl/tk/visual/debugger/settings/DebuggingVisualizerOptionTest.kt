package no.hvl.tk.visual.debugger.settings;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

class DebuggingVisualizerOptionTest {

  @Test
  void testToString() {
    assertThat(
        DebuggingVisualizerOption.EMBEDDED.toString(), is("Embedded visualizer (no interaction)"));
    assertThat(DebuggingVisualizerOption.WEB_UI.toString(), is("Browser visualizer"));
  }
}
