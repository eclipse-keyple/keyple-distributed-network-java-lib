/********************************************************************************
 * Copyright (c) 2020 Calypso Networks Association https://www.calypsonet-asso.org/
 ********************************************************************************/
package org.cna.keyple.demo.sale.android.nfc.slave.util;

import org.cna.keyple.demo.sale.android.nfc.slave.TestApp;
import android.app.Application;
import android.content.Context;
import android.support.test.runner.AndroidJUnitRunner;

public class TestRunner extends AndroidJUnitRunner {
    @Override
    public Application newApplication(ClassLoader cl, String className, Context context)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        return super.newApplication(cl, TestApp.class.getName(), context);
    }
}
