/********************************************************************************
 * Copyright (c) 2020 Calypso Networks Association https://www.calypsonet-asso.org/
 ********************************************************************************/
package org.cna.keyple.demo.sale.android.nfc.slave.viewModels;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Observer;
import javax.inject.Inject;
import org.cna.keyple.demo.sale.android.nfc.slave.data.KeypleSlaveAPI;
import org.cna.keyple.demo.sale.android.nfc.slave.data.http.common.ReaderState;
import org.cna.keyple.demo.sale.android.nfc.slave.data.method.ConnectReaderAsyncTask;
import org.cna.keyple.demo.sale.android.nfc.slave.data.method.DisconnectAllReaderAsyncTask;
import org.cna.keyple.demo.sale.android.nfc.slave.data.nfc.AndroidNfcKeypleService;
import org.cna.keyple.demo.sale.android.nfc.slave.data.wizway.WizwaySlaveService;
import org.cna.keyple.demo.sale.android.nfc.slave.di.scopes.AppScoped;
import org.cna.keyple.demo.sale.android.nfc.slave.rx.SchedulerProvider;
import org.cna.keyple.demo.sale.android.nfc.slave.util.LiveEvent;
import org.eclipse.keyple.core.seproxy.SeReader;
import org.eclipse.keyple.core.seproxy.event.ObservablePlugin;
import org.eclipse.keyple.core.seproxy.event.ObservableReader;
import org.eclipse.keyple.core.seproxy.event.PluginEvent;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

@AppScoped
public class ConnectionStatusViewModel extends ViewModel {
    private final static String TAG = ConnectionStatusViewModel.class.getSimpleName();

    private final SchedulerProvider schedulerProvider;

    private final CompositeDisposable disposables = new CompositeDisposable();

    private final LiveEvent<Boolean> serverConnection = new LiveEvent<>();

    private final LiveEvent<Boolean> deviceConnection = new LiveEvent<>();

    @NonNull
    private final KeypleSlaveAPI keypleSlaveAPI;

    @Inject
    public ConnectionStatusViewModel(@NonNull KeypleSlaveAPI keypleSlaveAPI,
            SchedulerProvider provider) {
        this.schedulerProvider = provider;
        this.keypleSlaveAPI = keypleSlaveAPI;

//        /**
//         * Observe Plugin for wizway succesful connection Connect isDeviceConnected field to
//         * READER_CONNECTED event of plugin
//         */
//        AndroidWizwayPlugin wizwayPlugin =
//                keypleSlaveAPI.getWizwayKeypleService().getWizwayPlugin();
//
//        wizwayPlugin.addObserver(new ObservablePlugin.PluginObserver() {
//            @Override
//            public void update(PluginEvent event) {
//                Timber.d( "DeviceReaderViewModel - Plugin Event : " + event.getPluginName() + " - "
//                        + event.getEventType());
//                switch (event.getEventType()) {
//                    case READER_CONNECTED:
//                        disposables.add(Observable.just(true).observeOn(schedulerProvider.ui())
//                                .subscribeOn(schedulerProvider.ui())
//                                .subscribe(deviceConnection::setValue));
//                    case READER_DISCONNECTED:
//                        if (wizwayPlugin.getReaders().size() == 0) {
//                            disposables.add(Observable.just(false).observeOn(schedulerProvider.ui())
//                                    .subscribeOn(schedulerProvider.ui())
//                                    .subscribe(deviceConnection::setValue));
//                        }
//
//
//                }
//            }
//        });

    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear();
    }

    public LiveEvent<Boolean> getServerConnection() {
        return serverConnection;
    }

    public LiveEvent<Boolean> getDeviceConnection() {
        return deviceConnection;
    }

    public void initStatus() {

        /*
         * Presume polling will work
         */
        // serverConnection.setValue(false);

        deviceConnection.setValue(false);

        /*
         * disposables.add(Observable.just(true) .observeOn(schedulerProvider.ui())
         * .subscribeOn(schedulerProvider.ui()) .subscribe(serverConnection::setValue));
         */
        disposables.add(Observable.just(false).observeOn(schedulerProvider.ui())
                .subscribeOn(schedulerProvider.ui()).subscribe(deviceConnection::setValue));
    }


    /**
     * Connect NFC to Master, attach observer
     * 
     * @param obs
     */
    public void NfcReaderConnect(ObservableReader.ReaderObserver obs) {
        /**
         * Connect NFC Reader to Master
         */
        this.connectReaderToMaster(keypleSlaveAPI.getAndroidNfcKeypleService().getReader(),
                new Callback<ReaderState>() {
                    @Override
                    public void onResponse(Call<ReaderState> call, Response<ReaderState> response) {
                        /*
                         * Attach PO logic to connected Reader
                         */
                        ((ObservableReader) keypleSlaveAPI.getAndroidNfcKeypleService().getReader())
                                .addObserver(obs);

                        disposables.add(Observable.just(true).observeOn(schedulerProvider.ui())
                                .subscribeOn(schedulerProvider.ui())
                                .subscribe(serverConnection::setValue));
                    }

                    @Override
                    public void onFailure(Call<ReaderState> call, Throwable t) {
                        disposables.add(Observable.just(false).observeOn(schedulerProvider.ui())
                                .subscribeOn(schedulerProvider.ui())
                                .subscribe(serverConnection::setValue));
                    }
                });
    }

