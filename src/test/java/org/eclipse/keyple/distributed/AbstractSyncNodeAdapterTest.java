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

import static org.eclipse.keyple.distributed.MessageDto.*;

import com.google.gson.JsonObject;

public abstract class AbstractSyncNodeAdapterTest extends AbstractNodeAdapterTest {

  String bodyPolling;
  String bodyLongPolling;
  String bodyLongPollingLongTimeout;

  {
    JsonObject body = new JsonObject();
    body.addProperty(
        JsonProperty.STRATEGY.getKey(), ServerPushEventStrategyAdapter.Type.POLLING.name());
    bodyPolling = body.toString();

    body = new JsonObject();
    body.addProperty(
        JsonProperty.STRATEGY.getKey(), ServerPushEventStrategyAdapter.Type.LONG_POLLING.name());
    body.addProperty(JsonProperty.DURATION.getKey(), 1000);
    bodyLongPolling = body.toString();

    body = new JsonObject();
    body.addProperty(
        JsonProperty.STRATEGY.getKey(), ServerPushEventStrategyAdapter.Type.LONG_POLLING.name());
    body.addProperty(JsonProperty.DURATION.getKey(), 5000);
    bodyLongPollingLongTimeout = body.toString();
  }

  void setUp() {
    super.setUp();
  }
}
