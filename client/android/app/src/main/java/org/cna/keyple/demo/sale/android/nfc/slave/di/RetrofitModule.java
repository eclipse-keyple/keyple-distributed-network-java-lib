/********************************************************************************
 * Copyright (c) 2020 Calypso Networks Association https://www.calypsonet-asso.org/
 ********************************************************************************/
package org.cna.keyple.demo.sale.android.nfc.slave.di;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.cna.keyple.demo.sale.android.nfc.slave.data.SharedPrefData;
import org.cna.keyple.demo.sale.android.nfc.slave.data.apiEndpoints.PingApiEndpoint;
import org.cna.keyple.demo.sale.android.nfc.slave.data.remote.transport.wspolling.client_retrofit.WsPollingFactory;
import org.cna.keyple.demo.sale.android.nfc.slave.di.scopes.AppScoped;
import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

@Module
public class RetrofitModule {

    private static final String CLIENT_NODE_ID = "androidDevice" + new Date().getTime();
    private static final String MASTER_NODE_ID = "server1";


    @Provides
    @AppScoped
    RxJava2CallAdapterFactory getRxJava2CallAdapterFactory() {
        return RxJava2CallAdapterFactory.create();
    }

    @Provides
    @AppScoped
    GsonConverterFactory getGsonConverterFactory() {
        return GsonConverterFactory.create();
    }

    @Provides
    @AppScoped
    ScalarsConverterFactory getScalarsConverterFactory() {
        return ScalarsConverterFactory.create();
    }

    @Provides
    @AppScoped
    OkHttpClient getOkHttpClient(HttpLoggingInterceptor httpLoggingInterceptor) {
        return new OkHttpClient.Builder().connectTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(httpLoggingInterceptor).build();
    }

    @Provides
    @AppScoped
    HttpLoggingInterceptor getHttpLoggingInterceptor() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return httpLoggingInterceptor;
    }

    @Provides
    @AppScoped
    PingApiEndpoint getApiInterface(Retrofit retroFit) {
        return retroFit.create(PingApiEndpoint.class);
    }

    @Provides
    @AppScoped
    Retrofit getRetrofit(OkHttpClient okHttpClient, RxJava2CallAdapterFactory rxFactory,
            GsonConverterFactory gsonFactory, ScalarsConverterFactory scalarsConverterFactory,
            SharedPrefData sharedPrefData) {
        String serverIp = sharedPrefData.loadServerIP();
        Integer serverPort = sharedPrefData.loadServerPort();
        String protocol = sharedPrefData.loadServerProtocol();

        return new Retrofit.Builder().baseUrl(protocol + serverIp + ":" + serverPort)
                .addCallAdapterFactory(rxFactory).addConverterFactory(gsonFactory)
                .addConverterFactory(scalarsConverterFactory).client(okHttpClient).build();
    }

    @Provides
    @AppScoped
    WsPollingFactory getWsPollingRetrofitFactory(SharedPrefData sharedPrefData) {
        String serverIp = sharedPrefData.loadServerIP();
        Integer serverPort = sharedPrefData.loadServerPort();
        String protocol = sharedPrefData.loadServerProtocol();

        return new WsPollingFactory(MASTER_NODE_ID, protocol, serverIp, serverPort);
    }

    @Provides
    @AppScoped
    String getSlaveNodeId() {
        return CLIENT_NODE_ID;
    }
}
