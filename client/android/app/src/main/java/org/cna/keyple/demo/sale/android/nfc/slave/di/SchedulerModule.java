/********************************************************************************
 * Copyright (c) 2020 Calypso Networks Association https://www.calypsonet-asso.org/
 ********************************************************************************/
package org.cna.keyple.demo.sale.android.nfc.slave.di;

import org.cna.keyple.demo.sale.android.nfc.slave.di.scopes.AppScoped;
import org.cna.keyple.demo.sale.android.nfc.slave.rx.SchedulerProvider;
import dagger.Module;
import dagger.Provides;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Module
public class SchedulerModule {
    @Provides
    @AppScoped
    public SchedulerProvider provideSchedulerProvider() {
        return new SchedulerProvider(Schedulers.io(), AndroidSchedulers.mainThread());
    }
}
