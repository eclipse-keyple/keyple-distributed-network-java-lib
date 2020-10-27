/********************************************************************************
 * Copyright (c) 2020 Calypso Networks Association https://www.calypsonet-asso.org/
 ********************************************************************************/
package org.cna.keyple.demo.sale.android.nfc.slave.ui.activities;

import java.util.Timer;
import java.util.TimerTask;
import javax.inject.Inject;
import org.cna.keyple.demo.sale.android.nfc.slave.R;
import org.cna.keyple.demo.sale.android.nfc.slave.databinding.SplashscreenActivityBinding;
import org.cna.keyple.demo.sale.android.nfc.slave.ui.BaseView;
import org.cna.keyple.demo.sale.android.nfc.slave.viewModels.ConnectionStatusViewModel;
import org.cna.keyple.demo.sale.android.nfc.slave.viewModels.DeviceReaderViewModel;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import dagger.android.support.DaggerAppCompatActivity;

public class SplashScreenActivity extends DaggerAppCompatActivity implements BaseView {

    private static final String SPLASH_TAG = SplashScreenActivity.class.getSimpleName();
    private static final Integer SPLASH_MAX_DELAY_MS = 6000;

    DeviceReaderViewModel deviceReaderViewModel;
    ConnectionStatusViewModel connectionStatusViewModel;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Make sure this is before calling super.onCreate
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen_activity);

        SplashscreenActivityBinding binding =
                DataBindingUtil.setContentView(this, R.layout.splashscreen_activity);
        binding.animation.setAnimation("splashscreen_anim.json");
        binding.animation.playAnimation();

        deviceReaderViewModel =
                ViewModelProviders.of(this, viewModelFactory).get(DeviceReaderViewModel.class);
        connectionStatusViewModel =
                ViewModelProviders.of(this, viewModelFactory).get(ConnectionStatusViewModel.class);

        connectionStatusViewModel.startPolling();

        connectionStatusViewModel.initStatus();

        /*
         * Connect Wizway Device
         */

        connectionStatusViewModel.WizwayDeviceConnect(this.getApplicationContext());


        /*
         * Wait for Wizway Device to be connected
         */
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (!isFinishing()) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }
            }
        }, SPLASH_MAX_DELAY_MS);
    }

    @Override
    public void onResume() {
        super.onResume();
        bindViewModel();
    }

    @Override
    public void onPause() {
        super.onPause();
        unbindViewModel();
    }

    @Override
    public void bindViewModel() {
        connectionStatusViewModel.getDeviceConnection().observe(this, this::nextScreen);
    }

    @Override
    public void unbindViewModel() {
        connectionStatusViewModel.getDeviceConnection().removeObservers(this);
    }

    public void nextScreen(Boolean deviceConnected) {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

}
