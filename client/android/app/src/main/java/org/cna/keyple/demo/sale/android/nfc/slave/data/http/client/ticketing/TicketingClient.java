/********************************************************************************
 * Copyright (c) 2020 Calypso Networks Association https://www.calypsonet-asso.org/
 ********************************************************************************/
package org.cna.keyple.demo.sale.android.nfc.slave.data.http.client.ticketing;

import org.cna.keyple.demo.sale.android.nfc.slave.data.http.common.ReaderState;

import retrofit2.Call;
import retrofit2.http.Headers;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface TicketingClient {

    @Headers({"Accept: application/json", "Content-Type: application/json; charset=UTF-8"})
    @PUT("ticketing")
    Call<ReaderState> loadTicket(@Query("nativeReaderName") String nativeReaderName,
                                 @Query("slaveId") String slaveId, @Query("transactionId") String transactionId,
                                 @Query("ticketNumber") Integer ticketNumber);

}
