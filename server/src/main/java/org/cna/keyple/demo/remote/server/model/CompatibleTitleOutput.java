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
package org.cna.keyple.demo.remote.server.model;

import java.util.List;

/**
 * Output of compatible title endpoint
 */
public class CompatibleTitleOutput {

  private Boolean isSamReady;
  private List<Title> compatibleTitles;

  public List<Title> getCompatibleTitles() {
    return compatibleTitles;
  }

  public CompatibleTitleOutput setCompatibleTitles(List<Title> compatibleTitles) {
    this.compatibleTitles = compatibleTitles;
    return this;
  }

  public Boolean isSamReady() {
    return isSamReady;
  }

  public CompatibleTitleOutput setSamReady(Boolean isSamReady) {
    isSamReady = isSamReady;
    return this;
  }
}
