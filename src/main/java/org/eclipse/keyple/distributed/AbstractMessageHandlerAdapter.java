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

import java.util.UUID;
import org.eclipse.keyple.core.util.json.BodyError;
import org.eclipse.keyple.core.util.json.JsonUtil;
import org.eclipse.keyple.distributed.spi.AsyncEndpointClientSpi;
import org.eclipse.keyple.distributed.spi.AsyncEndpointServerSpi;
import org.eclipse.keyple.distributed.spi.SyncEndpointClientSpi;

/**
 * (package-private)<br>
 * Abstract Message Handler.
 *
 * @since 2.0.0
 */
abstract class AbstractMessageHandlerAdapter {

  private AbstractNodeAdapter node;
  private boolean isBoundToSyncNode;

  /**
   * (package-private)<br>
   *
   * @since 2.0.0
   */
  AbstractMessageHandlerAdapter() {}

  /**
   * (package-private)<br>
   * Generates a unique session ID.
   *
   * @return A not empty value.
   * @since 2.0.0
   */
  static String generateSessionId() {
    return UUID.randomUUID().toString();
  }

  /**
   * (package-private)<br>
   * Checks if the provided message contains an error.
   *
   * @param message The message to check.
   * @throws RuntimeException If the message contains an error.
   */
  static void checkError(MessageDto message) {
    if (message.getAction().equals(MessageDto.Action.ERROR.name())) {
      throw new RuntimeException( // NOSONAR
          JsonUtil.getParser().fromJson(message.getBody(), BodyError.class).getException());
    }
  }

  /**
   * (package-private)<br>
   * Builds and bind a {@link SyncNodeClient} with the handler.<br>
   * It must be invoked by the factory during the initialization phase.
   *
   * @since 2.0.0
   * @param endpoint The {@link SyncEndpointClientSpi} endpoint.
   * @param pluginObservationStrategy The {@link ServerPushEventStrategyAdapter} associated to the
   *     plugin (null if observation is not activated).
   * @param readerObservationStrategy The {@link ServerPushEventStrategyAdapter} associated to the
   *     reader (null if observation is not activated).
   */
  final void bindSyncNodeClient(
      SyncEndpointClientSpi endpoint,
      ServerPushEventStrategyAdapter pluginObservationStrategy,
      ServerPushEventStrategyAdapter readerObservationStrategy) {
    node =
        new SyncNodeClientAdapter(
            this, endpoint, pluginObservationStrategy, readerObservationStrategy);
    isBoundToSyncNode = true;
  }

  /**
   * (package-private)<br>
   * Builds and bind a {@link SyncNodeServer} with the handler.<br>
   * It must be invoked by the factory during the initialization phase.
   *
   * @since 2.0.0
   */
  final void bindSyncNodeServer() {
    node = new SyncNodeServerAdapter(this, 20);
    isBoundToSyncNode = true;
  }

  /**
   * (package-private)<br>
   * Builds and bind a {@link AsyncNodeClient} with the handler.<br>
   * It must be invoked by the factory during the initialization phase.
   *
   * @param endpoint The {@link AsyncEndpointClientSpi} endpoint.
   * @param timeoutSeconds Time (in seconds) to wait for the server to transmit a request.
   * @since 2.0.0
   */
  final void bindAsyncNodeClient(AsyncEndpointClientSpi endpoint, int timeoutSeconds) {
    node = new AsyncNodeClientAdapter(this, endpoint, timeoutSeconds);
    isBoundToSyncNode = false;
  }

  /**
   * (package-private)<br>
   * Builds and bind a {@link AsyncNodeServer} with the handler.<br>
   * It must be invoked by the factory during the initialization phase.
   *
   * @param endpoint The {@link AsyncEndpointServerSpi} endpoint.
   * @since 2.0.0
   */
  final void bindAsyncNodeServer(AsyncEndpointServerSpi endpoint) {
    node = new AsyncNodeServerAdapter(this, endpoint, 20);
    isBoundToSyncNode = false;
  }

  /**
   * (package-private)<br>
   * Gets the bound {@link AbstractNodeAdapter}.
   *
   * @return Null if no one of the "bind...()" methods has been invoked.
   * @since 2.0.0
   */
  final AbstractNodeAdapter getNode() {
    return node;
  }

  /**
   * (package-private)<br>
   * Is handler bound to a synchronous node ?
   *
   * @return false by default until the binding of the node.
   * @since 2.0.0
   */
  final boolean isBoundToSyncNode() {
    return isBoundToSyncNode;
  }

  /**
   * (package-private)<br>
   * Processes an incoming message.<br>
   * It should be invoked by a node following the reception of a {@link MessageDto}.
   *
   * @param message The message to process.
   * @since 2.0.0
   */
  abstract void onMessage(MessageDto message);
}
