package no.hvl.tk.visual.debugger.debugging.visualization.cef

import no.hvl.tk.visual.debugger.ui.VisualDebuggerNotifications.notifyDownloadStarted
import org.cef.browser.CefBrowser
import org.cef.callback.CefBeforeDownloadCallback
import org.cef.callback.CefDownloadItem
import org.cef.handler.CefDownloadHandlerAdapter

class SimpleDownloadHandler : CefDownloadHandlerAdapter() {
  override fun onBeforeDownload(
      browser: CefBrowser,
      downloadItem: CefDownloadItem,
      suggestedName: String,
      callback: CefBeforeDownloadCallback
  ) {
    notifyDownloadStarted(suggestedName)
    callback.Continue("", true)
  }
}
