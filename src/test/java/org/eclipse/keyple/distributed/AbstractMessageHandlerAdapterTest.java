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

import static org.assertj.core.api.Assertions.*;

import com.google.gson.Gson;
import org.eclipse.keyple.core.util.json.BodyError;
import org.eclipse.keyple.core.util.json.JsonUtil;
import org.junit.Test;

public class AbstractMessageHandlerAdapterTest {

  Gson parser = JsonUtil.getParser();

  MessageDto response = new MessageDto().setAction(MessageDto.Action.RESP.name());

  MessageDto responseWithRuntimeException =
      new MessageDto()
          .setAction(MessageDto.Action.ERROR.name())
          .setBody(parser.toJson(new BodyError(new RuntimeException("Test runtime exception"))));

  MessageDto responseWithException =
      new MessageDto()
          .setAction(MessageDto.Action.ERROR.name())
          .setBody(parser.toJson(new BodyError(new Exception("Test exception"))));

  @Test
  public void generateSessionId_shouldReturnNewSessionId() {
    String s1 = AbstractMessageHandlerAdapter.generateSessionId();
    String s2 = AbstractMessageHandlerAdapter.generateSessionId();
    assertThat(s2).isNotEqualTo(s1);
  }

  @Test
  public void checkError_noError_doNothing() {
    AbstractMessageHandlerAdapter.checkError(response);
  }

  @Test(expected = RuntimeException.class)
  public void checkError_runtimeException() {
    AbstractMessageHandlerAdapter.checkError(responseWithRuntimeException);
  }

  @Test(expected = RuntimeException.class)
  public void checkError_exception() {
    AbstractMessageHandlerAdapter.checkError(responseWithException);
  }
}
