/********************************************************************************
 * Copyright (c) 2020 Calypso Networks Association https://www.calypsonet-asso.org/
 ********************************************************************************/
package org.cna.keyple.demo.sale.android.nfc.slave.data.nfc;

import android.app.Activity;

import org.cna.keyple.demo.sale.android.nfc.slave.data.http.client.slave.KeypleSlaveService;
import org.eclipse.keyple.core.seproxy.SeProxyService;
import org.eclipse.keyple.core.seproxy.SeReader;
import org.eclipse.keyple.core.seproxy.event.ObservableReader;
import org.eclipse.keyple.core.seproxy.exception.KeypleException;
import org.eclipse.keyple.core.seproxy.protocol.SeCommonProtocols;
import org.eclipse.keyple.plugin.android.nfc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import androidx.fragment.app.Fragment;


public class AndroidNfcKeypleService extends KeypleSlaveService {

    private static final Logger logger = LoggerFactory.getLogger(AndroidNfcKeypleService.class);

    private AndroidNfcReader reader;

//    public static final String TAG_NFC_ANDROID_FRAGMENT =
//            "org.eclipse.keyple.plugin.android.nfc.AndroidNfcFragment";

    public static final String NFC_READER_NAME = "AndroidNfcReaderImpl";

    @Override
    public void initReader() {

        logger.info("Configure NFC Plugin...");

        SeProxyService seProxyService = SeProxyService.getInstance();

        try {

            // register plugin
            seProxyService.registerPlugin(new AndroidNfcPluginFactory());

            AndroidNfcPlugin nfcPlugin =
                    (AndroidNfcPlugin) seProxyService.getPlugin(AndroidNfcPlugin.PLUGIN_NAME);
            reader = (AndroidNfcReader) nfcPlugin.getReaders().values().toArray()[0];


            logger.info("FLAG_READER_PRESENCE_CHECK_DELAY: {}", 1000);
            logger.info("FLAG_READER_NO_PLATFORM_SOUNDS: {}", 1);
            logger.info("FLAG_READER_SKIP_NDEF_CHECK: {}", 1);

            reader.setParameter("FLAG_READER_PRESENCE_CHECK_DELAY", "1000");
            reader.setParameter("FLAG_READER_NO_PLATFORM_SOUNDS", "1");
            reader.setParameter("FLAG_READER_SKIP_NDEF_CHECK", "1");

            /*
             * Listen for protocols
             */
            reader.addSeProtocolSetting(SeCommonProtocols.PROTOCOL_ISO14443_4,
                    AndroidNfcProtocolSettings.INSTANCE.getSetting(SeCommonProtocols.PROTOCOL_ISO14443_4));

            reader.addSeProtocolSetting(SeCommonProtocols.PROTOCOL_MIFARE_UL,
                    AndroidNfcProtocolSettings.INSTANCE.getSetting(SeCommonProtocols.PROTOCOL_MIFARE_UL));

            reader.addSeProtocolSetting(SeCommonProtocols.PROTOCOL_MIFARE_CLASSIC,
                    AndroidNfcProtocolSettings.INSTANCE.getSetting(SeCommonProtocols.PROTOCOL_MIFARE_CLASSIC));

        } catch (KeypleException e) {
            e.printStackTrace();
        }


    }

    @Override
    public SeReader getReader() {
        return reader;
    }

    /**
     * Start the NFC detection
     * @param activity
     */
    public void startNfcDetection(Activity activity){
        reader.enableNFCReaderMode(activity);
        reader.startSeDetection(ObservableReader.PollingMode.REPEATING);
    }

    /**
     * End the NFC detection
     * @param activity
     */
    public void stopNfcDetection(Activity activity){
        reader.stopSeDetection();
        reader.disableNFCReaderMode(activity);
    }
}
