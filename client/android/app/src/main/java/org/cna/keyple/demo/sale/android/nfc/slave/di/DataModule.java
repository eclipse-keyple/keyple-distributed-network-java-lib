/********************************************************************************
 * Copyright (c) 2020 Calypso Networks Association https://www.calypsonet-asso.org/
 ********************************************************************************/
package org.cna.keyple.demo.sale.android.nfc.slave.di;

import org.cna.keyple.demo.sale.android.nfc.slave.Application;
import org.cna.keyple.demo.sale.android.nfc.slave.di.scopes.AppScoped;
import android.content.Context;
import android.content.SharedPreferences;
import dagger.Module;
import dagger.Provides;

@Module
public class DataModule {

    private String PREF_NAME = "Keyple-prefs";

    @Provides
    @AppScoped
    SharedPreferences getSharedPreferences(Application app) {
        return app.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
}
