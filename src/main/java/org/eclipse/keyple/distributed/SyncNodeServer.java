/* **************************************************************************************
 * Copyright (c) 2021 Calypso Networks Association https://www.calypsonet-asso.org/
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

import java.util.List;

/**
 * API of the <b>Node</b> associated to a <b>server endpoint</b> using a <b>synchronous</b> network
 * protocol.
 *
 * <p>You must bind this kind of node on the server's side if you plan to use a Client-Server
 * communication protocol, such as standard HTTP for example.
 *
 * <p>Keyple provides its own implementations of this interface and manages their lifecycle.<br>
 * This kind of node can be bind to a all <b>server</b> remote plugins and local services :
 *
 * <ul>
 *   <li>{@code RemotePluginServer}
 *   <li>{@code LocalServiceServer}
 *   <li>{@code PoolLocalServiceServer}
 * </ul>
 *
 * To create it, you must only bind a <b>sync</b> node during the initialization process.<br>
 * Then, you can access it on the server's side from the using the <b>getSyncNode()</b> method of
 * the associated above distributed component.
 *
 * @since 2.0
 */
public interface SyncNodeServer {

  /**
   * Must be invoked by the server controller endpoint following the reception and deserialization
   * of a {@link MessageDto} from the client. Following the receive of a request, the controller
   * must :
   *
   * <ul>
   *   <li>Retrieve the node {@link SyncNodeServer} using the <b>getSyncNode()</b> method of the
   *       associated distributed component.
   *   <li>Invoke this method on the node.
   *   <li>Serialize the result en return it to the client.
   * </ul>
   *
   * @param msg The message to process.
   * @return not null but empty list if there is no result.
   * @throws IllegalArgumentException if some arguments are incorrect.
   * @since 2.0
   */
  List<MessageDto> onRequest(MessageDto msg);
}
