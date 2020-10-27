/********************************************************************************
 * Copyright (c) 2020 Calypso Networks Association https://www.calypsonet-asso.org/
 ********************************************************************************/
package org.cna.keyple.demo.sale.android.nfc.slave.data.http.client.slave;

import org.cna.keyple.demo.sale.android.nfc.slave.data.http.client.transaction.SaleTransactionClient;
import org.cna.keyple.demo.sale.android.nfc.slave.data.http.client.transaction.SaleTransactionClientFactory;
import org.cna.keyple.demo.sale.android.nfc.slave.data.http.common.SaleTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SaleTransactionAPI {

    private static final Logger logger = LoggerFactory.getLogger(SaleTransactionAPI.class);

    SaleTransactionClient client;

    public SaleTransactionAPI(String baseUrl) {
        this.client = SaleTransactionClientFactory.getRetrofitClient(baseUrl);
    }

    public void payTicket(String transactionId, Integer ticketNumber) {

        Call<SaleTransaction> call =
                client.update(transactionId, SaleTransaction.WAIT_FOR_CARD_TO_LOAD, ticketNumber);
        call.enqueue(new Callback<SaleTransaction>() {

            @Override
            public void onResponse(Call<SaleTransaction> call, Response<SaleTransaction> response) {
                logger.debug("SaleTransactionResponse:" + response.toString());
            }

            @Override
            public void onFailure(Call<SaleTransaction> call, Throwable t) {
                logger.error("SaleTransactionResponse:" + t.toString());
            }
        });
    }

    public SaleTransactionClient getClient() {
        return client;
    }

}
