/********************************************************************************
 * Copyright (c) 2020 Calypso Networks Association https://www.calypsonet-asso.org/
 ********************************************************************************/
package org.cna.keyple.demo.sale.android.nfc.slave.data;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Observer;
import javax.inject.Inject;

import org.cna.keyple.demo.sale.android.nfc.slave.data.http.client.reader.ReaderStateClient;
import org.cna.keyple.demo.sale.android.nfc.slave.data.http.client.reader.ReaderStateClientFactory;
import org.cna.keyple.demo.sale.android.nfc.slave.data.http.client.slave.ClientSlaveAPI;
import org.cna.keyple.demo.sale.android.nfc.slave.data.http.client.slave.SaleTransactionAPI;
import org.cna.keyple.demo.sale.android.nfc.slave.data.http.client.slave.TicketingAPI;
import org.cna.keyple.demo.sale.android.nfc.slave.data.http.common.ReaderState;
import org.cna.keyple.demo.sale.android.nfc.slave.data.http.common.SaleTransaction;
import org.cna.keyple.demo.sale.android.nfc.slave.data.model.DeviceEnum;
import org.cna.keyple.demo.sale.android.nfc.slave.data.nfc.AndroidNfcKeypleService;
import org.cna.keyple.demo.sale.android.nfc.slave.data.remote.transport.wspolling.client_retrofit.WsPollingFactory;
import org.cna.keyple.demo.sale.android.nfc.slave.data.wizway.WizwaySlaveService;
import org.cna.keyple.demo.sale.android.nfc.slave.di.scopes.AppScoped;
import org.eclipse.keyple.core.seproxy.SeReader;
import org.eclipse.keyple.core.seproxy.exception.KeypleException;
import org.eclipse.keyple.core.seproxy.exception.KeypleReaderException;
import android.util.Log;
import retrofit2.Callback;

@AppScoped
public class KeypleSlaveAPI {

    private final static String TAG = KeypleSlaveAPI.class.getSimpleName();

    /*
     * Plugin configurations
     */
    // nfc
    AndroidNfcKeypleService androidNfcKeypleService;

    // wizway
    WizwaySlaveService wizwayKeypleService;


    /*
     * Web service configuration
     */

    // Web client for currentReader status
    private ReaderStateClient readerStateClient;

    // Web client to communicate with Master
    private ClientSlaveAPI clientSlaveAPI;

    // Web client for sale transaction
    private SaleTransactionAPI transactionAPI;

    // Ticketing client for explicit load of tickets
    private TicketingAPI ticketingAPI;


    // app configuratino
    @Inject
    SharedPrefData prefData;


    public final static String CALYPSO_PO_TYPE = "CALYPSO";


    /**
     * Constructor, WsPollingRetrofitFactory is generated
     * by @{@link org.cna.keyple.demo.sale.android.nfc.slave.di.RetrofitModule}
     */
    @Inject
    public KeypleSlaveAPI(WsPollingFactory transportFactory, String slaveNodeId) {
        Log.i(TAG, "Creating KeypleSlaveAPI API");


        try {
            /*
             * configure nfc plugin
             */
            androidNfcKeypleService = new AndroidNfcKeypleService();
            androidNfcKeypleService.initReader();


            /*
             * configure wizway plugin
             */
            wizwayKeypleService = new WizwaySlaveService();
            wizwayKeypleService.initReader();

            clientSlaveAPI = new ClientSlaveAPI(transportFactory,
                    transportFactory.getServerNodeId(), slaveNodeId);

            transactionAPI = new SaleTransactionAPI(transportFactory.getBaseUrl());

            ticketingAPI = new TicketingAPI(transportFactory.getBaseUrl());

            readerStateClient =
                    ReaderStateClientFactory.getRetrofitClient(transportFactory.getBaseUrl());
        } catch (KeypleReaderException e) {
            e.printStackTrace();
        } catch (KeypleException e) {
            e.printStackTrace();
        }


    }


    /*
     * Polling the server
     */
    public void startPolling(Observer isPolling) {
        clientSlaveAPI.startPolling(isPolling);
    }

    public void stopPolling() {
        clientSlaveAPI.stopPolling();
    }

    public Boolean isPolling() {
        return clientSlaveAPI.isPolling();
    }


    /*
     * Ticketing methods
     */


    /**
     * Get reader of current scenario
     * 
     * @return connected SeReader
     */
    public SeReader getCurrentReader() {
        String deviceType = prefData.loadDeviceType();
        if (DeviceEnum.CONTACTLESS_CARD.toString().equals(deviceType)) {
            return androidNfcKeypleService.getReader();
        } else if (DeviceEnum.SIM.toString().equals(deviceType)) {
            return wizwayKeypleService.getReader();
        } else {
            // deviceType is not recognized
            return null;
        }

    }


    /**
     * Connect Reader to the Master terminal Blocking method
     */
    public void connectReader(SeReader seReader, Map<String, String> options,
            Callback<ReaderState> readerStateCallback) {
        clientSlaveAPI.connectReader(seReader, options, readerStateCallback);

    }

    /**
     * Purge all connected readers from this slave Even if the readers were created in another
     * session
     *
     */
    public void disconnectAll(String[] readerNames) {
        Log.i(TAG, "disconnectAll readers : " + Arrays.toString(readerNames));
        for (String readerName : readerNames) {
            try {
                clientSlaveAPI.disconnect(readerName);
            } catch (KeypleReaderException e) {
                Log.d(TAG, "reader was not connected : " + readerName);
            }
        }
    }

    /**
     * Explicit load of tickets (must be in a explicit selection ticketing session) Use for wizway
     * 
     * @param nativeReaderName
     * @param slaveId
     * @param transactionId
     * @param ticketNumber
     * @param callback
     */
    public void chargeTicket(String nativeReaderName, String slaveId, String transactionId,
            Integer ticketNumber, Callback<ReaderState> callback) {
        ticketingAPI.loadTicket(nativeReaderName, slaveId, transactionId, ticketNumber, callback);
    }


    /*
     * Transaction Management
     */

    public void resetTransaction(String readerName, Callback<String> callback) throws IOException {
        transactionAPI.getClient().delete(null, readerName, clientSlaveAPI.getSlaveNodeId())
                .enqueue(callback);
    }

    public void getTransaction(String transactionId, Callback<SaleTransaction> callback) {
        transactionAPI.getClient().getById(transactionId).enqueue(callback);
    }



    public void getUpdatedReaderStateAsync(Callback<ReaderState> callback) throws IOException {
        Log.d(TAG, "Get update currentReader state async...");
        readerStateClient
                .getReaderState(clientSlaveAPI.getSlaveNodeId(), getCurrentReader().getName(), true)
                .enqueue(callback);
    }



    /*
     * Getters
     */

    public SaleTransactionAPI getTransactionAPI() {
        return transactionAPI;
    }

    public AndroidNfcKeypleService getAndroidNfcKeypleService() {
        return androidNfcKeypleService;
    }

    public WizwaySlaveService getWizwayKeypleService() {
        return wizwayKeypleService;
    }

    public String getSlaveId() {
        return clientSlaveAPI.getSlaveNodeId();
    }

}
