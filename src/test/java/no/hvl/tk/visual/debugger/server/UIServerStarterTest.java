package no.hvl.tk.visual.debugger.server;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.Session;
import java.io.IOException;
import java.net.URI;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClientBuilder;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.tyrus.client.ClientManager;
import org.glassfish.tyrus.server.Server;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class ServerStarterTest {

  // Works but test is broken due to class
  // com.intellij.util.lang.ZipResourceFile$MyUrlConnection cannot be cast to class
  // java.net.JarURLConnection (com.intellij.util.lang.ZipResourceFile$MyUrlConnection is in
  // unnamed module of loader 'app'; java.net.JarURLConnection is in module java.base of
  // loader 'bootstrap')
  @Disabled
  @Test
  void startUIServerTest() throws IOException {
    HttpServer httpServer = null;
    try {
      httpServer = UIServerStarter.runNewServer();

      HttpGet request = new HttpGet(ServerConstants.UI_SERVER_URL);

      CloseableHttpResponse response = HttpClientBuilder.create().build().execute(request);

      assertThat(response.getStatusLine().getStatusCode(), is(HttpStatus.SC_OK));
      String mimeType = ContentType.getOrDefault(response.getEntity()).getMimeType();
      assertThat(mimeType, is("text/html"));
    } finally {
      if (httpServer != null) {
        httpServer.shutdownNow();
      }
    }
  }

  @Test
  void startAPIServerTest() throws Exception {
    final String API_SERVER_URL =
        String.format(
            "ws://%s:%s/debug",
            ServerConstants.HOST_NAME, ServerConstants.VISUAL_DEBUGGING_API_SERVER_PORT);
    Server websocketServer = null;
    try {
      websocketServer = VisualDebuggingAPIServerStarter.runNewServer();

      final ClientManager websocketClient = ClientManager.createClient();
      final Session session =
          websocketClient.connectToServer(MockWebsocketClient.class, new URI(API_SERVER_URL));

      assertNotNull(session);
    } finally {
      if (websocketServer != null) {
        websocketServer.stop();
      }
    }
  }

  @ClientEndpoint
  static class MockWebsocketClient {}
}
