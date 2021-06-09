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

/**
 * (package-private)<br>
 * Server Push Event Strategy
 *
 * <p>This internal class indicates the strategy to adopt in a client-server communication to allow
 * the client to receive events from the server.
 *
 * @since 2.0
 */
final class ServerPushEventStrategyAdapter {

  private final Type type;
  private final int durationMillis;

  /**
   * (package-private)<br>
   * Creates a new instance with a initial duration set to 0.
   *
   * @param type The strategy type to set.
   * @param durationMillis The duration in milliseconds (must be {@code >= 0}).
   * @since 2.0
   */
  ServerPushEventStrategyAdapter(Type type, int durationMillis) {
    this.type = type;
    this.durationMillis = durationMillis;
  }

  /**
   * (package-private)<br>
   * Enumeration of the different strategies.
   *
   * @since 2.0
   */
  enum Type {

    /**
     * Polling : The client requests the server every X milliseconds to check if there are any
     * events.<br>
     * This mode is non-blocking server side and not very demanding on the server's resources
     * because if there are no events, then the server immediately responds to the client.
     *
     * @since 2.0
     */
    POLLING,

    /**
     * Long polling : The client requests continuously the server to check for events.<br>
     * This mode is blocking server side and more costly in resource for the server because if there
     * is no event, then the server keeps the hand during X milliseconds in case an event would
     * occurs before responds to the client.<br>
     * This mode has the advantage of being more reactive.
     *
     * @since 2.0
     */
    LONG_POLLING
  }

  /**
   * (package-private)<br>
   * Gets the strategy type.
   *
   * @return a not null value.
   * @since 2.0
   */
  Type getType() {
    return type;
  }

  /**
   * (package-private)<br>
   * Gets the duration (in milliseconds).
   *
   * @return A positive int.
   * @since 2.0
   */
  int getDurationMillis() {
    return durationMillis;
  }
}
