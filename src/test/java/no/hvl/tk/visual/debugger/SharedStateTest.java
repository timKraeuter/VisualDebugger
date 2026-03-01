package no.hvl.tk.visual.debugger;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.websocket.Session;
import java.io.IOException;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class SharedStateTest {

  @AfterEach
  void tearDown() {
    // Reset shared state after each test to avoid leaking between tests.
    SharedState.clearWebsocketClients();
    SharedState.getManuallyExploredObjects().clear();
  }

  // --- WebSocket client management ---

  @Test
  void addAndRemoveWebsocketClient() {
    Session session = mock(Session.class);

    SharedState.addWebsocketClient(session);
    assertThat(SharedState.getWebsocketClients().size(), is(1));
    assertTrue(SharedState.getWebsocketClients().contains(session));

    SharedState.removeWebsocketClient(session);
    assertThat(SharedState.getWebsocketClients().size(), is(0));
  }

  @Test
  void getWebsocketClientsReturnsSnapshot() {
    Session session1 = mock(Session.class);
    Session session2 = mock(Session.class);
    SharedState.addWebsocketClient(session1);

    // Take a snapshot.
    Set<Session> snapshot = SharedState.getWebsocketClients();
    assertThat(snapshot.size(), is(1));

    // Mutate the underlying set after taking the snapshot.
    SharedState.addWebsocketClient(session2);

    // The snapshot must NOT reflect the mutation.
    assertThat(snapshot.size(), is(1));
    assertFalse(snapshot.contains(session2));

    // A new call should reflect the mutation.
    assertThat(SharedState.getWebsocketClients().size(), is(2));
  }

  @Test
  void getWebsocketClientsReturnsUnmodifiableSet() {
    Session session = mock(Session.class);
    SharedState.addWebsocketClient(session);

    Set<Session> snapshot = SharedState.getWebsocketClients();
    assertThrows(UnsupportedOperationException.class, () -> snapshot.add(mock(Session.class)));
    assertThrows(UnsupportedOperationException.class, snapshot::clear);
  }

  @Test
  void clearWebsocketClientsClosesOpenSessions() throws IOException {
    Session openSession = mock(Session.class);
    when(openSession.isOpen()).thenReturn(true);

    Session closedSession = mock(Session.class);
    when(closedSession.isOpen()).thenReturn(false);

    SharedState.addWebsocketClient(openSession);
    SharedState.addWebsocketClient(closedSession);

    SharedState.clearWebsocketClients();

    verify(openSession).close();
    verify(closedSession, never()).close();
    assertThat(SharedState.getWebsocketClients().size(), is(0));
  }

  @Test
  void clearWebsocketClientsContinuesOnIOException() throws IOException {
    Session failingSession = mock(Session.class);
    when(failingSession.isOpen()).thenReturn(true);
    doThrow(new IOException("test")).when(failingSession).close();

    Session okSession = mock(Session.class);
    when(okSession.isOpen()).thenReturn(true);

    SharedState.addWebsocketClient(failingSession);
    SharedState.addWebsocketClient(okSession);

    // Should not throw even though one session fails to close.
    assertDoesNotThrow(SharedState::clearWebsocketClients);
    assertThat(SharedState.getWebsocketClients().size(), is(0));
  }

  @Test
  void clearWebsocketClientsOnEmptySetIsNoOp() {
    assertDoesNotThrow(SharedState::clearWebsocketClients);
    assertThat(SharedState.getWebsocketClients().size(), is(0));
  }

  // --- Manually explored objects (concurrent set) ---

  @Test
  void manuallyExploredObjectsAddAndClear() {
    Set<String> explored = SharedState.getManuallyExploredObjects();
    explored.add("obj1");
    explored.add("obj2");
    assertThat(explored.size(), is(2));

    explored.clear();
    assertThat(explored.size(), is(0));
  }

  @Test
  void manuallyExploredObjectsDeduplicates() {
    Set<String> explored = SharedState.getManuallyExploredObjects();
    explored.add("obj1");
    explored.add("obj1");
    assertThat(explored.size(), is(1));
  }
}
