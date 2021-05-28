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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.keyple.core.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * (package-private)<br>
 * Sync Node Server implementation.
 *
 * @since 2.0
 */
final class SyncNodeServerAdapter extends AbstractNodeAdapter implements SyncNodeServer {

  private static final Logger logger = LoggerFactory.getLogger(SyncNodeServerAdapter.class);

  private final Map<String, SessionManager> sessionManagers;
  private final Map<String, ServerPushEventManager> pluginManagers;
  private final Map<String, ServerPushEventManager> readerManagers;
  private final JsonParser jsonParser;

  /**
   * (package-private)<br>
   *
   * @param handler The associated handler.
   * @param timeoutSeconds The default timeout (in seconds) to use.
   * @since 2.0
   */
  SyncNodeServerAdapter(AbstractMessageHandlerAdapter handler, int timeoutSeconds) {
    super(handler, timeoutSeconds);
    jsonParser = new JsonParser();
    this.sessionManagers = new ConcurrentHashMap<String, SessionManager>();
    this.pluginManagers = new ConcurrentHashMap<String, ServerPushEventManager>();
    this.readerManagers = new ConcurrentHashMap<String, ServerPushEventManager>();
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0
   */
  @Override
  public List<MessageDto> onRequest(MessageDto message) {

    // Check mandatory fields
    Assert.getInstance()
        .notNull(message, "message")
        .notEmpty(message.getSessionId(), "sessionId")
        .notEmpty(message.getAction(), "action")
        .notEmpty(message.getClientNodeId(), "clientNodeId");

    List<MessageDto> responses;
    MessageDto.Action action = MessageDto.Action.valueOf(message.getAction());
    switch (action) {
      case CHECK_PLUGIN_EVENT:
        responses = checkEvents(message, pluginManagers);
        break;
      case CHECK_READER_EVENT:
        responses = checkEvents(message, readerManagers);
        break;
      default:
        responses = processOnRequest(message);
    }
    return responses != null ? responses : Collections.<MessageDto>emptyList();
  }

  /**
   * (private)<br>
   * Check on client request if some events are present in the associated sandbox.
   *
   * @param message The client message containing all client info (node id, strategy, ...)
   * @param eventManagers The event managers map.
   * @return A null list or a not empty list
   */
  private List<MessageDto> checkEvents(
      MessageDto message, Map<String, ServerPushEventManager> eventManagers) {
    ServerPushEventManager manager = getEventManager(message, eventManagers);
    return manager.checkEvents(message);
  }

  /**
   * (private)<br>
   * Processes onRequest for standard transaction call.<br>
   * Create a new session manager if needed.
   *
   * @param message The message to process.
   * @return A nullable list or which contains at most one element.
   */
  private List<MessageDto> processOnRequest(MessageDto message) {
    SessionManager manager = sessionManagers.get(message.getSessionId());
    if (manager == null) {
      manager = new SessionManager(message.getSessionId());
      sessionManagers.put(message.getSessionId(), manager);
    }
    MessageDto response = manager.onRequest(message);
    return response != null ? Collections.singletonList(response) : null;
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0
   */
  @Override
  MessageDto sendRequest(MessageDto message) {
    message.setServerNodeId(getNodeId());
    SessionManager manager = sessionManagers.get(message.getSessionId());
    try {
      return manager.sendRequest(message);
    } catch (RuntimeException e) {
      sessionManagers.remove(message.getSessionId());
      throw e;
    }
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0
   */
  @Override
  void sendMessage(MessageDto message) {
    message.setServerNodeId(getNodeId());
    MessageDto.Action action = MessageDto.Action.valueOf(message.getAction());
    switch (action) {
      case PLUGIN_EVENT:
        postEvent(message, pluginManagers);
        break;
      case READER_EVENT:
        postEvent(message, readerManagers);
        break;
      default:
        processSendMessage(message);
    }
  }

  /**
   * (private)<br>
   * Post an event into the sandbox, analyse the client strategy, and eventually try to wake up the
   * pending client task in case of long polling strategy.
   *
   * @param message The message containing the event to post.
   * @param eventManagers The event managers map.
   */
  private void postEvent(MessageDto message, Map<String, ServerPushEventManager> eventManagers) {
    ServerPushEventManager manager = getEventManager(message, eventManagers);
    manager.postEvent(message);
  }

  /**
   * (private)<br>
   * Get or create an event manager associated to a client node id.
   *
   * @param message The message containing the client's information
   * @param eventManagers The event managers map.
   * @return a not null reference.
   */
  private ServerPushEventManager getEventManager(
      MessageDto message, Map<String, ServerPushEventManager> eventManagers) {
    ServerPushEventManager manager = eventManagers.get(message.getClientNodeId());
    if (manager == null) {
      manager = new ServerPushEventManager(message.getClientNodeId());
      eventManagers.put(message.getClientNodeId(), manager);
    }
    return manager;
  }

  /**
   * (private)<br>
   * Processes sendMessage for standard transaction call.<br>
   * Note that the associated session is also closed.
   *
   * @param message The message to process.
   */
  private void processSendMessage(MessageDto message) {
    SessionManager manager = sessionManagers.get(message.getSessionId());
    if (manager == null) {
      throw new IllegalStateException("Session is closed");
    }
    try {
      manager.sendMessage(message);
    } finally {
      sessionManagers.remove(message.getSessionId());
    }
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
      // NOP
    }

    /**
     * (private)<br>
     * Invoked by the endpoint when a new client request is received.
     *
     * @param message The message to process.
     * @return a not null reference on a message to return to the client.
     */
    private synchronized MessageDto onRequest(MessageDto message) {
      checkState(SessionManagerState.INITIALIZED, SessionManagerState.SEND_REQUEST_BEGIN);
      if (state == SessionManagerState.INITIALIZED) {
        // Process the message as a client request
        state = SessionManagerState.ON_REQUEST;
        getHandler().onMessage(message);
      } else {
        // State is SEND_REQUEST_BEGIN
        // Process the message as a client response
        postMessageAndNotify(message, SessionManagerState.SEND_REQUEST_END);
      }
      waitForState(SessionManagerState.SEND_MESSAGE, SessionManagerState.SEND_REQUEST_BEGIN);
      return response;
    }

    /**
     * (private)<br>
     * Invoked by the handler to send a request to the endpoint and await a response.
     *
     * @param message The message to send.
     * @return The response.
     */
    private synchronized MessageDto sendRequest(MessageDto message) {
      postMessageAndNotify(message, SessionManagerState.SEND_REQUEST_BEGIN);
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
      postMessageAndNotify(message, SessionManagerState.SEND_MESSAGE);
    }

    /**
     * (private)<br>
     * Posts a message and try to wake up the waiting task.
     *
     * @param message The message to post.
     * @param targetState The new state to set before to notify the waiting task.
     */
    private synchronized void postMessageAndNotify(
        MessageDto message, SessionManagerState targetState) {
      response = message;
      state = targetState;
      notifyAll();
    }
  }

  /**
   * (private)<br>
   * This inner class is a manager for server push events.
   */
  private class ServerPushEventManager {

    private final String clientNodeId;

    private List<MessageDto> events;
    private ServerPushEventStrategyAdapter strategy;

    /**
     * (private)<br>
     * Constructor
     *
     * @param clientNodeId The client node id to manage.
     */
    private ServerPushEventManager(String clientNodeId) {
      this.clientNodeId = clientNodeId;
      this.events = null;
      this.strategy = null;
    }

    /**
     * (private)<br>
     * Posts an event into the sandbox, analyse the client strategy, and eventually try to wake up
     * the pending client task in case of long polling strategy.
     *
     * @param message The message containing the event to post.
     */
    private synchronized void postEvent(MessageDto message) {

      // Post the event
      if (events == null) {
        events = new ArrayList<MessageDto>(1);
      }
      events.add(message);

      // Gets the client's strategy
      // If strategy is long polling, then try to wake up the associated awaiting task.
      if (strategy != null
          && strategy.getType() == ServerPushEventStrategyAdapter.Type.LONG_POLLING) {
        notifyAll();
      }
    }

    /**
     * (private)<br>
     * Checks on client request if some events are present in the associated sandbox.
     *
     * @param message The client message containing all client info (node id, strategy, ...)
     * @return a null list or a not empty list
     */
    private synchronized List<MessageDto> checkEvents(MessageDto message) {
      try {
        // We're checking to see if any events are already present
        if (events != null) {
          return events;
        }

        // If none, then gets the client's strategy
        registerClientStrategy(message);

        // If is a long polling strategy, then await for an event notification.
        if (strategy.getType() == ServerPushEventStrategyAdapter.Type.LONG_POLLING) {
          waitAtMost(strategy.getDurationMillis());
        }
        return events;
      } finally {
        events = null;
      }
    }

    /**
     * (private)<br>
     * Registers the client strategy in case of first client invocation.
     *
     * @param message The client message containing all client info (node id, strategy, ...)
     * @throws IllegalArgumentException in case of first client invocation with bad arguments.
     */
    private void registerClientStrategy(MessageDto message) {

      // Gets the client registered strategy if exists.
      if (strategy == null) {

        // Register the client's strategy
        JsonObject body;
        ServerPushEventStrategyAdapter.Type type;
        try {
          body = jsonParser.parse(message.getBody()).getAsJsonObject();
          type = ServerPushEventStrategyAdapter.Type.valueOf(body.get("strategy").getAsString());
        } catch (Exception e) {
          throw new IllegalArgumentException("body", e);
        }

        int durationSeconds = 0;
        if (type == ServerPushEventStrategyAdapter.Type.LONG_POLLING) {
          try {
            durationSeconds = body.get("duration").getAsInt();
          } catch (Exception e) {
            throw new IllegalArgumentException("long polling duration", e);
          }
        }

        strategy = new ServerPushEventStrategyAdapter(type, durationSeconds);
      }
    }

    /**
     * (private)<br>
     * Wait a most the provided max awaiting time (in milliseconds).
     *
     * @param maxAwaitingTimeMillis The max awaiting time (in milliseconds).
     */
    private synchronized void waitAtMost(int maxAwaitingTimeMillis) {
      try {
        long deadline = new Date().getTime() + maxAwaitingTimeMillis;
        while (events == null && new Date().getTime() < deadline) {
          wait(maxAwaitingTimeMillis);
        }
      } catch (InterruptedException e) {
        logger.error(
            "Unexpected interruption of the task associated with the node's id {}",
            clientNodeId,
            e);
        Thread.currentThread().interrupt();
      }
    }
  }
}
