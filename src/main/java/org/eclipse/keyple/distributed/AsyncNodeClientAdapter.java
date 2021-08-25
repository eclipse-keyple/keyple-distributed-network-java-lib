/* **************************************************************************************
 * Copyright (c) 2020 Calypso Networks Association https://calypsonet.org/
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
import org.eclipse.keyple.distributed.spi.AsyncEndpointClientSpi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * (package-private)<br>
 * Async Node Client implementation.
 *
 * @since 2.0.0
 */
final class AsyncNodeClientAdapter extends AbstractNodeAdapter implements AsyncNodeClient {

  private static final Logger logger = LoggerFactory.getLogger(AsyncNodeClientAdapter.class);
  private static final String SESSION_ID = "sessionId";

  private final AsyncEndpointClientSpi endpoint;
  private final Map<String, SessionManager> sessionManagers;

  /**
   * (package-private)<br>
   *
   * @param handler The associated handler.
   * @param endpoint The user client async endpoint.
   * @param timeoutSeconds The default timeout (in seconds) to use.
   * @since 2.0.0
   */
  AsyncNodeClientAdapter(
      AbstractMessageHandlerAdapter handler, AsyncEndpointClientSpi endpoint, int timeoutSeconds) {
    super(handler, timeoutSeconds);
    this.endpoint = endpoint;
    this.sessionManagers = new ConcurrentHashMap<String, SessionManager>();
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0.0
   */
  @Override
  void openSession(String sessionId) {
    SessionManager manager = new SessionManager(sessionId);
    sessionManagers.put(sessionId, manager);
    manager.openSession();
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0.0
   */
  @Override
  public void onOpen(String sessionId) {
    Assert.getInstance().notEmpty(sessionId, SESSION_ID);
    SessionManager manager = getManagerForEndpoint(sessionId);
    if (manager != null) {
      manager.onOpen();
    }
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0.0
   */
  @Override
  MessageDto sendRequest(MessageDto message) {
    message.setClientNodeId(getNodeId());
    SessionManager manager = sessionManagers.get(message.getSessionId());
    return manager.sendRequest(message);
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0.0
   */
  @Override
  void sendMessage(MessageDto message) {
    message.setClientNodeId(getNodeId());
    SessionManager manager = sessionManagers.get(message.getSessionId());
    manager.sendMessage(message);
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0.0
   */
  @Override
  public void onMessage(MessageDto message) {

    Assert.getInstance()
        .notNull(message, "message")
        .notEmpty(message.getSessionId(), SESSION_ID)
        .notEmpty(message.getAction(), "action")
        .notEmpty(message.getClientNodeId(), "clientNodeId")
        .notEmpty(message.getServerNodeId(), "serverNodeId");

    SessionManager manager = getManagerForEndpoint(message.getSessionId());
    if (manager != null) {
      MessageDto.Action action = MessageDto.Action.valueOf(message.getAction());
      switch (action) {
        case PLUGIN_EVENT:
        case READER_EVENT:
          manager.onEvent(message);
          break;
        default:
          manager.onResponse(message);
      }
    }
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0.0
   */
  @Override
  void closeSession(String sessionId) {
    SessionManager manager = sessionManagers.get(sessionId);
    try {
      manager.closeSession();
    } finally {
      sessionManagers.remove(sessionId);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0.0
   */
  @Override
  public void onClose(String sessionId) {
    Assert.getInstance().notEmpty(sessionId, SESSION_ID);
    SessionManager manager = getManagerForEndpoint(sessionId);
    if (manager != null) {
      manager.onClose();
    }
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0.0
   */
  @Override
  public void onError(String sessionId, Throwable error) {
    Assert.getInstance().notEmpty(sessionId, SESSION_ID).notNull(error, "error");
    SessionManager manager = getManagerForEndpoint(sessionId);
    if (manager != null) {
      manager.onError(error);
    }
  }

  /**
   * (private)<br>
   * Get the manager associated to the provided session id and log a session not found message if
   * manager does not exists.
   *
   * @param sessionId The session id (must be not empty).
   * @return a nullable reference.
   */
  private SessionManager getManagerForEndpoint(String sessionId) {
    SessionManager manager = sessionManagers.get(sessionId);
    if (manager == null) {
      logger.warn(
          "The node's session '{}' is not found. It was maybe closed due to a timeout.", sessionId);
    }
    return manager;
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
     * @since 2.0.0
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
     * Invoked by the handler to open the session by calling the endpoint and awaiting the result.
     */
    private synchronized void openSession() {
      state = SessionManagerState.OPEN_SESSION_BEGIN;
      endpoint.openSession(sessionId);
      waitForState(SessionManagerState.OPEN_SESSION_END);
    }

    /**
     * (private)<br>
     * Invoked by the endpoint and notify the awaiting thread if necessary.
     *
     * @throws IllegalStateException in case of bad use.
     */
    private synchronized void onOpen() {
      checkState(SessionManagerState.OPEN_SESSION_BEGIN);
      state = SessionManagerState.OPEN_SESSION_END;
      notifyAll();
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
     * Invoked by the endpoint and notify the awaiting thread if necessary.
     *
     * @param message The response received from the endpoint.
     * @throws IllegalStateException in case of bad use.
     */
    private synchronized void onResponse(MessageDto message) {
      checkState(SessionManagerState.SEND_REQUEST_BEGIN);
      response = message;
      state = SessionManagerState.SEND_REQUEST_END;
      notifyAll();
    }

    /**
     * (private)<br>
     * Invoked by the endpoint to transmit an event to the handler.
     *
     * @param message The event received from the endpoint.
     */
    private void onEvent(MessageDto message) {
      getHandler().onMessage(message);
    }

    /**
     * (private)<br>
     * Invoked by the handler to send a message to the endpoint.
     *
     * @param message The message to send.
     * @throws RuntimeException if an error occurs.
     */
    private synchronized void sendMessage(MessageDto message) {
      checkIfExternalErrorOccurred();
      state = SessionManagerState.SEND_MESSAGE;
      endpoint.sendMessage(message);
      checkIfExternalErrorOccurred();
    }

    /**
     * (private)<br>
     * Invoked by the handler or by the node to close the current session by calling the endpoint
     * and awaiting the result.
     */
    private synchronized void closeSession() {
      checkIfExternalErrorOccurred();
      state = SessionManagerState.CLOSE_SESSION_BEGIN;
      endpoint.closeSession(sessionId);
      waitForState(SessionManagerState.CLOSE_SESSION_END);
    }

    /**
     * (private)<br>
     * Invoked by the endpoint and notify the awaiting thread if necessary.<br>
     *
     * @throws IllegalStateException in case of bad use.
     */
    private synchronized void onClose() {
      checkState(SessionManagerState.CLOSE_SESSION_BEGIN);
      state = SessionManagerState.CLOSE_SESSION_END;
      notifyAll();
    }

    /**
     * (private)<br>
     * Invoked by the endpoint in case of endpoint error and notify the awaiting thread if
     * necessary.
     *
     * @throws IllegalStateException in case of bad use.
     */
    private synchronized void onError(Throwable e) {
      checkState(
          SessionManagerState.OPEN_SESSION_BEGIN,
          SessionManagerState.SEND_REQUEST_BEGIN,
          SessionManagerState.SEND_MESSAGE,
          SessionManagerState.CLOSE_SESSION_BEGIN);
      error = e;
      state = SessionManagerState.EXTERNAL_ERROR_OCCURRED;
      notifyAll();
    }
  }
}
