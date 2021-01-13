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
package org.cna.keyple.demo.remote.server.dto;

import org.cna.keyple.demo.sale.data.model.ContractStructureDto;

import java.util.List;

/**
 * Output of Compatible Contract endpoint
 */
public class CompatibleContractOutput {

  private Boolean isSamReady;
  private List<ContractStructureDto> compatibleTitles;

  public List<ContractStructureDto> getCompatibleTitles() {
    return compatibleTitles;
  }

  public CompatibleContractOutput setCompatibleTitles(List<ContractStructureDto> compatibleTitles) {
    this.compatibleTitles = compatibleTitles;
    return this;
  }

  public Boolean isSamReady() {
    return isSamReady;
  }

  public CompatibleContractOutput setSamReady(Boolean isSamReady) {
    isSamReady = isSamReady;
    return this;
  }
}
