/********************************************************************************
 * Copyright (c) 2020 Calypso Networks Association https://www.calypsonet-asso.org/
 ********************************************************************************/
package org.cna.keyple.demo.sale.android.nfc.slave.data.method;

import org.cna.keyple.demo.sale.android.nfc.slave.data.KeypleSlaveAPI;
import android.os.AsyncTask;


public class DisconnectAllReaderAsyncTask extends AsyncTask<Object, Integer, Void> {

    KeypleSlaveAPI cardReaderAPI;
    DisconnectReaderAsyncResponse callback;
    String[] readerNames;

    public DisconnectAllReaderAsyncTask(KeypleSlaveAPI cardReaderAPI, String[] readerNames,
            DisconnectReaderAsyncResponse callback) {
        this.cardReaderAPI = cardReaderAPI;
        this.callback = callback;
        this.readerNames = readerNames;
    }

    @Override
    protected Void doInBackground(Object... objects) {
        cardReaderAPI.disconnectAll(readerNames);
        return null;
    }

    @Override
    protected void onPostExecute(Void v) {
        callback.isDisconnected();
    }
}
