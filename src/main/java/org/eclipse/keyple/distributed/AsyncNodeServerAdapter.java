/* **************************************************************************************
 * Copyright (c) 2020 Calypso Networks Association https://www.calypsonet-asso.org/
 *
 * See the NOTICE file(s) distributed with this work for additional information
 * regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 ************************************************************************************** */
package org.eclipse.keyple.distributed;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.keyple.core.util.Assert;
import org.eclipse.keyple.distributed.spi.AsyncEndpointServerSpi;

/**
 * (package-private)<br>
 * Async Node Server implementation.
 *
 * @since 2.0
 */
final class AsyncNodeServerAdapter extends AbstractNodeAdapter implements AsyncNodeServer {

  private static final String SESSION_ID = "sessionId";

  private final AsyncEndpointServerSpi endpoint;
  private final Map<String, SessionManager> sessionManagers;

  /**
   * (package-private)<br>
   *
   * @param handler The associated handler.
   * @param endpoint The user server async endpoint.
   * @param timeoutSeconds The default timeout (in seconds) to use.
   * @since 2.0
   */
  AsyncNodeServerAdapter(
      AbstractMessageHandlerAdapter handler, AsyncEndpointServerSpi endpoint, int timeoutSeconds) {
    super(handler, timeoutSeconds);
    this.endpoint = endpoint;
    this.sessionManagers = new ConcurrentHashMap<String, SessionManager>();
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0
   */
  @Override
  void openSession(String sessionId) {
    throw new UnsupportedOperationException("openSession");
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0
   */
  @Override
  MessageDto sendRequest(MessageDto message) {
    message.setServerNodeId(getNodeId());
    SessionManager manager = getManagerForHandler(message.getSessionId());
    return manager.sendRequest(message);
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0
   */
  @Override
  void sendMessage(MessageDto message) {
    message.setServerNodeId(getNodeId());
    SessionManager manager = getManagerForHandler(message.getSessionId());
    manager.sendMessage(message);
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0
   */
  @Override
  void closeSession(String sessionId) {
    throw new UnsupportedOperationException("closeSession");
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0
   */
  @Override
  void startPluginsObservation() {
    throw new UnsupportedOperationException("startPluginsObservation");
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0
   */
  @Override
  void stopPluginsObservation() {
    throw new UnsupportedOperationException("stopPluginsObservation");
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0
   */
  @Override
  void startReadersObservation() {
    throw new UnsupportedOperationException("startReadersObservation");
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0
   */
  @Override
  void stopReadersObservation() {
    throw new UnsupportedOperationException("stopReadersObservation");
  }

  /**
   * (private)<br>
   * Check if the session is active and get the associated session manager.
   *
   * @param sessionId The session id (must be not empty).
   * @return a not null reference.
   * @throws IllegalStateException if the session is not found.
   */
  private SessionManager getManagerForHandler(String sessionId) {
    SessionManager manager = sessionManagers.get(sessionId);
    if (manager == null) {
      throw new IllegalStateException(
          String.format("The node's session '%s' is closed.", sessionId));
    }
    return manager;
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0
   */
  @Override
  public void onMessage(MessageDto message) {

    Assert.getInstance()
        .notNull(message, "message")
        .notEmpty(message.getSessionId(), SESSION_ID)
        .notEmpty(message.getAction(), "action")
        .notEmpty(message.getClientNodeId(), "clientNodeId");

    // Get or create a new session manager
    SessionManager manager = sessionManagers.get(message.getSessionId());
    if (manager == null) {
      manager = new SessionManager(message.getSessionId());
      sessionManagers.put(message.getSessionId(), manager);
    }
    manager.onMessage(message);
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0
   */
  @Override
  public void onError(String sessionId, Throwable error) {
    Assert.getInstance().notEmpty(sessionId, SESSION_ID).notNull(error, "error");
    SessionManager manager = sessionManagers.get(sessionId);
    Assert.getInstance().notNull(manager, SESSION_ID);
    manager.onError(error);
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0
   */
  @Override
  public void onClose(String sessionId) {
    Assert.getInstance().notEmpty(sessionId, SESSION_ID);
    SessionManager manager = sessionManagers.remove(sessionId);
    Assert.getInstance().notNull(manager, SESSION_ID);
  }

  /**
   * (private)<br>
   * The inner session manager class.<br>
   * There is one manager by session id.
   */
  private class SessionManager extends AbstractSessionManager {

    /**
     * (private)<br>
     * Constructor
     *
     * @param sessionId The session id to manage.
     */
    private SessionManager(String sessionId) {
      super(sessionId);
    }

    /**
     * {@inheritDoc}
     *
     * @since 2.0
     */
    @Override
    void checkIfExternalErrorOccurred() {
      if (state == SessionManagerState.EXTERNAL_ERROR_OCCURRED) {
        state = SessionManagerState.ABORTED_SESSION;
        throw new NodeCommunicationException(error.getMessage(), error);
      }
    }

    /**
     * (private)<br>
     * Invoked by the endpoint and notify the awaiting thread if necessary.
     *
     * @param message The message received from the endpoint.
     * @throws IllegalStateException in case of bad use.
     */
    private synchronized void onMessage(MessageDto message) {
      checkState(
          SessionManagerState.INITIALIZED,
          SessionManagerState.ON_MESSAGE,
          SessionManagerState.SEND_REQUEST_BEGIN,
          SessionManagerState.SEND_REQUEST_END,
          SessionManagerState.SEND_MESSAGE);
      if (state == SessionManagerState.SEND_REQUEST_BEGIN) {
        response = message;
        state = SessionManagerState.SEND_REQUEST_END;
        notifyAll();
      } else {
        state = SessionManagerState.ON_MESSAGE;
        getHandler().onMessage(message);
      }
    }

    /**
     * (private)<br>
     * Invoked by the handler to send a request to the endpoint and await a response.
     *
     * @param message The message to send.
     * @return The response.
     */
    private synchronized MessageDto sendRequest(MessageDto message) {
      checkIfExternalErrorOccurred();
      state = SessionManagerState.SEND_REQUEST_BEGIN;
      response = null;
      endpoint.sendMessage(message);
      waitForState(SessionManagerState.SEND_REQUEST_END);
      return response;
    }

    /**
     * (private)<br>
     * Invoked by the handler to send a message to the endpoint.
     *
     * @param message The message to send.
     */
    private synchronized void sendMessage(MessageDto message) {
      checkIfExternalErrorOccurred();
      state = SessionManagerState.SEND_MESSAGE;
      endpoint.sendMessage(message);
      checkIfExternalErrorOccurred();
    }

    /**
     * (private)<br>
     * Invoked by the endpoint in case of endpoint error and notify the awaiting thread if
     * necessary.
     *
     * @throws IllegalStateException in case of bad use.
     */
    private synchronized void onError(Throwable e) {
      checkState(SessionManagerState.SEND_REQUEST_BEGIN, SessionManagerState.SEND_MESSAGE);
      error = e;
      state = SessionManagerState.EXTERNAL_ERROR_OCCURRED;
      notifyAll();
    }
  }
}
