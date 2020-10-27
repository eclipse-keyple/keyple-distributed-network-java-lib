/********************************************************************************
 * Copyright (c) 2020 Calypso Networks Association https://www.calypsonet-asso.org/
 ********************************************************************************/
package org.cna.keyple.demo.sale.android.nfc.slave.data.wizway;

import org.cna.keyple.demo.sale.android.nfc.slave.data.http.client.slave.KeypleSlaveService;
import org.eclipse.keyple.core.seproxy.SeProxyService;
import org.eclipse.keyple.core.seproxy.SeReader;
import org.eclipse.keyple.core.seproxy.exception.KeypleException;
import org.eclipse.keyple.core.seproxy.exception.KeyplePluginNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import android.content.Context;


public class WizwaySlaveService extends KeypleSlaveService {

    private static final Logger logger = LoggerFactory.getLogger(WizwaySlaveService.class);

    private static final int navigoNfcId = 10015004;
    // private static final String packageName = "org.keyple.examples.wizway";
    private static final String packageName = "org.cna.keyple.demo.sale.android.nfc.slave";

    //private AndroidWizwayPlugin wizwayPlugin;

    public static final String OPTION_SELECTION_EXPLICIT = "option_selection_explicit";


    @Override
    public void initReader() {

//        try {
//            logger.info("Configure Wizway Plugin...");
//            SeProxyService.getInstance().registerPlugin(new AndroidWizwayFactory());
//
//            wizwayPlugin = (AndroidWizwayPlugin) SeProxyService.getInstance()
//                    .getPlugin(AndroidWizwayPlugin.NAME);
//
//            logger.debug("Set NFC_SERVICE_ID as a parameter of plugin");
//
//            wizwayPlugin.setParameter(AndroidWizwayPlugin.NFC_SERVICE_ID,
//                    Integer.toString(navigoNfcId));
//
//            logger.debug("Set PACKAGE_NAME as a parameter of plugin");
//            wizwayPlugin.setParameter(AndroidWizwayPlugin.PACKAGE_NAME, packageName);
//
//        } catch (KeyplePluginNotFoundException e) {
//            e.printStackTrace();
//        } catch (KeypleBaseException e) {
//            e.printStackTrace();
//        }

    }


    @Override
    public SeReader getReader() {
//        if (wizwayPlugin.getReaders().size() > 0) {
//            return wizwayPlugin.getReaders().first();
//        } else {
            return null;
//        }


    }

    //public AndroidWizwayPlugin getWizwayPlugin() {
        //return wizwayPlugin;
    //}

    public void connectDevice(Context context) {

        //wizwayPlugin.bindWizway(context);
    }

    public void disconnectDevice(Context context)
    {
        //wizwayPlugin.unbindWizway(context);
    }
}
