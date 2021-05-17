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

import java.util.Arrays;
import java.util.Date;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * (package-private)<br>
 * Abstract Node.
 *
 * @since 2.0
 */
abstract class AbstractNodeAdapter {

  private static final Logger logger = LoggerFactory.getLogger(AbstractNodeAdapter.class);

  /**
   * (private)<br>
   * The node ID.
   */
  private final String nodeId;

  /**
   * (private)<br>
   * The associated handler.
   */
  private final AbstractMessageHandlerAdapter handler;

  /**
   * (private)<br>
   * Timeout used during awaiting (in milliseconds)
   */
  private final int timeoutMillis;

  /**
   * (package-private)<br>
   *
   * @param handler The associated handler.
   * @param timeoutSeconds The default timeout (in seconds) to use.
   * @since 2.0
   */
  AbstractNodeAdapter(AbstractMessageHandlerAdapter handler, int timeoutSeconds) {
    this.nodeId = UUID.randomUUID().toString();
    this.handler = handler;
    this.timeoutMillis = timeoutSeconds * 1000;
  }

  /**
   * (package-private)<br>
   * Opens a new session on the endpoint (for internal use only).
   *
   * @param sessionId The session id.
   * @since 2.0
   */
  abstract void openSession(String sessionId);

  /**
   * (package-private)<br>
   * Sends a request and return a response (for internal use only).
   *
   * @param message The message to send.
   * @return null if there is no response.
   * @since 2.0
   */
  abstract MessageDto sendRequest(MessageDto message);

  /**
   * (package-private)<br>
   * Sends a message (for internal use only).
   *
   * @param message The message to send.
   * @since 2.0
   */
  abstract void sendMessage(MessageDto message);

  /**
   * (package-private)<br>
   * Closes the session having the provided session id (for internal use only).
   *
   * @param sessionId The session id.
   * @since 2.0
   */
  abstract void closeSession(String sessionId);

  /**
   * (package-private)<br>
   * Starts the background observation of all remote plugins events using the configured strategy.
   *
   * @since 2.0
   */
  abstract void startPluginsObservation();

  /**
   * (package-private)<br>
   * Stops the background observation of all remote plugins events.
   *
   * @since 2.0
   */
  abstract void stopPluginsObservation();

  /**
   * (package-private)<br>
   * Starts the background observation of all remote readers events using the configured strategy.
   *
   * @since 2.0
   */
  abstract void startReadersObservation();

  /**
   * (package-private)<br>
   * Stops the background observation of all remote readers events.
   *
   * @since 2.0
   */
  abstract void stopReadersObservation();

  /**
   * (package-private)<br>
   * Closes the session silently (without throwing exceptions)
   *
   * @param sessionId The session id.
   * @since 2.0
   */
  final void closeSessionSilently(String sessionId) {
    try {
      closeSession(sessionId);
    } catch (RuntimeException e) {
      logger.error(
          "Error during the silent closing of node's session [{}] : {}",
          sessionId,
          e.getMessage(),
          e);
    }
  }

  /**
   * (package-private)<br>
   * Gets the node ID.
   *
   * @return A not empty string.
   * @since 2.0
   */
  final String getNodeId() {
    return nodeId;
  }

  /**
   * (package-private)<br>
   * Gets the associated handler.
   *
   * @return A not null reference.
   * @since 2.0
   */
  final AbstractMessageHandlerAdapter getHandler() {
    return handler;
  }

  /**
   * (package-private)<br>
   * The session manager state enum.
   *
   * @since 2.0
   */
  enum SessionManagerState {

    /**
     * Manager initialized.
     *
     * @since 2.0
     */
    INITIALIZED,

    /**
     * The open session process has started.
     *
     * @since 2.0
     */
    OPEN_SESSION_BEGIN,

    /**
     * The open session process is complete.
     *
     * @since 2.0
     */
    OPEN_SESSION_END,

    /**
     * An incoming request-type message is processing.
     *
     * @since 2.0
     */
    ON_REQUEST,

    /**
     * An incoming event-type message is processing.
     *
     * @since 2.0
     */
    ON_MESSAGE,

