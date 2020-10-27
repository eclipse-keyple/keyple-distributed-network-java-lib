/********************************************************************************
 * Copyright (c) 2020 Calypso Networks Association https://www.calypsonet-asso.org/
 ********************************************************************************/
package org.cna.keyple.demo.sale.android.nfc.slave.data.model;

public class CardReaderResponse {

    public final Status status;

    public final Integer ticketsNumber;

    public final String cardType, lastValidation, seasonPassExpiryDate;

    public CardReaderResponse(Status status, Integer ticketsNumber, String cardType,
            String lastValidation, String seasonPassExpiryDate) {
        this.status = status;
        this.ticketsNumber = ticketsNumber;
        this.cardType = cardType;
        this.lastValidation = lastValidation;
        this.seasonPassExpiryDate = seasonPassExpiryDate;
    }
}
