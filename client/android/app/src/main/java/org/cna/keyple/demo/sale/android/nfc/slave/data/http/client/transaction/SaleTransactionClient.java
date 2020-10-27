/********************************************************************************
 * Copyright (c) 2020 Calypso Networks Association https://www.calypsonet-asso.org/
 ********************************************************************************/
package org.cna.keyple.demo.sale.android.nfc.slave.data.http.client.transaction;

import org.cna.keyple.demo.sale.android.nfc.slave.data.http.common.SaleTransaction;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface SaleTransactionClient {


    /* PUT */

    @Headers({"Accept: application/json", "Content-Type: application/json; charset=UTF-8"})
    @PUT("saleTransaction")
    Call<SaleTransaction> update(@Query("transactionId") String transactionId,
                                 @Query("status") String status, @Query("ticketNumber") Integer ticketNumber);

    /* GET */

    @Headers({"Accept: application/json", "Content-Type: application/json; charset=UTF-8"})
    @GET("saleTransaction")
    Call<List<SaleTransaction>> findAll();

    @Headers({"Accept: application/json", "Content-Type: application/json; charset=UTF-8"})
    @GET("saleTransaction")
    Call<SaleTransaction> getById(@Query("transactionId") String transactionId);

    @Headers({"Accept: application/json", "Content-Type: application/json; charset=UTF-8"})
    @GET("saleTransaction")
    Call<SaleTransaction> getByNativeReaderName(@Query("nativeReaderName") String nativeReaderName,
                                                @Query("slaveNodeId") String slaveNodeId);

    /* POST */

    @Headers({"Accept: application/json", "Content-Type: application/json; charset=UTF-8"})
    @POST("saleTransaction")
    Call<SaleTransaction> create(@Body SaleTransaction newSaleTransaction);

    /* DELETE */
    @DELETE("saleTransaction")
    Call<String> delete(@Query("transactionId") String transactionId,
                        @Query("readerName") String readerName, @Query("slaveNodeId") String slaveNodeId);


}
