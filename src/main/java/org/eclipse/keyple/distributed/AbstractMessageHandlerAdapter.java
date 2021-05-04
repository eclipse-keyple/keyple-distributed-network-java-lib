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
 * @since 2.0
 */
abstract class AbstractMessageHandlerAdapter {

  /**
   * (private)<br>
   * The bounded node.
   */
  private AbstractNodeAdapter node;

  /**
   * (package-private)<br>
   *
   * @since 2.0
   */
  AbstractMessageHandlerAdapter() {}

  /**
   * (package-private)<br>
   * Builds and bind a {@link SyncNodeClient} with the handler.<br>
   * It must be invoked by the factory during the initialization phase.
   *
   * @since 2.0
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
  }

  /**
   * (package-private)<br>
   * Builds and bind a {@link SyncNodeServer} with the handler.<br>
   * It must be invoked by the factory during the initialization phase.
   *
   * @since 2.0
   */
  final void bindSyncNodeServer() {
    node = new SyncNodeServerAdapter(this, 20);
  }

  /**
   * (package-private)<br>
   * Builds and bind a {@link AsyncNodeClient} with the handler.<br>
   * It must be invoked by the factory during the initialization phase.
   *
   * @param endpoint The {@link AsyncEndpointClientSpi} endpoint.
   * @param timeoutSeconds Time (in seconds) to wait for the server to transmit a request.
   * @since 2.0
   */
  final void bindAsyncNodeClient(AsyncEndpointClientSpi endpoint, int timeoutSeconds) {
    node = new AsyncNodeClientAdapter(this, endpoint, timeoutSeconds);
  }

  /**
   * (package-private)<br>
   * Builds and bind a {@link AsyncNodeServer} with the handler.<br>
   * It must be invoked by the factory during the initialization phase.
   *
   * @param endpoint The {@link AsyncEndpointServerSpi} endpoint.
   * @since 2.0
   */
  final void bindAsyncNodeServer(AsyncEndpointServerSpi endpoint) {
    node = new AsyncNodeServerAdapter(this, endpoint, 20);
  }

  /**
   * (package-private)<br>
   * Gets the bound {@link AbstractNodeAdapter}.
   *
   * @return Null if no one of the "bind...()" methods has been invoked.
   */
  final AbstractNodeAdapter getNode() {
    return node;
  }

  /**
   * (package-private)<br>
   * If message contains an error, throws the embedded exception.
   *
   * @param message not null instance
   * @since 2.0
   */
  final void checkError(MessageDto message)
      throws Exception { // TODO move to LocalServiceClientAdapter and remove gson dependencies
    if (message.getBody() == null || message.getBody().isEmpty()) {
      return;
    }
    JsonObject output = JsonUtil.getParser().fromJson(message.getBody(), JsonObject.class);
    if (output.has("ERROR")) {
      BodyError body =
          JsonUtil.getParser().fromJson(output.get("ERROR").getAsString(), BodyError.class);
      throw body.getException();
    }
  }

  /**
   * (package-private)<br>
   * Generates a unique session ID.
   *
   * @return A not empty value.
   * @since 2.0
   */
  final String generateSessionId() {
    return UUID.randomUUID().toString();
  }

  /**
   * (package-private)<br>
   * Processes an incoming message.<br>
   * It should be invoked by a node following the reception of a {@link MessageDto}.
   *
   * @param msg The message to process.
   * @since 2.0
   */
  abstract void onMessage(MessageDto msg);
}
