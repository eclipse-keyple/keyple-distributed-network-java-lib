/********************************************************************************
 * Copyright (c) 2020 Calypso Networks Association https://www.calypsonet-asso.org/
 ********************************************************************************/
package org.cna.keyple.demo.sale.android.nfc.slave.di;

import org.cna.keyple.demo.sale.android.nfc.slave.Application;
import android.content.Context;
import dagger.Binds;
import dagger.Module;

@Module
public abstract class AppModule {
    // expose Application as an injectable context
    @Binds
    abstract Context bindContext(Application application);
}
