/********************************************************************************
 * Copyright (c) 2020 Calypso Networks Association https://www.calypsonet-asso.org/
 ********************************************************************************/
package org.cna.keyple.demo.sale.android.nfc.slave.data.http.common;

import org.cna.keyple.demo.sale.android.nfc.slave.data.ticketing.CardContent;
import org.cna.keyple.demo.sale.android.nfc.slave.data.ticketing.ITicketingSession;
import org.eclipse.keyple.core.util.ByteArrayUtil;
import org.eclipse.keyple.plugin.remotese.pluginse.VirtualReader;

public class ReaderState {

    public static String READER_CONFIGURATION_OK = "READER_CONFIGURATION_OK";
    public static String READER_CONFIGURATION_KO = "READER_CONFIGURATION_KO";
    public static String READER_DISCONNECTED = "READER_DISCONNECTED";
    public static String READER_IO_ERROR = "READER_IO_ERROR";
    public static String SAM_K0 = "SAM_KO";
    public static String SAM_OK = "SAM_OK";

    String readerStatus;
    String sessionId;
    CardContent cardContent;
    String currentPoSN;
    String poTypeName;
    String poReaderName;
    // String poNativeReaderType; not supported anymore, use poReaderName
    SaleTransaction saleTransaction;

    public ReaderState(ITicketingSession session, SaleTransaction saleTransaction) {
        if (session != null) {
            readerStatus = READER_CONFIGURATION_OK;
            cardContent = session.getCardContent();
            poTypeName = session.getPoTypeName();
            currentPoSN = ByteArrayUtil.toHex(session.getCardContent().getSerialNumber());
            poReaderName = ((VirtualReader) session.getSeReader()).getNativeReaderName();
        } else {
            readerStatus = READER_CONFIGURATION_KO;
        }
        this.saleTransaction = saleTransaction;
    }

    public ReaderState(ITicketingSession session, SaleTransaction saleTransaction,
            String readerStatus) {
        this(session, saleTransaction);
        this.readerStatus = readerStatus;
    }

    public ReaderState(String readerStatus) {
        this(null, null);
        this.readerStatus = readerStatus;
    }


    public String getReaderStatus() {
        return readerStatus;
    }

    public void setReaderStatus(String readerStatus) {
        this.readerStatus = readerStatus;
    }

    public CardContent getCardContent() {
        return cardContent;
    }

    public void setCardContent(CardContent cardContent) {
        this.cardContent = cardContent;
    }

    public String getCurrentPoSN() {
        return currentPoSN;
    }

    public void setCurrentPoSN(String currentPoSN) {
        this.currentPoSN = currentPoSN;
    }

    public String getPoTypeName() {
        return poTypeName;
    }

    public void setPoTypeName(String poTypeName) {
        this.poTypeName = poTypeName;
    }

    public SaleTransaction getSaleTransaction() {
        return saleTransaction;
    }

    public void setSaleTransaction(SaleTransaction saleTransaction) {
        this.saleTransaction = saleTransaction;
    }

    @Override
    public String toString() {
        return "readerStatus:" + (readerStatus != null ? readerStatus : "null") + " - "
                + "currentPoSn:" + (currentPoSN != null ? currentPoSN : "null") + " - "
                + "poTypeName:" + (poTypeName != null ? poTypeName : "null") + " - "
                + "cardContent:" + (cardContent != null ? cardContent.toString() : ":null") + "-"
                + "saleTransaction:"
                + (saleTransaction != null ? saleTransaction.toString() : ":null");
    }
}
