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
package org.eclipse.keyple.distributed;

/**
 * This exception is thrown when an error occurs during the communication between nodes.
 *
 * <p>This can happen, for example, if the remote node is unreachable or takes too long to respond.
 *
 * @since 2.0.0
 */
public class NodeCommunicationException extends RuntimeException {

  /**
   * @param message The message to identify the exception context.
   * @since 2.0.0
   */
  public NodeCommunicationException(String message) {
    super(message);
  }

  /**
   * Encapsulates a lower level external exception.
   *
   * @param message The message to identify the exception context.
   * @param cause The cause.
   * @since 2.0.0
   */
  public NodeCommunicationException(String message, Throwable cause) {
    super(message, cause);
  }
}
