/********************************************************************************
 * Copyright (c) 2020 Calypso Networks Association https://www.calypsonet-asso.org/
 ********************************************************************************/
package org.cna.keyple.demo.sale.android.nfc.slave.ui.activities;

import javax.inject.Inject;
import org.cna.keyple.demo.sale.android.nfc.slave.R;
import org.cna.keyple.demo.sale.android.nfc.slave.data.KeypleSlaveAPI;
import org.cna.keyple.demo.sale.android.nfc.slave.ui.BaseView;
import org.cna.keyple.demo.sale.android.nfc.slave.ui.OnBackPressedListener;
import org.cna.keyple.demo.sale.android.nfc.slave.viewModels.ConnectionStatusViewModel;
import android.os.Bundle;

import androidx.annotation.VisibleForTesting;
import androidx.appcompat.widget.Toolbar;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import dagger.android.support.DaggerAppCompatActivity;
import timber.log.Timber;

@VisibleForTesting
public class MainActivity extends DaggerAppCompatActivity implements BaseView {

    private final static String TAG = MainActivity.class.getSimpleName();

    NavController navController;
    NavHostFragment navHostFragment;

    ImageView menuBtn, closeMenu, serverStatus, deviceStatus;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    ConnectionStatusViewModel connectionStatusViewModel;

    @Inject
    KeypleSlaveAPI keypleSlaveAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup custom toolbar as main action bar
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        myToolbar.setTitle("");
        setSupportActionBar(myToolbar);
        myToolbar.setNavigationIcon(R.drawable.ic_back);

        menuBtn = findViewById(R.id.menu_btn);
        closeMenu = findViewById(R.id.close_menu_btn);
        serverStatus = findViewById(R.id.server_status);
        deviceStatus = findViewById(R.id.device_status);

        // Configure up navigation in toolbar
        navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupWithNavController(myToolbar, navController, appBarConfiguration);

        menuBtn.setOnClickListener(v -> {
            navController.navigate(R.id.action_menu);
        });

        closeMenu.setOnClickListener(v -> {
            onBackPressed();
        });

        connectionStatusViewModel =
                ViewModelProviders.of(this, viewModelFactory).get(ConnectionStatusViewModel.class);

    }

    @Override
    public void onResume() {
        super.onResume();
        connectionStatusViewModel.startPolling();
        bindViewModel();
    }

    @Override
    public void onPause() {
        super.onPause();
        connectionStatusViewModel.stopPolling();
        unbindViewModel();
    }

    @Override
    public void onBackPressed() {
        final Fragment currentFragment =
                navHostFragment.getChildFragmentManager().getFragments().get(0);
        final NavController controller = Navigation.findNavController(this, R.id.nav_host_fragment);
        if (currentFragment instanceof OnBackPressedListener)
            ((OnBackPressedListener) currentFragment).onBackPressed();
        else if (!controller.popBackStack())
            finish();
    }

    @Override
    protected void onDestroy() {
        connectionStatusViewModel.disconnectAll();
        super.onDestroy();
    }

    @Override
    public void bindViewModel() {
        connectionStatusViewModel.getServerConnection().observe(this, connection -> {
            if (connection) {
                serverStatus
                        .setImageDrawable(getResources().getDrawable(R.drawable.ic_connection_ok));
            } else {
                serverStatus
                        .setImageDrawable(getResources().getDrawable(R.drawable.ic_connection_nok));
            }
        });
        connectionStatusViewModel.getDeviceConnection().observe(this, connection -> {
            if (connection) {
                deviceStatus
                        .setImageDrawable(getResources().getDrawable(R.drawable.ic_connection_ok));
            } else {
                deviceStatus
                        .setImageDrawable(getResources().getDrawable(R.drawable.ic_connection_nok));
            }
        });
    }

    @Override
    public void unbindViewModel() {
        connectionStatusViewModel.getServerConnection().removeObservers(this);
        connectionStatusViewModel.getDeviceConnection().removeObservers(this);
    }
}
