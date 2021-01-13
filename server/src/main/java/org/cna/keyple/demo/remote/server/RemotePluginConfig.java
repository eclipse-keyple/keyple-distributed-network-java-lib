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

import java.util.concurrent.Executors;
import javax.enterprise.context.ApplicationScoped;

import io.quarkus.runtime.Startup;
import org.eclipse.keyple.calypso.transaction.sammanager.SamResourceManager;
import org.eclipse.keyple.calypso.transaction.sammanager.SamResourceManagerFactory;
import org.eclipse.keyple.core.service.Plugin;
import org.eclipse.keyple.core.service.SmartCardService;
import org.eclipse.keyple.core.service.event.PluginObservationExceptionHandler;
import org.eclipse.keyple.core.service.event.ReaderObservationExceptionHandler;
import org.eclipse.keyple.distributed.impl.RemotePluginServerFactory;
import org.eclipse.keyple.plugin.pcsc.PcscPluginFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Server side application. */
@ApplicationScoped
@Startup
public class RemotePluginConfig {

  private static String samReaderFilter = ".*(Cherry TC|SCM Microsystems|Identive|HID|Generic).*";
  private static final Logger logger = LoggerFactory.getLogger(RemotePluginConfig.class);

  private RemoteServiceObserver remoteServiceObserver;
  private SamResourceManager resourceManager;

  public RemotePluginConfig(RemoteServiceObserver remoteServiceObserver){
    this.remoteServiceObserver = remoteServiceObserver;
    init();
  }
  /**
   * Initialize the server components :
   *
   * <ul>
   *   <li> Initialize the {@link SamResourceManager} with {@link PcscPlugin}
   *   <li> Initialize the {@link RemotePluginServer} with a sync node and attach an observer
   *   that contains all the business logic.
   * </ul>
   */
  private void init() {
    logger.info("Remote Plugin init...");

    //init Sam Resource
    initPcscSamResourceManager();

    //init Remote Server Plugin
    initRemotePlugin();
  }

  private void initRemotePlugin(){

    // Init the remote plugin factory with a sync node and a remote plugin observer.
    RemotePluginServerFactory factory =
            RemotePluginServerFactory.builder()
                    .withDefaultPluginName()
                    .withSyncNode()
                    .withPluginObserver(remoteServiceObserver)
                    .usingEventNotificationPool(
                            Executors.newCachedThreadPool(r -> new Thread(r, "server-pool")))
                    .build();

    // Register the remote plugin to the smart card service using the factory.
    SmartCardService.getInstance().registerPlugin(factory);
  }

  private void initPcscSamResourceManager(){
    // Registers the plugin to the smart card service.
    Plugin plugin = SmartCardService.getInstance().registerPlugin(new PcscPluginFactory(new PluginObservationExceptionHandler() {
      @Override
      public void onPluginObservationError(String pluginName, Throwable e) {
        logger.error("error in reader observer pluginName:{}, error:{}", pluginName, e.getMessage());
      }
    }, new ReaderObservationExceptionHandler() {
      @Override
      public void onReaderObservationError(String pluginName, String readerName, Throwable e) {
        logger.error("error in reader observer pluginName:{}, readerName:{}, error:{}", pluginName, readerName, e.getMessage());
      }
    }));

    if (plugin.getReaders().size() == 0) {
      throw new IllegalStateException(
              "For the matter of this example, we expect at least one PCSC reader to be connected");
    }

    resourceManager = SamResourceManagerFactory.instantiate(plugin, samReaderFilter);
  }
}
