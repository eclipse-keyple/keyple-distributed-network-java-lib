/********************************************************************************
 * Copyright (c) 2020 Calypso Networks Association https://www.calypsonet-asso.org/
 ********************************************************************************/
package org.cna.keyple.demo.sale.android.nfc.slave.data.http.client.slave;


import org.cna.keyple.demo.sale.android.nfc.slave.data.http.client.reader.ReaderStateClient;
import org.cna.keyple.demo.sale.android.nfc.slave.data.http.client.reader.ReaderStateClientFactory;
import org.cna.keyple.demo.sale.android.nfc.slave.data.http.common.ReaderState;
import org.cna.keyple.demo.sale.android.nfc.slave.data.remote.transport.wspolling.client_retrofit.WsPRetrofitClientImpl;
import org.cna.keyple.demo.sale.android.nfc.slave.data.remote.transport.wspolling.client_retrofit.WsPollingFactory;
import org.eclipse.keyple.core.seproxy.SeProxyService;
import org.eclipse.keyple.core.seproxy.SeReader;
import org.eclipse.keyple.core.seproxy.exception.KeypleException;
import org.eclipse.keyple.core.seproxy.exception.KeypleReaderException;
import org.eclipse.keyple.plugin.remotese.nativese.SlaveAPI;
import org.eclipse.keyple.plugin.remotese.transport.factory.ClientNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.Observer;

import retrofit2.Callback;

public class ClientSlaveAPI {

    private static final Logger logger = LoggerFactory.getLogger(ClientSlaveAPI.class);

    // Web client for reader status
    private final ReaderStateClient readerStateClient;

    // TransportNode used as to send and receive KeypleDto to Master
    private final WsPRetrofitClientImpl clientNode;

    // NativeReaderServiceImpl, used to connectReader and disconnect readers
    private final SlaveAPI slaveAPI;


    /**
     * At startup, create a client
     *
     * @param : factory to get the type of transport needed (websocket, ClientSlaveAPI
     *        webservice...)
     */
    public ClientSlaveAPI(WsPollingFactory transportFactory, String masterNodeId,
                          String slaveNodeId) throws KeypleException {
        logger.info("Creating CardReader API");
        clientNode = (WsPRetrofitClientImpl) transportFactory.getClient(slaveNodeId);

        this.slaveAPI = new SlaveAPI(SeProxyService.getInstance(), clientNode, masterNodeId);

        readerStateClient =
                ReaderStateClientFactory.getRetrofitClient(transportFactory.getBaseUrl());
    }

    /**
     * Start polling and observe polling client
     */
    public void startPolling(Observer isPolling) {

        /*
         * Only one observer
         */
        if (clientNode.countObservers() == 0) {
            clientNode.addObserver(isPolling);
        }

        /**
         * Activate Poll if not activated already
         */
        if (!clientNode.getPollActivated()) {
            logger.info("StartPolling - Connect client to server");
            clientNode.connect(new ClientNode.ConnectCallback() {
                @Override
                public void onConnectSuccess() {
                    // logger.trace("Client Connection to server succesfull");
                }

                @Override
                public void onConnectFailure() {

                    logger.error("Failed connection client to server");


                }
            });
        }
    }

    /**
     * Stop polling
     */
    public void stopPolling() {
        clientNode.deleteObservers();
        clientNode.disconnect();
    }

    public void isPollActivated() {
        clientNode.getPollActivated();
    }

    public Boolean isPolling() {
        return clientNode.isPolling();
    }


    /**
     * connects it to the Master terminal via the SlaveAPI Publish a new state
     *
     * @throws KeypleReaderException
     * @throws InterruptedException
     */
    public void connectReader(SeReader reader, Map<String, String> options,
            Callback<ReaderState> readerStateCallback) {

        // connect a reader to Remote Plugin
        logger.info("Connect remotely the Native Reader {} with options {}", reader.getName(),
                options);
        try {
            slaveAPI.connectReader(reader, options);
            getUpdatedReaderStateAsync(reader.getName(), readerStateCallback);

        } catch (KeypleReaderException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /*
     * public void asyncConnectReader(ConnectReaderAsyncResponse callback){ // connect a reader to
     * Remote Plugin Log.i(TAG,"Connect remotely the Native Reader "); new
     * ConnectReaderAsyncTask(slaveAPI,callback) .execute(reader, clientNode.getNodeId()); }
     * 
     * public void asyncDisconnectReader(DisconnectReaderAsyncResponse callback){
     * Log.i(TAG,"Disconnect native reader"); new DisconnectReaderAsyncTask(slaveAPI , callback)
     * .execute("", reader.getName(), clientNode.getNodeId());
     * reader.removeObserver(poReaderObserver); }
     */


    /**
     * Get a reader state (will wait for a new reader state to be published by the server)
     *
     * @return
     * @throws IOException
     */
    public ReaderState getUpdatedReaderState(SeReader reader) throws IOException {
        return readerStateClient.getReaderState(clientNode.getNodeId(), reader.getName(), true)
                .execute().body();
    }

    public void getUpdatedReaderStateAsync(String nativeReaderName, Callback<ReaderState> callback)
            throws IOException {
        logger.debug("Get update reader state async...");
        readerStateClient.getReaderState(clientNode.getNodeId(), nativeReaderName, true)
                .enqueue(callback);
    }


    /**
     * disconnect does not publish a new state
     * 
     * @param nativeReaderName
     * @throws KeypleReaderException
     */
    public void disconnect(String nativeReaderName) throws KeypleReaderException {
        logger.info("Disconnect native reader {}", nativeReaderName);
        slaveAPI.disconnectReader("", nativeReaderName);
    }

    public String getSlaveNodeId() {
        return clientNode.getNodeId();
    }

}
