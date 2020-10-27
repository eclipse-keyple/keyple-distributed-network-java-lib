/********************************************************************************
 * Copyright (c) 2020 Calypso Networks Association https://www.calypsonet-asso.org/
 ********************************************************************************/
package org.cna.keyple.demo.sale.android.nfc.slave.data.apiEndpoints;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.GET;

public interface PingApiEndpoint {

    @GET("/saleTransaction")
    Observable<Response<String>> ping();
}
