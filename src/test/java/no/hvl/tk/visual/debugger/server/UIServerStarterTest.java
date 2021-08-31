package no.hvl.tk.visual.debugger.server;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClientBuilder;
import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class UIServerStarterTest {

    @Test
    void startUITest() throws IOException {
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
}
