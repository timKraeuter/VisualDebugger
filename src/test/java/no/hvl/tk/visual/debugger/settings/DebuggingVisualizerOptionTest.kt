package no.hvl.tk.visual.debugger.settings

import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.jupiter.api.Test

internal class DebuggingVisualizerOptionTest {
  @Test
  fun testToString() {
    MatcherAssert.assertThat(
        DebuggingVisualizerOption.EMBEDDED.toString(),
        CoreMatchers.`is`("Embedded visualizer (no interaction)"))
    MatcherAssert.assertThat(
        DebuggingVisualizerOption.WEB_UI.toString(), CoreMatchers.`is`("Browser visualizer"))
  }
}