    /**
     * An outgoing request-type message is processing.
     *
     * @since 2.0
     */
    SEND_REQUEST_BEGIN,

    /**
     * An outgoing request-type message is complete.
     *
     * @since 2.0
     */
    SEND_REQUEST_END,

    /**
     * An outgoing event-type message was performed.
     *
     * @since 2.0
     */
    SEND_MESSAGE,

    /**
     * An external error was received.
     *
     * @since 2.0
     */
    EXTERNAL_ERROR_OCCURRED,

    /**
     * The close session process has started.
     *
     * @since 2.0
     */
    CLOSE_SESSION_BEGIN,

    /**
     * The close session process is complete.
     *
     * @since 2.0
     */
    CLOSE_SESSION_END,

    /**
     * The current session is aborted due to an unexpected error.
     *
     * @since 2.0
     */
    ABORTED_SESSION
  }

  /**
   * (package-private)<br>
   * The inner session manager abstract class.<br>
   * There is one manager by session id.
   *
   * @since 2.0
   */
  abstract class AbstractSessionManager {

    /**
     * (package-private)<br>
     *
     * @since 2.0
     */
    final String sessionId;

    /**
     * (package-private)<br>
     *
     * @since 2.0
     */
    volatile SessionManagerState state;

    /**
     * (package-private)<br>
     *
     * @since 2.0
     */
    MessageDto response;

    /**
     * (package-private)<br>
     *
     * @since 2.0
     */
    Throwable error;

    /**
     * (package-private)<br>
     * Constructor
     *
     * @param sessionId The session id to manage.
     * @since 2.0
     */
    AbstractSessionManager(String sessionId) {
      this.sessionId = sessionId;
      this.state = SessionManagerState.INITIALIZED;
      this.response = null;
      this.error = null;
    }

    /**
     * (package-private)<br>
     * Checks if the current state is equal to the target state, else wait until a timeout or to be
     * wake up by another thread.<br>
     *
     * @param targetStates The target states.
     * @since 2.0
     */
    synchronized void waitForState(SessionManagerState... targetStates) {
      for (SessionManagerState targetState : targetStates) {
        if (state == targetState) {
          return;
        }
      }
      checkIfExternalErrorOccurred();
      try {
        long deadline = new Date().getTime() + timeoutMillis;
        while (new Date().getTime() < deadline) {
          wait(timeoutMillis);
          for (SessionManagerState targetState : targetStates) {
            if (state == targetState) {
              return;
            }
          }
          checkIfExternalErrorOccurred();
        }
        timeoutOccurred();
      } catch (InterruptedException e) {
        logger.error(
            "Unexpected interruption of the task associated with the node's session {}",
            sessionId,
            e);
        Thread.currentThread().interrupt();
      }
    }

    /**
     * (package-private)<br>
     * Checks if an external error was received from the endpoint or the handler, regardless to the
     * current state, and then request the cancelling of the session and throws an exception.
     *
     * @throws NodeCommunicationException with the original cause if an error exists.
     * @since 2.0
     */
    abstract void checkIfExternalErrorOccurred();

    /**
     * (package-private)<br>
     * Checks if the current state is one of the provided target states.
     *
     * @param targetStates The target states to test.
     * @throws IllegalStateException if the current state does not match any of the states provided.
     * @since 2.0
     */
    void checkState(SessionManagerState... targetStates) {
      for (SessionManagerState targetState : targetStates) {
        if (state == targetState) {
          return;
        }
      }
      throw new IllegalStateException(
          String.format(
              "The status of the node's session manager '%s' should have been one of %s, but is currently '%s'",
              sessionId, Arrays.toString(targetStates), state));
    }

    /**
     * (package-private)<br>
     * The timeout case : request the cancelling of the session and throws an exception.
     *
     * @throws NodeCommunicationException the thrown exception.
     * @since 2.0
     */
    void timeoutOccurred() {
      state = SessionManagerState.ABORTED_SESSION;
      logger.error(
          "Timeout occurs for the task associated with the node's session '{}'", sessionId);
      throw new NodeCommunicationException(
          String.format(
              "Timeout occurs for the task associated with the node's session '%s'", sessionId));
    }
  }
}
