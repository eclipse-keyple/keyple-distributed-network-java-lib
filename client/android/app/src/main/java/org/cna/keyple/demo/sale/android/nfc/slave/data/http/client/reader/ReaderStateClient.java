/********************************************************************************
 * Copyright (c) 2020 Calypso Networks Association https://www.calypsonet-asso.org/
 ********************************************************************************/
package org.cna.keyple.demo.sale.android.nfc.slave.data.http.client.reader;

import org.cna.keyple.demo.sale.android.nfc.slave.data.http.common.ReaderState;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface ReaderStateClient {

    @Headers({"Accept: application/json", "Content-Type: application/json; charset=UTF-8"})
    @GET("readerState")
    Call<ReaderState> getReaderState(@Query("slaveNodeId") String slaveNodeId,
                                     @Query("nativeReaderName") String nativeReaderName,
                                     @Query("waitForUpdate") Boolean waitForUpdate);

}
