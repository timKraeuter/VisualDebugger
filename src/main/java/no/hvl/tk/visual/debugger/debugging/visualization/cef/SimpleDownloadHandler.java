package no.hvl.tk.visual.debugger.debugging.visualization.cef;

import no.hvl.tk.visual.debugger.ui.VisualDebuggerNotifications;
import org.cef.browser.CefBrowser;
import org.cef.callback.CefBeforeDownloadCallback;
import org.cef.callback.CefDownloadItem;
import org.cef.callback.CefDownloadItemCallback;
import org.cef.handler.CefDownloadHandler;

public class SimpleDownloadHandler implements CefDownloadHandler {

  @Override
  public void onBeforeDownload(
      CefBrowser browser,
      CefDownloadItem downloadItem,
      String suggestedName,
      CefBeforeDownloadCallback callback) {
    VisualDebuggerNotifications.notifyDownloadStarted(suggestedName);
    callback.Continue("", true);
  }

  @Override
  public void onDownloadUpdated(
      CefBrowser browser, CefDownloadItem downloadItem, CefDownloadItemCallback callback) {
    // NO OP
  }
}
