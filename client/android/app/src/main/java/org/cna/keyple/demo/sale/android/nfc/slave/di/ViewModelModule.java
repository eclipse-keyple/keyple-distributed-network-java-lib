/********************************************************************************
 * Copyright (c) 2020 Calypso Networks Association https://www.calypsonet-asso.org/
 ********************************************************************************/
package org.cna.keyple.demo.sale.android.nfc.slave.di;

import org.cna.keyple.demo.sale.android.nfc.slave.di.scopes.AppScoped;
import org.cna.keyple.demo.sale.android.nfc.slave.viewModels.*;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(CardReaderViewModel.class)
    abstract ViewModel bindCardReaderViewModel(CardReaderViewModel cardReaderViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ConnectionStatusViewModel.class)
    abstract ViewModel bindConnectionStatusViewModel(
            ConnectionStatusViewModel connectionStatusViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(DeviceReaderViewModel.class)
    abstract ViewModel bindDeviceReaderViewModel(DeviceReaderViewModel deviceReaderViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(SettingsViewModel.class)
    abstract ViewModel bindSettingsViewModel(SettingsViewModel settingsViewModel);


    @Binds
    @AppScoped
    abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelFactory factory);
}
