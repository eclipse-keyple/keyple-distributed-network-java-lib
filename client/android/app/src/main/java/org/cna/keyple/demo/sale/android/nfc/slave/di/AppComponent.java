/********************************************************************************
 * Copyright (c) 2020 Calypso Networks Association https://www.calypsonet-asso.org/
 ********************************************************************************/
package org.cna.keyple.demo.sale.android.nfc.slave.di;

import org.cna.keyple.demo.sale.android.nfc.slave.Application;
import org.cna.keyple.demo.sale.android.nfc.slave.di.scopes.AppScoped;
import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

@AppScoped
@Component(modules = {ViewModelModule.class, AppModule.class, UIModule.class, SchedulerModule.class,
        AndroidSupportInjectionModule.class, RetrofitModule.class, DataModule.class})
public interface AppComponent extends AndroidInjector<Application> {

    @Component.Builder
    interface Builder {
        @BindsInstance
        AppComponent.Builder application(Application application);

        AppComponent build();
    }
}
