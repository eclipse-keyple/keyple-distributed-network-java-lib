/********************************************************************************
 * Copyright (c) 2020 Calypso Networks Association https://www.calypsonet-asso.org/
 ********************************************************************************/
package org.cna.keyple.demo.sale.android.nfc.slave.di;

import org.cna.keyple.demo.sale.android.nfc.slave.di.scopes.ActivityScoped;
import org.cna.keyple.demo.sale.android.nfc.slave.di.scopes.FragmentScoped;
import org.cna.keyple.demo.sale.android.nfc.slave.ui.activities.MainActivity;
import org.cna.keyple.demo.sale.android.nfc.slave.ui.activities.SplashScreenActivity;
import org.cna.keyple.demo.sale.android.nfc.slave.ui.fragments.CardReaderFragment;
import org.cna.keyple.demo.sale.android.nfc.slave.ui.fragments.CardSummaryFragment;
import org.cna.keyple.demo.sale.android.nfc.slave.ui.fragments.ChargeCardFragment;
import org.cna.keyple.demo.sale.android.nfc.slave.ui.fragments.ChargeDeviceFragment;
import org.cna.keyple.demo.sale.android.nfc.slave.ui.fragments.DeviceStatusFragment;
import org.cna.keyple.demo.sale.android.nfc.slave.ui.fragments.PaymentValidatedFragment;
import org.cna.keyple.demo.sale.android.nfc.slave.ui.fragments.SelectDeviceFragment;
import org.cna.keyple.demo.sale.android.nfc.slave.ui.fragments.SettingsFragment;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class UIModule {
    @ActivityScoped
    @ContributesAndroidInjector
    abstract MainActivity mainActivity();

    @ActivityScoped
    @ContributesAndroidInjector
    abstract SplashScreenActivity splashScreenActivity();

    @FragmentScoped
    @ContributesAndroidInjector
    abstract SelectDeviceFragment selectDeviceFragment();

    @FragmentScoped
    @ContributesAndroidInjector
    abstract CardReaderFragment cardReaderFragment();

    @FragmentScoped
    @ContributesAndroidInjector
    abstract DeviceStatusFragment deviceStatusFragment();

    @FragmentScoped
    @ContributesAndroidInjector
    abstract CardSummaryFragment cardSummaryFragment();

    @FragmentScoped
    @ContributesAndroidInjector
    abstract PaymentValidatedFragment paymentValidatedFragment();

    @FragmentScoped
    @ContributesAndroidInjector
    abstract ChargeCardFragment chargeCardFragment();

    @FragmentScoped
    @ContributesAndroidInjector
    abstract ChargeDeviceFragment chargeDeviceFragment();

    @FragmentScoped
    @ContributesAndroidInjector
    abstract SettingsFragment settingsFragment();
}
