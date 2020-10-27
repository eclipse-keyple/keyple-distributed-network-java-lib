/********************************************************************************
 * Copyright (c) 2020 Calypso Networks Association https://www.calypsonet-asso.org/
 ********************************************************************************/
package org.cna.keyple.demo.sale.android.nfc.slave.data.http.common;

import java.util.Date;

public class SaleTransaction {

    public static String WAIT_FOR_PAYMENT = "WAIT_FOR_PAYMENT";
    public static String WAIT_FOR_CARD_TO_LOAD = "WAIT_FOR_CARD_TO_LOAD";
    public static String WRONG_CARD_TO_LOAD_TRY_AGAIN = "WRONG_CARD_TO_LOAD_TRY_AGAIN";
    public static String LOAD_COMPLETE = "LOAD_COMPLETE";

    String id;
    String status;
    byte[] serialNumber;
    Integer ticketNumber; // ticket number to load
    String poReader;

    public SaleTransaction(String poReader, byte[] serialNumber) {
        this.poReader = poReader;
        ticketNumber = 0;
        this.id = poReader + new Date().getTime();
        this.status = WAIT_FOR_PAYMENT;
        this.serialNumber = serialNumber;
    }

    public String getPoReader() {
        return poReader;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(Integer ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    /*
     * public CardContent getCardContent() { return cardContent; }
     */
    /*
     * public void setCardContent(CardContent cardContent) { this.cardContent = cardContent; }
     */

    public byte[] getSerialNumber() {
        return serialNumber;
    }

    @Override
    public String toString() {
        return id + " - " + status + " - ticket: " + ticketNumber + " - poReader:" + poReader;
    }
}
