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

import static org.eclipse.keyple.distributed.MessageDto.API_LEVEL;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.util.concurrent.Callable;

public abstract class AbstractNodeAdapterTest {

  static final String SESSION_ID = "sessionId";

  AbstractMessageHandlerAdapter handler;

  MessageDto msg;
  MessageDto response;

  {
    msg =
        new MessageDto()
            .setApiLevel(API_LEVEL)
            .setSessionId(SESSION_ID)
            .setAction(MessageDto.Action.EXECUTE_REMOTE_SERVICE.name())
            .setClientNodeId("clientNodeId")
            .setServerNodeId("serverNodeId");

    response = new MessageDto(msg);
  }

  void setUp() {
    handler = mock(AbstractMessageHandlerAdapter.class);
  }

  void setHandlerError() {
    doThrow(new NodeCommunicationException("TEST")).when(handler).onMessage(any(MessageDto.class));
  }

  Callable<Boolean> threadHasStateTimedWaiting(final Thread thread) {
    return new Callable<Boolean>() {
      public Boolean call() {
        return thread.getState() == Thread.State.TIMED_WAITING;
      }
    };
  }

  Callable<Boolean> threadHasStateTerminated(final Thread thread) {
    return new Callable<Boolean>() {
      public Boolean call() {
        return thread.getState() == Thread.State.TERMINATED;
      }
    };
  }
}
