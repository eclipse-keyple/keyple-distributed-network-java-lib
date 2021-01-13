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
package org.cna.keyple.demo.remote.server;

import io.quarkus.runtime.Startup;
import org.cna.keyple.demo.remote.server.model.CompatibleTitleOutput;
import org.cna.keyple.demo.remote.server.model.Title;
import org.cna.keyple.demo.remote.server.model.WriteTitleOutput;
import org.eclipse.keyple.calypso.transaction.CalypsoPo;
import org.eclipse.keyple.core.service.SmartCardService;
import org.eclipse.keyple.core.service.event.ObservablePlugin;
import org.eclipse.keyple.core.service.event.PluginEvent;
import org.eclipse.keyple.distributed.RemotePluginServer;
import org.eclipse.keyple.distributed.RemoteReaderServer;
import org.cna.keyple.demo.remote.server.model.CompatibleTitleInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.Arrays;
import java.util.List;

/**
 *{@link RemotePluginServer} observer.
 *
 * <p>It contains the business logic of the remote service execution.
 * <ul>
 *     <li>GET_COMPATIBLE_TITLE : returns the list of compatible title with the calypsoPo inserted</li>
 * </ul>
 *
 */
@ApplicationScoped
@Startup
public class RemoteServiceObserver implements ObservablePlugin.PluginObserver {

  private static final Logger logger = LoggerFactory.getLogger(RemoteServiceObserver.class);

  /** {@inheritDoc} */
  @Override
  public void update(PluginEvent event) {

    // For a RemotePluginServer, the events can only be of type READER_CONNECTED.
    // So there is no need to analyze the event type.
    logger.info(
        "Event received {} {} {}",
        event.getEventType(),
        event.getPluginName(),
        event.getReaderNames().first());

    // Retrieves the remote plugin using the plugin name contains in the event.
    RemotePluginServer plugin =
        (RemotePluginServer) SmartCardService.getInstance().getPlugin(event.getPluginName());

    // Retrieves the name of the remote reader using the first reader name contains in the event.
    // Note that for a RemotePluginServer, there can be only one reader per event.
    String readerName = event.getReaderNames().first();

    // Retrieves the remote reader from the plugin using the reader name.
    RemoteReaderServer reader = plugin.getReader(readerName);

    // Analyses the Service ID contains in the reader to find which business service to execute.
    // The Service ID was specified by the client when executing the remote service.
    Object userOutputData;
    if ("GET_COMPATIBLE_TITLE".equals(reader.getServiceId())) {

      // Executes the business service using the remote reader.
      userOutputData = getCompatibleTitle(reader);

    } else if ("WRITE_TITLE".equals(reader.getServiceId())) {

      // Executes the business service using the remote reader.
      userOutputData = writeTitle(reader);

    } else{
      throw new IllegalArgumentException("Service ID not recognized");
    }

    // Terminates the business service by providing the reader name and the optional output data.
    plugin.terminateService(readerName, userOutputData);
  }

  /**
   *
   * @param reader The remote reader on where to execute the business logic.
   * @return a nullable reference to the user output data to transmit to the client.
   */
  private CompatibleTitleOutput getCompatibleTitle(RemoteReaderServer reader) {

    /*
     * Retrieves the compatibleTitleInput and initial calypsoPO specified by the client when executing the remote service.
     */
    CompatibleTitleInput compatibleTitleInput = reader.getUserInputData(CompatibleTitleInput.class);
    CalypsoPo calypsoPo = reader.getInitialCardContent(CalypsoPo.class);

    //TODO
    return new CompatibleTitleOutput();
  }

  /**
   *
   * @param reader The remote reader on where to execute the business logic.
   * @return a nullable reference to the user output data to transmit to the client.
   */
  private WriteTitleOutput writeTitle(RemoteReaderServer reader) {

    /*
     * Retrieves the userInputData and initial calypsoPO specified by the client when executing the remote service.
     */
    CompatibleTitleInput userInputData = reader.getUserInputData(CompatibleTitleInput.class);
    CalypsoPo calypsoPo = reader.getInitialCardContent(CalypsoPo.class);

    //TODO
    return new WriteTitleOutput();
  }
}
