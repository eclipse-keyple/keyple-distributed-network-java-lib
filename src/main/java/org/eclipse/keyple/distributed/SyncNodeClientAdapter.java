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

import static org.eclipse.keyple.distributed.MessageDto.*;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.keyple.core.util.Assert;
import org.eclipse.keyple.distributed.spi.SyncEndpointClientSpi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sync Node Client implementation.
 *
 * @since 2.0.0
 */
final class SyncNodeClientAdapter extends AbstractNodeAdapter implements SyncNodeClient {

  private static final Logger logger = LoggerFactory.getLogger(SyncNodeClientAdapter.class);

  private final SyncEndpointClientSpi endpoint;

  private final ServerPushEventStrategyAdapter pluginObservationStrategy;
  private EventObserver pluginEventObserver;
  private final Object pluginMonitor;

  private final ServerPushEventStrategyAdapter readerObservationStrategy;
  private EventObserver readerEventObserver;
  private final Object readerMonitor;
  private volatile int nbOfObservedReaders;

  /**
   * @param handler The associated handler.
   * @param endpoint The user client sync endpoint.
   * @param pluginObservationStrategy The server push event strategy associated to the plugin
   *     observation (null if must not be activate).<br>
   *     This parameter can be used only for <b>Reader Server Side</b> use case.
   * @param readerObservationStrategy The server push event strategy associated to the reader
   *     observation (null if must not be activate).<br>
   *     This parameter can be used only for <b>Reader Server Side</b> use case.
   * @since 2.0.0
   */
  SyncNodeClientAdapter(
      AbstractMessageHandlerAdapter handler,
      SyncEndpointClientSpi endpoint,
      ServerPushEventStrategyAdapter pluginObservationStrategy,
      ServerPushEventStrategyAdapter readerObservationStrategy) {

    super(handler, 0);
    this.endpoint = endpoint;
    this.pluginObservationStrategy = pluginObservationStrategy;
    this.pluginMonitor = new Object();
    this.readerObservationStrategy = readerObservationStrategy;
    this.readerMonitor = new Object();
    this.nbOfObservedReaders = 0;
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0.0
   */
  @Override
  MessageDto sendRequest(MessageDto message) {

    message.setClientNodeId(getNodeId());
    List<MessageDto> responses = endpoint.sendRequest(message);

    if (responses == null || responses.isEmpty()) {
      return null;
    } else if (responses.size() == 1) {
      MessageDto response = responses.get(0);
      Assert.getInstance()
          .notNull(response, "message")
          .notEmpty(response.getSessionId(), "sessionId")
          .notEmpty(response.getAction(), "action")
          .notEmpty(response.getClientNodeId(), "clientNodeId")
          .notEmpty(response.getServerNodeId(), "serverNodeId");
      return response;
    } else {
      throw new IllegalStateException(
          String.format(
              "The list returned by the client endpoint should have contained a single element but contains %s elements.",
              responses.size()));
    }
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0.0
   */
  @Override
  void sendMessage(MessageDto message) {
    message.setClientNodeId(getNodeId());
    endpoint.sendRequest(message);
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0.0
   */
  @Override
  void onStartPluginsObservation() {
    if (pluginObservationStrategy == null) {
      throw new IllegalStateException("The plugin observation strategy is not set.");
    }
    synchronized (pluginMonitor) {
      if (pluginEventObserver == null) {
        pluginEventObserver =
            new EventObserver(pluginObservationStrategy, MessageDto.Action.CHECK_PLUGIN_EVENT);
        pluginEventObserver.start();
      }
    }
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0.0
   */
  @Override
  void onStopPluginsObservation() {
    synchronized (pluginMonitor) {
      if (pluginEventObserver != null) {
        pluginEventObserver.stop();
        pluginEventObserver = null;
      }
    }
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0.0
   */
  @Override
  void onStartReaderObservation() {
    if (readerObservationStrategy == null) {
      throw new IllegalStateException("The reader observation strategy is not set.");
    }
    synchronized (readerMonitor) {
      nbOfObservedReaders++;
      if (readerEventObserver == null) {
        readerEventObserver =
            new EventObserver(readerObservationStrategy, MessageDto.Action.CHECK_READER_EVENT);
        readerEventObserver.start();
      }
    }
  }

  /**
   * {@inheritDoc}
   *
   * @since 2.0.0
   */
  @Override
  void onStopReaderObservation() {
    synchronized (readerMonitor) {
      if (nbOfObservedReaders > 0) {
        nbOfObservedReaders--;
      }
      if (nbOfObservedReaders == 0 && readerEventObserver != null) {
        readerEventObserver.stop();
        readerEventObserver = null;
      }
    }
  }

  /**
   * Event Observer inner class.<br>
   * This class can be used only for <b>Reader Server Side</b> use case.
   */
  private class EventObserver {

    private final ServerPushEventStrategyAdapter strategy;
    private final MessageDto.Action action;
    private final MessageDto message;
    private final Thread thread;

    /**
     * Constructor.
     *
     * @param strategy The server push event strategy.
     * @param action The action to perform.
     */
    private EventObserver(ServerPushEventStrategyAdapter strategy, MessageDto.Action action) {
      this.strategy = strategy;
      this.action = action;
      this.message = buildMessage();
      if (strategy.getType() == ServerPushEventStrategyAdapter.Type.POLLING) {
        this.thread = new PollingEventObserver();
      } else {
        this.thread = new LongPollingEventObserver();
      }
      thread.setUncaughtExceptionHandler(new EventObserverUncaughtExceptionHandler());
      thread.setName(action.name());
    }

    /**
     * Builds the message to send to server for event observation.
     *
     * @return A not null reference.
     */
    private MessageDto buildMessage() {
      JsonObject body = new JsonObject();
      // API level:
      // The API level is retrieved from the wrapper, as the body content has been created by the
      // Distributed client layer.
      // In this particular case, the API level contained in the body does not reflect the version
      // of
      // the body, but that of the Core client layer.
      body.addProperty(JsonProperty.CORE_API_LEVEL.getKey(), getHandler().getCoreApiLevel());
      body.addProperty(JsonProperty.STRATEGY.getKey(), strategy.getType().name());
      if (strategy.getType() == ServerPushEventStrategyAdapter.Type.LONG_POLLING) {
        body.addProperty(JsonProperty.DURATION.getKey(), strategy.getDurationMillis());
      }
      return new MessageDto()
          .setSessionId(AbstractMessageHandlerAdapter.generateSessionId())
          .setAction(action.name())
          .setClientNodeId(getNodeId())
          .setBody(body.toString());
    }

    /** Polling Event Observer inner thread. */
    private class PollingEventObserver extends Thread {
      @Override
      public void run() {
        int requestFrequencyMillis = strategy.getDurationMillis();
        while (!Thread.currentThread().isInterrupted()) {
          checkForEvents();
          try {
            Thread.sleep(requestFrequencyMillis);
          } catch (InterruptedException e) {
            logger.error("Unexpected interruption of thread {}", getName(), e);
            Thread.currentThread().interrupt();
          }
        }
      }
    }

    /** Long Polling Event Observer inner thread. */
    private class LongPollingEventObserver extends Thread {
      @Override
      public void run() {
        while (!Thread.currentThread().isInterrupted()) {
          checkForEvents();
        }
      }
    }

    /** Event Observer Uncaught Exception Handler inner class. */
    private class EventObserverUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
      @Override
      public void uncaughtException(Thread t, Throwable e) {
        logger.error("Interruption of thread {} caused by an unhandled exception", t.getName(), e);
      }
    }

    /**
     * Checks if server has some events to push.<br>
     * If so, then forward the events to the handler.
     */
    private void checkForEvents() {
      List<MessageDto> responses;
      try {
        responses = endpoint.sendRequest(message);
      } catch (Exception e) {
        logger.error("Server connection error", e);
        responses = retryRequest();
      }
      if (responses != null && !responses.isEmpty()) {
        for (MessageDto event : responses) {
          getHandler().onMessage(event);
        }
      }
    }

    /**
     * Retries to send the request to the server in case of server connection error until the server
     * communication is reestablished or the thread is interrupted.<br>
     * The timing of the attempts is based on the Fibonacci sequence.
     *
     * @return A not null list.
     */
    private List<MessageDto> retryRequest() {
      List<MessageDto> responses;
      int timer1 = 0;
      int timer2 = 1000;
      int timer;
      while (!Thread.currentThread().isInterrupted()) {
        try {
          timer = timer1 + timer2;
          Thread.sleep(timer);
          logger.info("Retry to send request after {} seconds...", timer / 1000);
          responses = sendRequestSilently();
          if (responses != null) {
            logger.info("Server connection retrieved");
            return responses;
          } else {
            timer1 = timer2;
            timer2 = timer;
          }
        } catch (InterruptedException e) {
          logger.error("Unexpected interruption of thread {}", Thread.currentThread().getName(), e);
          Thread.currentThread().interrupt();
        }
      }
      return new ArrayList<MessageDto>();
    }

    /**
     * Tries to sends a request silently without throwing an exception.
     *
     * @return null if the sending has failed.
     */
    private List<MessageDto> sendRequestSilently() {
      try {
        return endpoint.sendRequest(message);
      } catch (Exception e) {
        return null; // NOSONAR Null is a functional value
      }
    }

    /** Starts the thread. */
    private void start() {
      thread.start();
    }

    /** Stops the thread. */
    private void stop() {
      thread.interrupt();
    }
  }
}
