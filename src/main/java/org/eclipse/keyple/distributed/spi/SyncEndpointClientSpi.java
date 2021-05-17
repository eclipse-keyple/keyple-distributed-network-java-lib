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
package org.eclipse.keyple.distributed.spi;

import java.util.List;
import org.eclipse.keyple.distributed.MessageDto;
import org.eclipse.keyple.distributed.SyncNodeClient;

/**
 * SPI of the <b>client endpoint</b> using a <b>synchronous</b> network protocol.
 *
 * <p>You must provide an implementation of this interface if you plan to use a Client-Server
 * communication protocol, such as standard HTTP for example.
 *
 * <p>This endpoint interacts only with a remote server controller.
 *
 * @since 2.0
 */
public interface SyncEndpointClientSpi {

  /**
   * Invoked by the {@link SyncNodeClient} node to send a {@link MessageDto} to the server.<br>
   * You have to serialize and send the provided {@link MessageDto} to the server, then retry the
   * response which is a list of {@link MessageDto}.
   *
   * @param message The message to send.
   * @return a null or empty list if there is no result.
   * @since 2.0
   */
  List<MessageDto> sendRequest(MessageDto message);
}
