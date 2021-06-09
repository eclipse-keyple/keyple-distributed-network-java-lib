/* **************************************************************************************
 * Copyright (c) 2021 Calypso Networks Association https://calypsonet.org/
 *
 * See the NOTICE file(s) distributed with this work for additional information
 * regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 ************************************************************************************** */
package org.eclipse.keyple.distributed.spi;

import org.eclipse.keyple.distributed.AsyncNodeClient;
import org.eclipse.keyple.distributed.MessageDto;

/**
 * SPI of the <b>client endpoint</b> using an <b>asynchronous</b> network protocol.
 *
 * <p>You must provide an implementation of this interface if you plan to use a full duplex
 * communication protocol, such as Web Sockets for example.
 *
 * <p>This endpoint interacts locally with a {@link AsyncNodeClient} node and remotely with a {@link
 * AsyncEndpointServerSpi} endpoint.
 *
 * @since 2.0
 */
public interface AsyncEndpointClientSpi {

  /**
   * Invoked by the {@link AsyncNodeClient} node to open a communication session with the server.
   * <br>
   * Following the opening of the session you must :
   *
   * <ul>
   *   <li>Associate the session with the provided <b>sessionId</b> in order to be able to retrieve
   *       the session later.
   *   <li>Retrieve the {@link AsyncNodeClient} node using the <b>getAsyncNode()</b> method of the
   *       associated distributed component, depending on your use case.
   *   <li>Invoke the method {@link AsyncNodeClient#onOpen(String)} on the node.
   * </ul>
   *
   * @param sessionId The session id.
   * @since 2.0
   */
  void openSession(String sessionId);

  /**
   * Invoked by the {@link AsyncNodeClient} node to send a {@link MessageDto} to the server.<br>
   * You have to :
   *
   * <ul>
   *   <li>Find the opened session using the accessible <b>sessionId</b> value using the method
   *       {@link MessageDto#getSessionId()} on the provided message to send.
   *   <li>Serialize and send the {@link MessageDto} to the server.
   * </ul>
   *
   * @param message The message to send.
   * @since 2.0
   */
  void sendMessage(MessageDto message);

  /**
   * Invoked by the {@link AsyncNodeClient} node to close a communication session with the server
   * identified by the provided <b>sessionId</b>.<br>
   * Following the closing of the session you must :
   *
   * <ul>
   *   <li>Unregister the session associated to the provided <b>sessionId</b>.
   *   <li>Retrieve the {@link AsyncNodeClient} node using the <b>getAsyncNode()</b> method of the
   *       associated distributed component, depending on your use case.
   *   <li>Call the method {@link AsyncNodeClient#onClose(String)} on the node.
   * </ul>
   *
   * @param sessionId The session id.
   * @since 2.0
   */
  void closeSession(String sessionId);
}
