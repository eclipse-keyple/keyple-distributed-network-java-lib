/********************************************************************************
 * Copyright (c) 2020 Calypso Networks Association https://www.calypsonet-asso.org/
 ********************************************************************************/
package org.cna.keyple.demo.sale.android.nfc.slave.data.http.client.slave;

import org.cna.keyple.demo.sale.android.nfc.slave.data.http.client.ticketing.TIcketingClientFactory;
import org.cna.keyple.demo.sale.android.nfc.slave.data.http.client.ticketing.TicketingClient;
import org.cna.keyple.demo.sale.android.nfc.slave.data.http.common.ReaderState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import retrofit2.Call;
import retrofit2.Callback;

public class TicketingAPI {

    private static final Logger logger = LoggerFactory.getLogger(TicketingAPI.class);

    TicketingClient client;

    public TicketingAPI(String baseUrl) {
        this.client = TIcketingClientFactory.getRetrofitClient(baseUrl);
    }

    public void loadTicket(String nativeReaderName, String slaveId, String transactionId,
            Integer ticketNumber, Callback<ReaderState> callback) {

        logger.info("Load ticket for {} {} {} {}", nativeReaderName, slaveId, transactionId,
                ticketNumber);
        Call<ReaderState> call =
                client.loadTicket(nativeReaderName, slaveId, transactionId, ticketNumber);
        call.enqueue(callback);
    }


}
