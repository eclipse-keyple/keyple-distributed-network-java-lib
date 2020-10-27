/********************************************************************************
 * Copyright (c) 2020 Calypso Networks Association https://www.calypsonet-asso.org/
 ********************************************************************************/
package org.cna.keyple.demo.sale.android.nfc.slave.data.http.client.slave;

import org.eclipse.keyple.core.seproxy.SeReader;
import org.eclipse.keyple.core.seproxy.exception.KeypleException;

/*
 * Configure Keyple Service
 */
public abstract class KeypleSlaveService {


    abstract public SeReader getReader() throws KeypleException;

    abstract public void initReader() throws KeypleException;


}
