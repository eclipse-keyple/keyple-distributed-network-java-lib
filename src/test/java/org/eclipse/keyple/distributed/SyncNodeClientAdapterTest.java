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

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import org.awaitility.Durations;
import org.eclipse.keyple.distributed.spi.SyncEndpointClientSpi;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SyncNodeClientAdapterTest extends AbstractSyncNodeAdapterTest {

  KeypleMessageHandlerErrorMock handlerError;
  SyncEndpointClientPollingMock endpoint;
  SyncEndpointClientLongPollingMock endpointLongPolling;
  SyncEndpointClientErrorMock endpointError;

  List<MessageDto> responses;

  ServerPushEventStrategyAdapter pollingEventStrategy;
  ServerPushEventStrategyAdapter longPollingEventStrategy;

  {
    responses = new ArrayList<MessageDto>();
    responses.add(response);

    pollingEventStrategy =
        new ServerPushEventStrategyAdapter(ServerPushEventStrategyAdapter.Type.POLLING, 1000);

    longPollingEventStrategy =
        new ServerPushEventStrategyAdapter(ServerPushEventStrategyAdapter.Type.LONG_POLLING, 1000);
  }

  static class KeypleMessageHandlerErrorMock extends AbstractMessageHandlerAdapter {

    boolean isError = false;

    @Override
    void onMessage(MessageDto msg) {
      isError = true;
      throw new NodeCommunicationException("Handler error mocked");
    }
  }

  class SyncEndpointClientPollingMock implements SyncEndpointClientSpi {

    List<MessageDto> messages = new ArrayList<MessageDto>();

    @Override
    public List<MessageDto> sendRequest(MessageDto msg) {
      messages.add(msg);
      return responses;
    }
  }

  class SyncEndpointClientLongPollingMock implements SyncEndpointClientSpi {

    List<MessageDto> messages = new ArrayList<MessageDto>();

    @Override
    public List<MessageDto> sendRequest(MessageDto msg) {
      messages.add(msg);
      // Equivalent to Thread.sleep(1000) but Sonar compliant.
      await()
          .pollDelay(Durations.ONE_SECOND)
          .until(
              new Callable<Boolean>() {
                @Override
                public Boolean call() {
                  return true;
                }
              });
      return responses;
    }
  }

  class SyncEndpointClientErrorMock implements SyncEndpointClientSpi {

    List<MessageDto> messages = new ArrayList<MessageDto>();
    int cpt = 0;

    @Override
    public List<MessageDto> sendRequest(MessageDto msg) {
      cpt++;
      if (cpt >= 2 && cpt <= 3) {
        throw new NodeCommunicationException("Endpoint error mocked");
      }
      messages.add(msg);
      return responses;
    }
  }

  Callable<Boolean> handlerErrorOccurred() {
    return new Callable<Boolean>() {
      public Boolean call() {
        return handlerError.isError;
      }
    };
  }

  Callable<Boolean> endpointMessagesHasMinSize(final int size) {
    return new Callable<Boolean>() {
      public Boolean call() {
        return endpoint.messages.size() >= size;
      }
    };
  }

  Callable<Boolean> endpointLongPollingMessagesHasAtLeastOneElement() {
    return new Callable<Boolean>() {
      public Boolean call() {
        return endpointLongPolling.messages.size() >= 1;
      }
    };
  }

  Callable<Boolean> endpointErrorMessagesHasAtLeastTwoElements() {
    return new Callable<Boolean>() {
      public Boolean call() {
        return endpointError.messages.size() >= 2;
      }
    };
  }

  @Before
  public void setUp() {
    super.setUp();
    handlerError = new KeypleMessageHandlerErrorMock();
    endpoint = new SyncEndpointClientPollingMock();
    endpointLongPolling = new SyncEndpointClientLongPollingMock();
    endpointError = new SyncEndpointClientErrorMock();
  }

  public void checkEventDto(MessageDto msg1, MessageDto.Action action, String body) {
    assertThat(msg1.getSessionId()).isNotEmpty();
    assertThat(msg1.getAction()).isEqualTo(action.name());
    assertThat(msg1.getClientNodeId()).isNotEmpty();
    assertThat(msg1.getServerNodeId()).isNull();
    assertThat(msg1.getLocalReaderName()).isNull();
    assertThat(msg1.getRemoteReaderName()).isNull();
    assertThat(msg1.getBody()).isEqualTo(body);
    assertThat(msg1.getSessionId()).isNotEqualTo(msg1.getClientNodeId());
  }

  @Test(expected = IllegalStateException.class)
  public void
      onStartPluginsObservation_whenPluginObservationStrategyIsNotProvided_shouldThrowISE() {
    SyncNodeClientAdapter node = new SyncNodeClientAdapter(handler, endpoint, null, null);
    node.onStartPluginsObservation();
  }

  @Test
  public void
      onStartPluginsObservation_whenPluginObservationStrategyIsProvided_shouldStartAPluginObserver() {
    SyncNodeClientAdapter node =
        new SyncNodeClientAdapter(handler, endpoint, pollingEventStrategy, null);
    node.onStartPluginsObservation();
    await().atMost(5, TimeUnit.SECONDS).until(endpointMessagesHasMinSize(2));
    MessageDto msg1 = endpoint.messages.get(0);
    MessageDto msg2 = endpoint.messages.get(1);
    assertThat(msg1).isSameAs(msg2).isEqualToComparingFieldByField(msg2);
  }

  @Test
  public void
      onStopPluginsObservation_whenPluginObservationStrategyIsNotProvided_shouldDoNothing() {
    SyncNodeClientAdapter node = new SyncNodeClientAdapter(handler, endpoint, null, null);
    node.onStopPluginsObservation();
  }

  @Test
  public void onStopPluginsObservation_whenOnStartPluginsObservationIsNotInvoked_shouldDoNothing() {
    SyncNodeClientAdapter node =
        new SyncNodeClientAdapter(handler, endpoint, pollingEventStrategy, null);
    node.onStopPluginsObservation();
  }

  @Test
  public void
      onStopPluginsObservation_whenOnStartPluginsObservationIsInvoked_shouldStopObservation() {
    SyncNodeClientAdapter node =
        new SyncNodeClientAdapter(handler, endpoint, pollingEventStrategy, null);
    node.onStartPluginsObservation();
    node.onStopPluginsObservation();
  }

  @Test(expected = IllegalStateException.class)
  public void onStartReaderObservation_whenReaderObservationStrategyIsNotProvided_shouldThrowISE() {
    SyncNodeClientAdapter node = new SyncNodeClientAdapter(handler, endpoint, null, null);
    node.onStartReaderObservation();
  }

  @Test
  public void
      onStartReaderObservation_whenReaderObservationStrategyIsProvided_shouldStartAReaderObserver() {
    SyncNodeClientAdapter node =
        new SyncNodeClientAdapter(handler, endpoint, null, pollingEventStrategy);
    node.onStartReaderObservation();
    await().atMost(5, TimeUnit.SECONDS).until(endpointMessagesHasMinSize(2));
    MessageDto msg1 = endpoint.messages.get(0);
    MessageDto msg2 = endpoint.messages.get(1);
    assertThat(msg1).isSameAs(msg2).isEqualToComparingFieldByField(msg2);
  }

  @Test
  public void onStopReaderObservation_whenReaderObservationStrategyIsNotProvided_shouldDoNothing() {
    SyncNodeClientAdapter node = new SyncNodeClientAdapter(handler, endpoint, null, null);
    node.onStopReaderObservation();
  }

  @Test
  public void onStopReaderObservation_whenOnStartReaderObservationIsNotInvoked_shouldDoNothing() {
    SyncNodeClientAdapter node =
        new SyncNodeClientAdapter(handler, endpoint, null, pollingEventStrategy);
    node.onStopReaderObservation();
  }

  @Test
  public void
      onStopReaderObservation_whenOnStartReaderObservationIsInvoked_shouldStopObservation() {
    SyncNodeClientAdapter node =
        new SyncNodeClientAdapter(handler, endpoint, null, pollingEventStrategy);
    node.onStartReaderObservation();
    node.onStopReaderObservation();
  }

  @Test
  public void onStartObservation_whenPollingObservationStrategyIsProvided_shouldSendAPollingDto() {
    SyncNodeClientAdapter node =
        new SyncNodeClientAdapter(handler, endpoint, pollingEventStrategy, null);
    node.onStartPluginsObservation();
    await().atMost(5, TimeUnit.SECONDS).until(endpointMessagesHasMinSize(1));
    MessageDto msg = endpoint.messages.get(0);
    checkEventDto(msg, MessageDto.Action.CHECK_PLUGIN_EVENT, bodyPolling);
  }

  @Test
  public void
      onStartObservation_whenLongPollingObservationStrategyIsProvided_shouldSendALongPollingDto() {
    SyncNodeClientAdapter node =
        new SyncNodeClientAdapter(handler, endpointLongPolling, longPollingEventStrategy, null);
    node.onStartPluginsObservation();
    await().atMost(5, TimeUnit.SECONDS).until(endpointLongPollingMessagesHasAtLeastOneElement());
    MessageDto msg = endpointLongPolling.messages.get(0);
    checkEventDto(msg, MessageDto.Action.CHECK_PLUGIN_EVENT, bodyLongPolling);
  }

  @Test
  public void
      onStartObservation_whenObservationStrategyIsProvided_shouldCallOnMessageMethodOnHandler() {
    SyncNodeClientAdapter node =
        new SyncNodeClientAdapter(handler, endpoint, pollingEventStrategy, null);
    node.onStartPluginsObservation();
    await().atMost(5, TimeUnit.SECONDS).until(endpointMessagesHasMinSize(1));
    verify(handler).onMessage(response);
  }

  @Test
  public void
      onStartObservation_whenObservationButHandlerInError_shouldInterruptObserverAndThrowException() {
    SyncNodeClientAdapter node =
        new SyncNodeClientAdapter(handlerError, endpoint, pollingEventStrategy, null);
    node.onStartPluginsObservation();
    await().atMost(5, TimeUnit.SECONDS).until(handlerErrorOccurred());
    await().atMost(2, TimeUnit.SECONDS);
    assertThat(endpoint.messages).hasSize(1);
  }

  @Test
  public void onStartObservation_whenObservationButEndpointInError_shouldRetryUntilNoError() {
    SyncNodeClientAdapter node =
        new SyncNodeClientAdapter(handler, endpointError, pollingEventStrategy, null);
    node.onStartPluginsObservation();
    await().atMost(10, TimeUnit.SECONDS).until(endpointErrorMessagesHasAtLeastTwoElements());
    assertThat(endpointError.messages).hasSize(2);
  }

  @Test
  public void openSession_shouldDoNothing() {
    SyncNodeClientAdapter node = new SyncNodeClientAdapter(handler, endpoint, null, null);
    node.openSession(SESSION_ID);
    verifyNoInteractions(handler);
    assertThat(endpoint.messages).isEmpty();
  }

  @Test
  public void sendRequest_shouldCallEndpointAndReturnEndpointResponse() {
    SyncNodeClientAdapter node = new SyncNodeClientAdapter(handler, endpoint, null, null);
    MessageDto result = node.sendRequest(msg);
    assertThat(endpoint.messages).hasSize(1);
    assertThat(endpoint.messages.get(0)).isSameAs(msg);
    assertThat(endpoint.messages.get(0)).isEqualToComparingFieldByField(msg);
    assertThat(result).isSameAs(response).isEqualToComparingFieldByField(response);
    verifyNoInteractions(handler);
  }

  @Test
  public void sendMessage_shouldCallEndpoint() {
    SyncNodeClientAdapter node = new SyncNodeClientAdapter(handler, endpoint, null, null);
    node.sendMessage(msg);
    assertThat(endpoint.messages).hasSize(1);
    assertThat(endpoint.messages.get(0)).isSameAs(msg);
    assertThat(endpoint.messages.get(0)).isEqualToComparingFieldByField(msg);
    verifyNoInteractions(handler);
  }

  @Test
  public void closeSession_shouldDoNothing() {
    SyncNodeClientAdapter node = new SyncNodeClientAdapter(handler, endpoint, null, null);
    node.closeSession(SESSION_ID);
    verifyNoInteractions(handler);
    assertThat(endpoint.messages).isEmpty();
  }
}