    /**
     * Connect Local Reader to Server Attach observer
     */
    public void connectReaderToMaster(SeReader reader, Callback<ReaderState> readerStateCallback) {

        /*
         * Disconnect first
         */
        /*
         * String[] readerNames ={reader.getName()}; new
         * DisconnectAllReaderAsyncTask(keypleSlaveAPI,readerNames, () -> { //is called Log.i(TAG,
         * "Disconnected : " + Arrays.toString(readerNames)); }).execute();
         */
        if (reader == null) {
            Log.e(TAG, "reader is null, cancel connectReaderToMaster()");
            return;
        }

//        /*
//         * Connect
//         */
//        Map<String, String> options = new HashMap<String, String>();
//        if (reader instanceof AndroidWizwayReader) {
//            options.put(WizwaySlaveService.OPTION_SELECTION_EXPLICIT, "true");
//        }
//        try {
//            Timber.d( "Connecting Reader : " + reader.getName());
//            new ConnectReaderAsyncTask(keypleSlaveAPI, reader, options,
//                    new Callback<ReaderState>() {
//                        @Override
//                        public void onResponse(Call<ReaderState> call,
//                                Response<ReaderState> response) {
//                            if (response.body() == null) {
//                                Log.e(TAG, ".. connection failed");
//                                readerStateCallback.onResponse(call, response);
//                            } else {
//                                Log.e(TAG, ".. connection successful : "
//                                        + response.body().getReaderStatus());
//                                // callback to the user
//                                readerStateCallback.onResponse(call, response);
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Call<ReaderState> call, Throwable t) {
//                            Log.e(TAG, "Failure ConnectReaderAsyncTask");
//                            // callback to the user
//                            readerStateCallback.onFailure(call, t);
//                        }
//                    }).execute();

 //       } catch (IllegalArgumentException e) {
 //           Log.e(TAG, e.toString());
 //       }
    }

    /*
     * Disconnect all readers
     */
    public void disconnectAll() {
        String[] readerNames =
                {AndroidNfcKeypleService.NFC_READER_NAME, "AndroidWizwayReader10015004"};
        new DisconnectAllReaderAsyncTask(keypleSlaveAPI, readerNames, () -> {
            // is called
            Log.i(TAG, "disconnected All : " + Arrays.toString(readerNames));
            disposables.add(Observable.just(false).observeOn(schedulerProvider.ui())
                    .subscribeOn(schedulerProvider.ui()).subscribe(serverConnection::setValue));
        }).execute();
    }


    /*
     * Disconnect a reader
     */
    public void NfcReaderDisconnect() {
        disconnectReader(AndroidNfcKeypleService.NFC_READER_NAME);
    }

    /*
     * Disconnect a reader
     */
    public void WizwayReaderDisconnect() {
        disconnectReader("AndroidWizwayReader10015004");
    }

    /*
     * Disconnect a reader
     */
    public void disconnectReader(String readerName) {
        String[] readerNames = {readerName};
        new DisconnectAllReaderAsyncTask(keypleSlaveAPI, readerNames, () -> {
            Log.i(TAG, "disconnected " + Arrays.toString(readerNames));
        }).execute();
    }

    public void startPolling() {
        Timber.d( "startPolling");

        keypleSlaveAPI.startPolling(new Observer() {
            @Override
            public void update(java.util.Observable observable, Object o) {
                Boolean isPolling = (Boolean) o;
                Timber.d( "received polling event : " + o);
                if (!isPolling) {
                    // polling stopped working
                    disposables.add(Observable.just(false).observeOn(schedulerProvider.ui())
                            .subscribeOn(schedulerProvider.ui())
                            .subscribe(serverConnection::setValue));
                } else {
                    // polling back on working
                    disposables.add(Observable.just(true).observeOn(schedulerProvider.ui())
                            .subscribeOn(schedulerProvider.ui())
                            .subscribe(serverConnection::setValue));
                }

            }
        });
    }

    public void stopPolling() {
        keypleSlaveAPI.stopPolling();
    }

    /**
     * Connect to wizway device
     */
    public void WizwayDeviceConnect(Context context) {
        keypleSlaveAPI.getWizwayKeypleService().connectDevice(context);
    }

    /**
     * Connect to wizway device
     */
    public void WizwayDeviceDisconnect(Context context) {
        keypleSlaveAPI.getWizwayKeypleService().disconnectDevice(context);
    }

}
