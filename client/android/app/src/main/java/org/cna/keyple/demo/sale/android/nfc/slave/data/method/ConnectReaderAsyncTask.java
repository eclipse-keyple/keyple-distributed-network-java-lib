/********************************************************************************
 * Copyright (c) 2020 Calypso Networks Association https://www.calypsonet-asso.org/
 ********************************************************************************/
package org.cna.keyple.demo.sale.android.nfc.slave.data.method;

import java.util.Map;
import org.cna.keyple.demo.sale.android.nfc.slave.data.KeypleSlaveAPI;
import org.cna.keyple.demo.sale.android.nfc.slave.data.http.common.ReaderState;
import org.eclipse.keyple.core.seproxy.SeReader;
import android.os.AsyncTask;
import retrofit2.Callback;


public class ConnectReaderAsyncTask extends AsyncTask<Object, Integer, Void> {

    KeypleSlaveAPI cardReaderAPI;
    Callback<ReaderState> callback;
    SeReader seReader;
    Map<String, String> options;

    public ConnectReaderAsyncTask(KeypleSlaveAPI cardReaderAPI, SeReader seReader,
            Map<String, String> options, Callback<ReaderState> callback) {
        this.cardReaderAPI = cardReaderAPI;
        this.callback = callback;
        this.seReader = seReader;
        this.options = options;
    }

    @Override
    protected Void doInBackground(Object... objects) {
        cardReaderAPI.connectReader(seReader, options, callback);
        return null;
    }

}
